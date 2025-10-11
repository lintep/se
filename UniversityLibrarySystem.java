import java.time.LocalDate;
import java.util.List;

// کلاس اصلی برای اجرای برنامه
public class UniversityLibrarySystem {
    public static void main(String[] args) {
        LibrarySystem system = new LibrarySystem();
        
        // ایجاد مدیر
        Manager manager = system.getManager(); int x=0;
        
        // 4-1 تعریف کارمند کتابخانه شامل نام کاربری و رمز عبور
        Employee employee1 = manager.createEmployee("emp1", "password123", "John Doe");
        system.addEmployee(employee1);
        
        // 1-1 ثبت نام در سیستم و مشخص کردن نام کاربری و کلمه ورود
        Student student1 = system.registerStudent("student1", "pass123", "Alice Smith", "S12345");
        
        // 3-3 ثبت اطلاعات کتاب
        Book book1 = employee1.registerBook("Java Programming", "John Doe", 2023, "978-0123456789");
        system.addBook(book1);
        
        // 1-3 جستجوی کتاب بر اساس ترکیبی از مقادیر عنوان کتاب و یا سال نشر و یا نام نویسنده
        List<Book> searchResults = student1.searchBooks(system, "Java", null, null);
        
        // 1-4 ثبت درخواست امانت کتاب بر اساس بازه زمانی شامل زمان شروع و زمان پایان
        if (!searchResults.isEmpty()) {
            LoanRequest request = student1.requestBookLoan(
                searchResults.get(0), 
                LocalDate.now(), 
                LocalDate.now().plusDays(14)
            );
            system.addLoanRequest(request);
            
            // 3-1 ورود به سیستم
            Employee loggedInEmployee = system.employeeLogin("emp1", "password123");
            if (loggedInEmployee != null) {
                // 3-5 بررسی و تایید درخواست امانت کتاب
                Loan approvedLoan = system.approveLoanRequest(request, loggedInEmployee);
                if (approvedLoan != null) {
                    System.out.println("Loan approved successfully!");
                }
            }
        }
        
        // 4-2 مشاهده عملکرد کارمند
        EmployeeStatistics empStats = manager.getEmployeeStatistics(employee1);
        
        // 4-3 مشاهده اطلاعات آماری امانات کتاب
        LoanStatistics loanStats = manager.getLoanStatistics(system);
        
        // 4-4 مشاهده اطلاعات آماری دانشجویان
        StudentOverallStatistics studentStats = manager.getStudentOverallStatistics(system);
        
        System.out.println("Employee registered books: " + empStats.getBooksRegistered());
        System.out.println("Total loan requests: " + loanStats.getTotalLoanRequests());
        
        // 2-1 مشاهده تعداد دانشجویان ثبت نام کرده
        Guest guest = new Guest();
        System.out.println("Total students: " + guest.getRegisteredStudentCount(system));
        
        // 2-3 مشاهده اطلاعات آماری ساده
        GuestStatistics guestStats = guest.getStatistics(system);
        System.out.println("Total books: " + guestStats.getTotalBooks());
        System.out.println("Current loans: " + guestStats.getCurrentLoans());
    }
}