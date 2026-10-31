package znulib.storage;

import znulib.model.Book;
import znulib.model.Employee;
import znulib.model.Loan;
import znulib.model.LoanRequest;
import znulib.model.Manager;
import znulib.model.Student;
import znulib.service.LibrarySystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// ذخیره و بازیابی اطلاعات در پایگاه داده SQLite (وابستگی org.xerial:sqlite-jdbc در pom.xml)
public class DataStore {
    private static final String URL = "jdbc:sqlite:library.db";

    private static Connection connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                "درایور SQLite پیدا نشد؛ برنامه را با Maven یا jar ساخته‌شده (java -jar target/library-system.jar) اجرا کنید.", e);
        }
        Connection c = DriverManager.getConnection(URL);
        try (Statement st = c.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
            st.execute("CREATE TABLE IF NOT EXISTS manager (username TEXT NOT NULL, password TEXT NOT NULL)");
            st.execute("CREATE TABLE IF NOT EXISTS students (id INTEGER PRIMARY KEY, username TEXT NOT NULL, "
                + "password TEXT NOT NULL, name TEXT NOT NULL, student_id TEXT NOT NULL, active INTEGER NOT NULL)");
            st.execute("CREATE TABLE IF NOT EXISTS employees (id INTEGER PRIMARY KEY, username TEXT NOT NULL, "
                + "password TEXT NOT NULL, name TEXT NOT NULL, active INTEGER NOT NULL, "
                + "books_registered INTEGER NOT NULL, books_loaned_out INTEGER NOT NULL, books_received INTEGER NOT NULL)");
            st.execute("CREATE TABLE IF NOT EXISTS books (id INTEGER PRIMARY KEY, title TEXT NOT NULL, author TEXT NOT NULL, "
                + "publication_year INTEGER NOT NULL, isbn TEXT NOT NULL, registered_by INTEGER REFERENCES employees(id))");
            st.execute("CREATE TABLE IF NOT EXISTS loan_requests (id INTEGER PRIMARY KEY, "
                + "student_id INTEGER NOT NULL REFERENCES students(id), book_id INTEGER NOT NULL REFERENCES books(id), "
                + "start_date TEXT NOT NULL, end_date TEXT NOT NULL, approved INTEGER NOT NULL)");
            st.execute("CREATE TABLE IF NOT EXISTS loans (id INTEGER PRIMARY KEY, "
                + "student_id INTEGER NOT NULL REFERENCES students(id), book_id INTEGER NOT NULL REFERENCES books(id), "
                + "approved_by INTEGER REFERENCES employees(id), start_date TEXT NOT NULL, end_date TEXT NOT NULL, "
                + "return_date TEXT)");
        }
        return c;
    }

    // آیا قبلاً اطلاعاتی در پایگاه داده ذخیره شده است؟
    public static boolean exists() {
        try (Connection c = connect();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM manager")) {
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.out.println("خطا در دسترسی به پایگاه داده: " + e.getMessage());
            return false;
        }
    }

    // کل وضعیت سیستم در یک تراکنش بازنویسی می‌شود
    public static void save(LibrarySystem s) {
        try (Connection c = connect()) {
            c.setAutoCommit(false);
            try {
                try (Statement st = c.createStatement()) {
                    st.execute("DELETE FROM loans");
                    st.execute("DELETE FROM loan_requests");
                    st.execute("DELETE FROM books");
                    st.execute("DELETE FROM employees");
                    st.execute("DELETE FROM students");
                    st.execute("DELETE FROM manager");
                }
                write(c, s);
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("خطا در ذخیره اطلاعات: " + e.getMessage());
        }
    }

    private static void write(Connection c, LibrarySystem s) throws SQLException {
        Manager m = s.getManager();
        try (PreparedStatement ps = c.prepareStatement("INSERT INTO manager VALUES (?, ?)")) {
            ps.setString(1, m.getUsername());
            ps.setString(2, m.getPassword());
            ps.executeUpdate();
        }

        Map<Student, Integer> studentIds = new HashMap<>();
        try (PreparedStatement ps = c.prepareStatement("INSERT INTO students VALUES (?, ?, ?, ?, ?, ?)")) {
            int id = 1;
            for (Student st : s.getStudents()) {
                studentIds.put(st, id);
                ps.setInt(1, id++);
                ps.setString(2, st.getUsername());
                ps.setString(3, st.getPassword());
                ps.setString(4, st.getName());
                ps.setString(5, st.getStudentId());
                ps.setInt(6, st.isActive() ? 1 : 0);
                ps.executeUpdate();
            }
        }

        Map<Employee, Integer> employeeIds = new HashMap<>();
        try (PreparedStatement ps = c.prepareStatement("INSERT INTO employees VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
            int id = 1;
            for (Employee e : s.getEmployees()) {
                employeeIds.put(e, id);
                ps.setInt(1, id++);
                ps.setString(2, e.getUsername());
                ps.setString(3, e.getPassword());
                ps.setString(4, e.getName());
                ps.setInt(5, e.isActive() ? 1 : 0);
                ps.setInt(6, e.getBooksRegistered());
                ps.setInt(7, e.getBooksLoanedOut());
                ps.setInt(8, e.getBooksReceived());
                ps.executeUpdate();
            }
        }

        Map<Book, Integer> bookIds = new HashMap<>();
        try (PreparedStatement ps = c.prepareStatement("INSERT INTO books VALUES (?, ?, ?, ?, ?, ?)")) {
            int id = 1;
            for (Book b : s.getBooks()) {
                bookIds.put(b, id);
                ps.setInt(1, id++);
                ps.setString(2, b.getTitle());
                ps.setString(3, b.getAuthor());
                ps.setInt(4, b.getPublicationYear());
                ps.setString(5, b.getIsbn());
                ps.setObject(6, employeeIds.get(b.getRegisteredBy()));
                ps.executeUpdate();
            }
        }

        try (PreparedStatement ps = c.prepareStatement("INSERT INTO loan_requests VALUES (?, ?, ?, ?, ?, ?)")) {
            for (LoanRequest r : s.getLoanRequests()) {
                ps.setInt(1, r.getId());
                ps.setInt(2, studentIds.get(r.getStudent()));
                ps.setInt(3, bookIds.get(r.getBook()));
                ps.setString(4, r.getStartDate().toString());
                ps.setString(5, r.getEndDate().toString());
                ps.setInt(6, r.isApproved() ? 1 : 0);
                ps.executeUpdate();
            }
        }

        try (PreparedStatement ps = c.prepareStatement("INSERT INTO loans VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            for (Loan l : s.getLoans()) {
                ps.setInt(1, l.getId());
                ps.setInt(2, studentIds.get(l.getStudent()));
                ps.setInt(3, bookIds.get(l.getBook()));
                ps.setObject(4, employeeIds.get(l.getApprovedBy()));
                ps.setString(5, l.getStartDate().toString());
                ps.setString(6, l.getEndDate().toString());
                ps.setString(7, l.getReturnDate() == null ? null : l.getReturnDate().toString());
                ps.executeUpdate();
            }
        }
    }

    public static void load(LibrarySystem s) {
        try (Connection c = connect(); Statement st = c.createStatement()) {
            try (ResultSet rs = st.executeQuery("SELECT password FROM manager")) {
                if (rs.next()) s.getManager().changePassword(rs.getString(1));
            }

            Map<Integer, Student> students = new HashMap<>();
            try (ResultSet rs = st.executeQuery("SELECT * FROM students ORDER BY id")) {
                while (rs.next()) {
                    Student stu = new Student(rs.getString("username"), rs.getString("password"),
                            rs.getString("name"), rs.getString("student_id"));
                    stu.setActive(rs.getInt("active") == 1);
                    s.addStudent(stu);
                    students.put(rs.getInt("id"), stu);
                }
            }

            Map<Integer, Employee> employees = new HashMap<>();
            try (ResultSet rs = st.executeQuery("SELECT * FROM employees ORDER BY id")) {
                while (rs.next()) {
                    Employee e = new Employee(rs.getString("username"), rs.getString("password"), rs.getString("name"));
                    e.setActive(rs.getInt("active") == 1);
                    e.restoreCounters(rs.getInt("books_registered"), rs.getInt("books_loaned_out"),
                            rs.getInt("books_received"));
                    s.addEmployee(e);
                    employees.put(rs.getInt("id"), e);
                }
            }

            Map<Integer, Book> books = new HashMap<>();
            try (ResultSet rs = st.executeQuery("SELECT * FROM books ORDER BY id")) {
                while (rs.next()) {
                    Book b = new Book(rs.getString("title"), rs.getString("author"),
                            rs.getInt("publication_year"), rs.getString("isbn"),
                            employees.get(rs.getInt("registered_by")));
                    s.addBook(b);
                    books.put(rs.getInt("id"), b);
                }
            }

            try (ResultSet rs = st.executeQuery("SELECT * FROM loan_requests ORDER BY id")) {
                while (rs.next()) {
                    s.addLoanRequest(new LoanRequest(rs.getInt("id"), students.get(rs.getInt("student_id")),
                            books.get(rs.getInt("book_id")), LocalDate.parse(rs.getString("start_date")),
                            LocalDate.parse(rs.getString("end_date")), rs.getInt("approved") == 1));
                }
            }

            try (ResultSet rs = st.executeQuery("SELECT * FROM loans ORDER BY id")) {
                while (rs.next()) {
                    Loan l = new Loan(rs.getInt("id"), students.get(rs.getInt("student_id")),
                            books.get(rs.getInt("book_id")), employees.get(rs.getInt("approved_by")),
                            LocalDate.parse(rs.getString("start_date")), LocalDate.parse(rs.getString("end_date")));
                    String returned = rs.getString("return_date");
                    if (returned != null) l.setReturnDate(LocalDate.parse(returned));
                    s.addLoan(l);
                }
            }
        } catch (SQLException e) {
            System.out.println("خطا در خواندن اطلاعات: " + e.getMessage());
        }
    }
}
