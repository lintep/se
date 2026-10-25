import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// کلاس دانشجو
public class Student extends User {
    private String name;
    private String studentId;
    private List<Loan> loanHistory;

    // 1-1 ثبت نام در سیستم و مشخص کردن نام کاربری و کلمه ورود
    public Student(String username, String password, String name, String studentId) {
        super(username, password);
        this.name = name;
        this.studentId = studentId;
        this.loanHistory = new ArrayList<>();
    }

    // 1-3 جستجوی کتاب بر اساس ترکیبی از مقادیر عنوان کتاب و یا سال نشر و یا نام نویسنده
    public List<Book> searchBooks(LibrarySystem system, String title, Integer year, String author) {
        return system.searchBooks(title, year, author);
    }

    // 1-4 ثبت درخواست امانت کتاب بر اساس بازه زمانی شامل زمان شروع و زمان پایان
    public LoanRequest requestBookLoan(Book book, LocalDate startDate, LocalDate endDate) {
        if (!this.active) {
            throw new IllegalStateException("Student account is not active");
        }
        return new LoanRequest(this, book, startDate, endDate);
    }

    public void addLoanToHistory(Loan loan) {
        loanHistory.add(loan);
    }

    public List<Loan> getLoanHistory() { return loanHistory; }
    public String getName() { return name; }
    public String getStudentId() { return studentId; }
}
