import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// ذخیره و بازیابی اطلاعات در فایل متنی با جداکننده TAB
public class DataStore {
    private static final String FILE = "library_data.txt";

    public static boolean exists() {
        return Files.exists(Paths.get(FILE));
    }

    private static String line(Object... fields) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) sb.append('\t');
            sb.append(String.valueOf(fields[i]).replaceAll("[\\t\\r\\n]", " "));
        }
        return sb.toString();
    }

    public static void save(LibrarySystem s) {
        List<String> out = new ArrayList<>();
        Manager m = s.getManager();
        out.add(line("MANAGER", m.getUsername(), m.password));
        for (Student st : s.getStudents()) {
            out.add(line("STUDENT", st.getUsername(), st.password, st.getName(), st.getStudentId(), st.isActive()));
        }
        for (Employee e : s.getEmployees()) {
            out.add(line("EMPLOYEE", e.getUsername(), e.password, e.getName(), e.isActive(),
                    e.getBooksRegistered(), e.getBooksLoanedOut(), e.getBooksReceived()));
        }
        List<Book> books = s.getBooks();
        for (Book b : books) {
            out.add(line("BOOK", b.getTitle(), b.getAuthor(), b.getPublicationYear(), b.getIsbn(),
                    b.getRegisteredBy().getUsername()));
        }
        for (LoanRequest r : s.getLoanRequests()) {
            out.add(line("REQUEST", r.getId(), r.getStudent().getUsername(), books.indexOf(r.getBook()),
                    r.getStartDate(), r.getEndDate(), r.isApproved()));
        }
        for (Loan l : s.getLoans()) {
            out.add(line("LOAN", l.getId(), l.getStudent().getUsername(), books.indexOf(l.getBook()),
                    l.getApprovedBy().getUsername(), l.getStartDate(), l.getEndDate(),
                    l.getReturnDate() == null ? "" : l.getReturnDate()));
        }
        try {
            Files.write(Paths.get(FILE), out, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("خطا در ذخیره فایل: " + e.getMessage());
        }
    }

    public static void load(LibrarySystem s) {
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(FILE), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("خطا در خواندن فایل: " + e.getMessage());
            return;
        }
        Map<String, Student> students = new HashMap<>();
        Map<String, Employee> employees = new HashMap<>();
        List<Book> books = new ArrayList<>();

        for (String ln : lines) {
            if (ln.trim().isEmpty()) continue;
            String[] f = ln.split("\t", -1);
            switch (f[0]) {
                case "MANAGER":
                    s.getManager().changePassword(f[2]);
                    break;
                case "STUDENT": {
                    Student st = new Student(f[1], f[2], f[3], f[4]);
                    st.setActive(Boolean.parseBoolean(f[5]));
                    s.addStudent(st);
                    students.put(f[1], st);
                    break;
                }
                case "EMPLOYEE": {
                    Employee e = new Employee(f[1], f[2], f[3]);
                    e.setActive(Boolean.parseBoolean(f[4]));
                    e.restoreCounters(Integer.parseInt(f[5]), Integer.parseInt(f[6]), Integer.parseInt(f[7]));
                    s.addEmployee(e);
                    employees.put(f[1], e);
                    break;
                }
                case "BOOK": {
                    Book b = new Book(f[1], f[2], Integer.parseInt(f[3]), f[4], employees.get(f[5]));
                    s.addBook(b);
                    books.add(b);
                    break;
                }
                case "REQUEST": {
                    LoanRequest r = new LoanRequest(Integer.parseInt(f[1]), students.get(f[2]),
                            books.get(Integer.parseInt(f[3])), LocalDate.parse(f[4]),
                            LocalDate.parse(f[5]), Boolean.parseBoolean(f[6]));
                    s.addLoanRequest(r);
                    break;
                }
                case "LOAN": {
                    Loan l = new Loan(Integer.parseInt(f[1]), students.get(f[2]),
                            books.get(Integer.parseInt(f[3])), employees.get(f[4]),
                            LocalDate.parse(f[5]), LocalDate.parse(f[6]));
                    if (!f[7].isEmpty()) l.setReturnDate(LocalDate.parse(f[7]));
                    s.addLoan(l);
                    break;
                }
                default:
                    break;
            }
        }
    }
}
