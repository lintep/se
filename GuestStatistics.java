// کلاس آمار مهمان
public class GuestStatistics {
    private int totalStudents;
    private int totalBooks;
    private int totalLoans;
    private int currentLoans;

    public GuestStatistics(LibrarySystem system) {
        this.totalStudents = system.getStudentCount();
        this.totalBooks = system.getBookCount();
        this.totalLoans = system.getLoanCount();
        this.currentLoans = system.getCurrentLoansCount();
    }

    // Getter methods
    // 2-3 مشاهده اطلاعات آماری ساده شامل تعداد کل دانشجویان، تعداد کل کتاب ها، تعداد کل امانت ها، تعداد آخرین کتاب هایی که در امانت هستند
    public int getTotalStudents() { return totalStudents; }
    public int getTotalBooks() { return totalBooks; }
    public int getTotalLoans() { return totalLoans; }
    public int getCurrentLoans() { return currentLoans; }
}