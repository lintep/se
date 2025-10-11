// کلاس آمار کارمند
public class EmployeeStatistics {
    private Employee employee;
    private int booksRegistered;
    private int booksLoanedOut;
    private int booksReceived;

    public EmployeeStatistics(Employee employee) {
        this.employee = employee;
        this.booksRegistered = employee.getBooksRegistered();
        this.booksLoanedOut = employee.getBooksLoanedOut();
        this.booksReceived = employee.getBooksReceived();
    }

    // Getter methods
    // 4-2 مشاهده عملکرد کارمند شامل تعداد اطلاعات کتاب هایی که ثبت کرده، تعداد کل کتاب هایی که به امانت داده، تعداد کل کتاب هایی که تحویل گرفته
    public Employee getEmployee() { return employee; }
    public int getBooksRegistered() { return booksRegistered; }
    public int getBooksLoanedOut() { return booksLoanedOut; }
    public int getBooksReceived() { return booksReceived; }
}