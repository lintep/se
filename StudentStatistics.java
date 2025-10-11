import java.util.List;

// کلاس آمار دانشجو
public class StudentStatistics {
    private Student student;
    private int totalLoans;
    private int notReturnedCount;
    private int delayedReturnsCount;

    public StudentStatistics(Student student) {
        this.student = student;
        calculateStatistics();
    }

    private void calculateStatistics() {
        List<Loan> loans = student.getLoanHistory();
        this.totalLoans = loans.size();
        this.notReturnedCount = (int) loans.stream().filter(loan -> !loan.isReturned()).count();
        this.delayedReturnsCount = (int) loans.stream().filter(Loan::isOverdue).count();
    }

    // Getter methods
    // 3-6 مشاهده گزارش تاریخچه امانات یک دانشجو به همراه اطلاعات آمارای شامل تعداد کل امانات، تعداد کل کتاب های تحویل داده نشد، تعداد کل امانت هایی که توسط دانشجو با تاخیر تحویل داده شده اند.
    public Student getStudent() { return student; }
    public int getTotalLoans() { return totalLoans; }
    public int getNotReturnedCount() { return notReturnedCount; }
    public int getDelayedReturnsCount() { return delayedReturnsCount; }
}