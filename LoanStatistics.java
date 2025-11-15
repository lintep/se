package temp;

import java.util.List;

// کلاس آمار امانت
public class LoanStatistics {
    private int totalLoanRequests;
    private int totalLoansApproved;
    private double averageLoanDuration;

    public LoanStatistics(LibrarySystem system) {
        calculateStatistics(system);
    }

    private void calculateStatistics(LibrarySystem system) {
        List<LoanRequest> requests = system.getLoanRequests();
        List<Loan> loans = system.getLoans();
        
        this.totalLoanRequests = requests.size();
        this.totalLoansApproved = loans.size();
        
        // محاسبه میانگین مدت امانت بدون استفاده از ChronoUnit
        double totalDuration = 0;
        int returnedLoansCount = 0;
        
        for (Loan loan : loans) {
            if (loan.isReturned()) {
                totalDuration += loan.getLoanDuration();
                returnedLoansCount++;
            }
        }
        
        this.averageLoanDuration = returnedLoansCount > 0 ? totalDuration / returnedLoansCount : 0.0;
    }

    // Getter methods
    // 4-3 مشاهده اطلاعات آماری امانات کتاب شامل تعداد درخواست های امانت ثبت شده، تعداد کل امانت داده شده، میانگین تعداد روزهایی که کتاب به امانت برده شده
    public int getTotalLoanRequests() { return totalLoanRequests; }
    public int getTotalLoansApproved() { return totalLoansApproved; }
    public double getAverageLoanDuration() { return averageLoanDuration; }
}