import java.time.LocalDate;

// کلاس امانت
public class Loan {
    private static int nextId = 1;

    private int id;
    private Student student;
    private Book book;
    private Employee approvedBy;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate returnDate;
    private boolean returned;

    public Loan(LoanRequest request, Employee approvedBy) {
        this.id = nextId++;
        this.student = request.getStudent();
        this.book = request.getBook();
        this.approvedBy = approvedBy;
        this.startDate = request.getStartDate();
        this.endDate = request.getEndDate();
        this.returned = false;
        this.book.setAvailable(false);
        student.addLoanToHistory(this);
    }

    public boolean isOverdue() {
        if (returned) {
            return returnDate.isAfter(endDate);
        }
        return LocalDate.now().isAfter(endDate);
    }

    public long getDaysOverdue() {
        if (isOverdue()) {
            if (returned) {
                return DateUtil.getDaysBetween(endDate, returnDate);
            } else {
                return DateUtil.getDaysBetween(endDate, LocalDate.now());
            }
        }
        return 0;
    }

    // محاسبه مدت زمان امانت
    public long getLoanDuration() {
        LocalDate end = returned ? returnDate : LocalDate.now();
        return DateUtil.getDaysBetween(startDate, end);
    }

    // Getter and Setter methods
    public int getId() { return id; }
    public Student getStudent() { return student; }
    public Book getBook() { return book; }
    public Employee getApprovedBy() { return approvedBy; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
        this.returned = true;
        this.book.setAvailable(true);
    }
    public boolean isReturned() { return returned; }
}