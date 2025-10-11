import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    }

    // 1-1 ثبت نام در سیستم و مشخص کردن نام کاربری و کلمه ورود
    public Student registerStudent(String username, String password, String name, String studentId) {
        Student student = new Student(username, password, name, studentId);
        students.add(student);
        return student;
    }

    // 1-2 ورود به سیستم
    public Student studentLogin(String username, String password) {
        return students.stream()
            .filter(student -> student.login(username, password))
            .findFirst()
            .orElse(null);
    }

    // 3-1 ورود به سیستم
    public Employee employeeLogin(String username, String password) {
        return employees.stream()
            .filter(employee -> employee.login(username, password))
            .findFirst()
            .orElse(null);
    }

    // 1-3 جستجوی کتاب بر اساس ترکیبی از مقادیر عنوان کتاب و یا سال نشر و یا نام نویسنده
    public List<Book> searchBooks(String title, Integer year, String author) {
        return books.stream()
            .filter(book -> 
                (title == null || book.getTitle().toLowerCase().contains(title.toLowerCase())) &&
                (year == null || book.getPublicationYear() == year) &&
                (author == null || book.getAuthor().toLowerCase().contains(author.toLowerCase())))
            .collect(Collectors.toList());
    }

    // 2-2 جستجو فقط بر اساس نام کتاب
    public List<Book> searchBooksByTitle(String title) {
        return books.stream()
            .filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase()))
            .collect(Collectors.toList());
    }

    // مدیریت درخواست‌های امانت
    public void addLoanRequest(LoanRequest request) {
        loanRequests.add(request);
    }

    public List<LoanRequest> getPendingLoanRequests() {
        return loanRequests.stream()
            .filter(request -> !request.isApproved())
            .collect(Collectors.toList());
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

    // Getter methods برای آمار
    public int getStudentCount() { return students.size(); }
    public int getBookCount() { return books.size(); }
    public int getLoanCount() { return loans.size(); }
    public int getCurrentLoansCount() { 
        return (int) loans.stream().filter(loan -> !loan.isReturned()).count(); 
    }
    
    public List<Student> getStudents() { return new ArrayList<>(students); }
    public List<Book> getBooks() { return new ArrayList<>(books); }
    public List<LoanRequest> getLoanRequests() { return new ArrayList<>(loanRequests); }
    public List<Loan> getLoans() { return new ArrayList<>(loans); }
    public Manager getManager() { return manager; }
    
    public void addEmployee(Employee employee) { employees.add(employee); }
    public void addBook(Book book) { books.add(book); }
}