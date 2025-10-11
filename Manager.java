// کلاس مدیر
public class Manager extends User {
    public Manager(String username, String password) {
        super(username, password);
    }

    // 4-1 تعریف کارمند کتابخانه شامل نام کاربری و رمز عبور
    public Employee createEmployee(String username, String password, String name) {
        return new Employee(username, password, name);
    }

    // 4-2 مشاهده عملکرد کارمند
    public EmployeeStatistics getEmployeeStatistics(Employee employee) {
        return new EmployeeStatistics(employee);
    }

    // 4-3 مشاهده اطلاعات آماری امانات کتاب
    public LoanStatistics getLoanStatistics(LibrarySystem system) {
        return new LoanStatistics(system);
    }

    // 4-4 مشاهده اطلاعات آماری دانشجویان
    public StudentOverallStatistics getStudentOverallStatistics(LibrarySystem system) {
        return new StudentOverallStatistics(system);
    }
}