package temp;

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
        
        this.notReturnedCount = 0;
        this.delayedReturnsCount = 0;
        
        for (Loan loan : loans) {
            if (!loan.isReturned()) {
                notReturnedCount++;
            }
            if (loan.isOverdue()) {
                delayedReturnsCount++;
            }
        }
    }

    // Getter methods
    public Student getStudent() { return student; }
    public int getTotalLoans() { return totalLoans; }
    public int getNotReturnedCount() { return notReturnedCount; }
    public int getDelayedReturnsCount() { return delayedReturnsCount; }
}