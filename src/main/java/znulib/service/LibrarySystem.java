package znulib.service;

import znulib.model.Book;
import znulib.model.Employee;
import znulib.model.Loan;
import znulib.model.LoanRequest;
import znulib.model.Manager;
import znulib.model.Student;
import znulib.storage.DataStore;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// سیستم اصلی کتابخانه
public class LibrarySystem {
    private List<Student> students;
    private List<Employee> employees;
    private List<Book> books;
    private List<LoanRequest> loanRequests;
    private List<Loan> loans;
    private Manager manager;

    public LibrarySystem() {
        this.students = new ArrayList<>();
        this.employees = new ArrayList<>();
        this.books = new ArrayList<>();
        this.loanRequests = new ArrayList<>();
        this.loans = new ArrayList<>();
        
        // ایجاد مدیر پیش‌فرض
        this.manager = new Manager("admin", "admin123");
        
        // ایجاد داده‌های نمونه
        if (DataStore.exists()) {
            DataStore.load(this);
        } else {
            initializeSampleData();
            DataStore.save(this);
        }
    }

    private void initializeSampleData() {
        // ایجاد کارمند نمونه
        Employee emp1 = manager.createEmployee("emp1", "123", "کارمند نمونه");
        employees.add(emp1);
        
        // ایجاد دانشجویان نمونه
        students.add(new Student("stu1", "123", "دانشجو یک", "S001"));
        students.add(new Student("stu2", "123", "دانشجو دو", "S002"));
        
        // ایجاد کتاب‌های نمونه
        books.add(emp1.registerBook("آموزش جاوا", "نویسنده یک", 2023, "111-1111111111"));
        books.add(emp1.registerBook("پایگاه داده", "نویسنده دو", 2022, "222-2222222222"));
        books.add(emp1.registerBook("هوش مصنوعی", "نویسنده سه", 2024, "333-3333333333"));
        
        // ایجاد برخی درخواست‌های امانت نمونه
        Student student1 = students.get(0);
        Book book1 = books.get(0);
        LoanRequest request = new LoanRequest(student1, book1, LocalDate.now(), LocalDate.now().plusDays(14));
        loanRequests.add(request);
    }

    // متدهای اصلی سیستم
    public Student registerStudent(String username, String password, String name, String studentId) {
        Student student = new Student(username, password, name, studentId);
        students.add(student);
        return student;
    }

    public Student studentLogin(String username, String password) {
        for (Student student : students) {
            if (student.login(username, password)) {
                return student;
            }
        }
        return null;
    }

    public Employee employeeLogin(String username, String password) {
        for (Employee employee : employees) {
            if (employee.login(username, password)) {
                return employee;
            }
        }
        return null;
    }

    public List<Book> searchBooks(String title, Integer year, String author) {
        List<Book> result = new ArrayList<>();
        for (Book book : books) {
            boolean matches = true;
            
            if (title != null && !book.getTitle().toLowerCase().contains(title.toLowerCase())) {
                matches = false;
            }
            
            if (year != null && book.getPublicationYear() != year) {
                matches = false;
            }
            
            if (author != null && !book.getAuthor().toLowerCase().contains(author.toLowerCase())) {
                matches = false;
            }
            
            if (matches) {
                result.add(book);
            }
        }
        return result;
    }

    public List<Book> searchBooksByTitle(String title) {
        List<Book> result = new ArrayList<>();
        for (Book book : books) {
            if (book.getTitle().toLowerCase().contains(title.toLowerCase())) {
                result.add(book);
            }
        }
        return result;
    }

    public void addLoanRequest(LoanRequest request) {
        loanRequests.add(request);
    }

    public List<LoanRequest> getPendingLoanRequests() {
        List<LoanRequest> pending = new ArrayList<>();
        for (LoanRequest request : loanRequests) {
            if (!request.isApproved()) {
                pending.add(request);
            }
        }
        return pending;
    }

    public Loan approveLoanRequest(LoanRequest request, Employee employee) {
        if (employee.approveLoanRequest(request)) {
            request.setApproved(true);
            Loan loan = new Loan(request, employee);
            loans.add(loan);
            return loan;
        }
        return null;
    }

    // Getter methods
    public int getStudentCount() { return students.size(); }
    public int getBookCount() { return books.size(); }
    public int getLoanCount() { return loans.size(); }
    public int getCurrentLoansCount() { 
        int count = 0;
        for (Loan loan : loans) {
            if (!loan.isReturned()) {
                count++;
            }
        }
        return count;
    }
    
    public List<Student> getStudents() { return new ArrayList<>(students); }
    public List<Book> getBooks() { return new ArrayList<>(books); }
    public List<LoanRequest> getLoanRequests() { return new ArrayList<>(loanRequests); }
    public List<Loan> getLoans() { return new ArrayList<>(loans); }
    public Manager getManager() { return manager; }
    
    public void addEmployee(Employee employee) { employees.add(employee); }
    public void addBook(Book book) { books.add(book); }
    public void addStudent(Student student) { students.add(student); }
    public void addLoan(Loan loan) { loans.add(loan); }
    public List<Employee> getEmployees() { return new ArrayList<>(employees); }
}
