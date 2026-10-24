package znulib;

import java.time.LocalDate;

// کلاس درخواست امانت
public class LoanRequest {
    private static int nextId = 1;
    
    private int id;
    private Student student;
    private Book book;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean approved;

    public LoanRequest(Student student, Book book, LocalDate startDate, LocalDate endDate) {
        this.id = nextId++;
        this.student = student;
        this.book = book;
        this.startDate = startDate;
        this.endDate = endDate;
        this.approved = false;
    }

    // Getter methods
    public int getId() { return id; }
    public Student getStudent() { return student; }
    public Book getBook() { return book; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }
    
    @Override
    public String toString() {
        return String.format("درخواست #%d | دانشجو: %s | کتاب: %s | تاریخ شروع: %s | وضعیت: %s", 
            id, student.getName(), book.getTitle(), startDate, approved ? "تایید شده" : "در انتظار");
    }
}
