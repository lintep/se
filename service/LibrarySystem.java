package znulib.service;

import znulib.model.Book;
import znulib.model.Employee;
import znulib.model.Guest;
import znulib.model.Loan;
import znulib.model.LoanRequest;
import znulib.model.Manager;
import znulib.model.Student;
import znulib.model.User;
import znulib.statistics.EmployeeStatistics;
import znulib.statistics.GuestStatistics;
import znulib.statistics.LoanStatistics;
import znulib.statistics.StudentOverallStatistics;
import znulib.statistics.StudentStatistics;
import znulib.storage.DataStore;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// سیستم اصلی کتابخانه
public class LibrarySystem {
    private List<Student> students;
    private List<Employee> employees;
    private List<Book> books;
    private List<LoanRequest> loanRequests;
    private List<Loan> loans;
    private Manager manager;
    private Scanner scanner;

    public LibrarySystem() {
        this.students = new ArrayList<>();
        this.employees = new ArrayList<>();
        this.books = new ArrayList<>();
        this.loanRequests = new ArrayList<>();
        this.loans = new ArrayList<>();
        this.scanner = new Scanner(System.in);
        
        // ایجاد مدیر پیش‌فرض
        this.manager = new Manager("admin", "admin123");
        
        // ایجاد داده‌های نمونه
        if (DataStore.exists()) {
            DataStore.load(this);
        } else {
            initializeSampleData();
            DataStore.save(this);
        }
    }

    private void initializeSampleData() {
        // ایجاد کارمند نمونه
        Employee emp1 = manager.createEmployee("emp1", "123", "کارمند نمونه");
        employees.add(emp1);
        
        // ایجاد دانشجویان نمونه
        students.add(new Student("stu1", "123", "دانشجو یک", "S001"));
        students.add(new Student("stu2", "123", "دانشجو دو", "S002"));
        
        // ایجاد کتاب‌های نمونه
        books.add(emp1.registerBook("آموزش جاوا", "نویسنده یک", 2023, "111-1111111111"));
        books.add(emp1.registerBook("پایگاه داده", "نویسنده دو", 2022, "222-2222222222"));
        books.add(emp1.registerBook("هوش مصنوعی", "نویسنده سه", 2024, "333-3333333333"));
        
        // ایجاد برخی درخواست‌های امانت نمونه
        Student student1 = students.get(0);
        Book book1 = books.get(0);
        LoanRequest request = new LoanRequest(student1, book1, LocalDate.now(), LocalDate.now().plusDays(14));
        loanRequests.add(request);
    }

    // متدهای اصلی سیستم
    public void start() {
        while (true) {
            DataStore.save(this);
            showMainMenu();
            int choice = getIntInput("لطفاً گزینه مورد نظر را انتخاب کنید: ");
            
            switch (choice) {
                case 1:
                    studentLogin();
                    break;
                case 2:
                    employeeLogin();
                    break;
                case 3:
                    managerLogin();
                    break;
                case 4:
                    guestMenu();
                    break;
                case 5:
                    System.out.println("خروج از سیستم...");
                    scanner.close();
                    return;
                default:
                    System.out.println("گزینه نامعتبر!");
            }
        }
    }

    private void showMainMenu() {
        System.out.println("\n=== سیستم مدیریت کتابخانه دانشگاه ===");
        System.out.println("1. ورود دانشجو");
        System.out.println("2. ورود کارمند");
        System.out.println("3. ورود مدیر");
        System.out.println("4. ورود به عنوان مهمان");
        System.out.println("5. خروج");
    }

    // منوی دانشجو
    private void studentLogin() {
        System.out.println("\n--- ورود دانشجو ---");
        String username = getStringInput("نام کاربری: ");
        String password = getStringInput("رمز عبور: ");
        
        Student student = studentLogin(username, password);
        if (student != null) {
            System.out.println("ورود موفقیت‌آمیز! خوش آمدید " + student.getName());
            studentMenu(student);
        } else {
            System.out.println("نام کاربری یا رمز عبور اشتباه است!");
        }
    }

    private void studentMenu(Student student) {
        while (true) {
            DataStore.save(this);
            System.out.println("\n--- منوی دانشجو ---");
            System.out.println("1. جستجوی کتاب");
            System.out.println("2. درخواست امانت کتاب");
            System.out.println("3. مشاهده تاریخچه امانت‌ها");
            System.out.println("4. تغییر رمز عبور");
            System.out.println("5. خروج");
            
            int choice = getIntInput("لطفاً گزینه مورد نظر را انتخاب کنید: ");
            
            switch (choice) {
                case 1:
                    searchBooksMenu(student);
                    break;
                case 2:
                    requestLoanMenu(student);
                    break;
                case 3:
                    showLoanHistory(student);
                    break;
                case 4:
                    changePasswordMenu(student);
                    break;
                case 5:
                    return;
                default:
                    System.out.println("گزینه نامعتبر!");
            }
        }
    }

    // منوی کارمند
    private void employeeLogin() {
        System.out.println("\n--- ورود کارمند ---");
        String username = getStringInput("نام کاربری: ");
        String password = getStringInput("رمز عبور: ");
        
        Employee employee = employeeLogin(username, password);
        if (employee != null) {
            System.out.println("ورود موفقیت‌آمیز! خوش آمدید " + employee.getName());
            employeeMenu(employee);
        } else {
            System.out.println("نام کاربری یا رمز عبور اشتباه است!");
        }
    }

    private void employeeMenu(Employee employee) {
        while (true) {
            DataStore.save(this);
            System.out.println("\n--- منوی کارمند ---");
            System.out.println("1. ثبت کتاب جدید");
            System.out.println("2. ویرایش اطلاعات کتاب");
            System.out.println("3. بررسی درخواست‌های امانت");
            System.out.println("4. ثبت دریافت کتاب بازگشتی");
            System.out.println("5. مشاهده گزارش دانشجو");
            System.out.println("6. فعال/غیرفعال کردن دانشجو");
            System.out.println("7. تغییر رمز عبور");
            System.out.println("8. خروج");
            
            int choice = getIntInput("لطفاً گزینه مورد نظر را انتخاب کنید: ");
            
            switch (choice) {
                case 1:
                    registerBookMenu(employee);
                    break;
                case 2:
                    updateBookMenu(employee);
                    break;
                case 3:
                    reviewLoanRequestsMenu(employee);
                    break;
                case 4:
                    receiveReturnedBookMenu(employee);
                    break;
                case 5:
                    viewStudentReportMenu(employee);
                    break;
                case 6:
                    manageStudentStatusMenu(employee);
                    break;
                case 7:
                    changePasswordMenu(employee);
                    break;
                case 8:
                    return;
                default:
                    System.out.println("گزینه نامعتبر!");
            }
        }
    }

    private void registerBookMenu(Employee employee) {
        System.out.println("\n--- ثبت کتاب جدید ---");
        String title = getStringInput("عنوان کتاب: ");
        String author = getStringInput("نام نویسنده: ");
        int year = getIntInput("سال نشر: ");
        String isbn = getStringInput("شابک (ISBN): ");
        
        Book newBook = employee.registerBook(title, author, year, isbn);
        addBook(newBook);
        System.out.println("کتاب با موفقیت ثبت شد: " + newBook);
    }

    private void updateBookMenu(Employee employee) {
        System.out.println("\n--- ویرایش اطلاعات کتاب ---");
        listAllBooks();
        
        if (books.isEmpty()) {
            System.out.println("هیچ کتابی برای ویرایش وجود ندارد.");
            return;
        }
        
        int bookIndex = getIntInput("شماره کتاب برای ویرایش: ") - 1;
        if (bookIndex < 0 || bookIndex >= books.size()) {
            System.out.println("شماره کتاب نامعتبر!");
            return;
        }
        
        Book book = books.get(bookIndex);
        System.out.println("ویرایش کتاب: " + book);
        
        String newTitle = getStringInput("عنوان جدید (Enter برای بدون تغییر): ");
        String newAuthor = getStringInput("نویسنده جدید (Enter برای بدون تغییر): ");
        String newYearStr = getStringInput("سال نشر جدید (Enter برای بدون تغییر): ");
        String newIsbn = getStringInput("شابک جدید (Enter برای بدون تغییر): ");
        
        Integer newYear = null;
        if (!newYearStr.isEmpty()) {
            newYear = Integer.parseInt(newYearStr);
        }
        
        employee.updateBook(book, 
            newTitle.isEmpty() ? null : newTitle,
            newAuthor.isEmpty() ? null : newAuthor,
            newYear,
            newIsbn.isEmpty() ? null : newIsbn);
        
        System.out.println("اطلاعات کتاب با موفقیت به‌روزرسانی شد.");
    }

    private void reviewLoanRequestsMenu(Employee employee) {
        System.out.println("\n--- بررسی درخواست‌های امانت ---");
        List<LoanRequest> pendingRequests = getPendingLoanRequests();
        
        if (pendingRequests.isEmpty()) {
            System.out.println("هیچ درخواست امانت در انتظار تایید وجود ندارد.");
            return;
        }
        
        System.out.println("درخواست‌های در انتظار تایید:");
        for (int i = 0; i < pendingRequests.size(); i++) {
            System.out.println((i + 1) + ". " + pendingRequests.get(i));
        }
        
        int requestIndex = getIntInput("شماره درخواست برای بررسی: ") - 1;
        if (requestIndex < 0 || requestIndex >= pendingRequests.size()) {
            System.out.println("شماره درخواست نامعتبر!");
            return;
        }
        
        LoanRequest request = pendingRequests.get(requestIndex);
        if (employee.approveLoanRequest(request)) {
            Loan loan = approveLoanRequest(request, employee);
            System.out.println("درخواست امانت تایید شد. شماره امانت: " + loan.getId());
        } else {
            System.out.println("درخواست امانت نمی‌تواند تایید شود (تاریخ شروع نامعتبر).");
        }
    }

    private void receiveReturnedBookMenu(Employee employee) {
        System.out.println("\n--- ثبت دریافت کتاب بازگشتی ---");
        List<Loan> activeLoans = new ArrayList<>();
        for (Loan loan : loans) {
            if (!loan.isReturned()) {
                activeLoans.add(loan);
            }
        }
        
        if (activeLoans.isEmpty()) {
            System.out.println("هیچ امانت فعالی وجود ندارد.");
            return;
        }
        
        System.out.println("امانت‌های فعال:");
        for (int i = 0; i < activeLoans.size(); i++) {
            System.out.println((i + 1) + ". " + activeLoans.get(i));
        }
        
        int loanIndex = getIntInput("شماره امانت برای ثبت بازگشت: ") - 1;
        if (loanIndex < 0 || loanIndex >= activeLoans.size()) {
            System.out.println("شماره امانت نامعتبر!");
            return;
        }
        
        Loan loan = activeLoans.get(loanIndex);
        employee.receiveReturnedBook(loan);
        System.out.println("بازگشت کتاب با موفقیت ثبت شد.");
    }

    private void viewStudentReportMenu(Employee employee) {
        System.out.println("\n--- مشاهده گزارش دانشجو ---");
        listAllStudents();
        
        if (students.isEmpty()) {
            System.out.println("هیچ دانشجویی ثبت‌نام نکرده است.");
            return;
        }
        
        int studentIndex = getIntInput("شماره دانشجو برای مشاهده گزارش: ") - 1;
        if (studentIndex < 0 || studentIndex >= students.size()) {
            System.out.println("شماره دانشجو نامعتبر!");
            return;
        }
        
        Student student = students.get(studentIndex);
        StudentStatistics stats = employee.getStudentStatistics(student);
        
        System.out.println("\nگزارش دانشجو: " + student.getName());
        System.out.println("تعداد کل امانت‌ها: " + stats.getTotalLoans());
        System.out.println("تعداد کتاب‌های تحویل داده نشده: " + stats.getNotReturnedCount());
        System.out.println("تعداد امانت‌های با تاخیر: " + stats.getDelayedReturnsCount());
    }

    private void manageStudentStatusMenu(Employee employee) {
        System.out.println("\n--- مدیریت وضعیت دانشجو ---");
        listAllStudents();
        
        if (students.isEmpty()) {
            System.out.println("هیچ دانشجویی ثبت‌نام نکرده است.");
            return;
        }
        
        int studentIndex = getIntInput("شماره دانشجو: ") - 1;
        if (studentIndex < 0 || studentIndex >= students.size()) {
            System.out.println("شماره دانشجو نامعتبر!");
            return;
        }
        
        Student student = students.get(studentIndex);
        System.out.println("وضعیت فعلی: " + (student.isActive() ? "فعال" : "غیرفعال"));
        
        int action = getIntInput("1. فعال کردن\n2. غیرفعال کردن\nانتخاب عمل: ");
        
        if (action == 1) {
            employee.activateStudent(student);
            System.out.println("دانشجو فعال شد.");
        } else if (action == 2) {
            employee.deactivateStudent(student);
            System.out.println("دانشجو غیرفعال شد.");
        } else {
            System.out.println("عمل نامعتبر!");
        }
    }

    // منوی مدیر
    private void managerLogin() {
        System.out.println("\n--- ورود مدیر ---");
        String username = getStringInput("نام کاربری: ");
        String password = getStringInput("رمز عبور: ");
        
        if (manager.login(username, password)) {
            System.out.println("ورود موفقیت‌آمیز! خوش آمدید مدیر");
            managerMenu();
        } else {
            System.out.println("نام کاربری یا رمز عبور اشتباه است!");
        }
    }

    private void managerMenu() {
        while (true) {
            DataStore.save(this);
            System.out.println("\n--- منوی مدیر ---");
            System.out.println("1. تعریف کارمند جدید");
            System.out.println("2. مشاهده عملکرد کارمندان");
            System.out.println("3. مشاهده آمار امانت‌ها");
            System.out.println("4. مشاهده آمار کلی دانشجویان");
            System.out.println("5. تغییر رمز عبور");
            System.out.println("6. خروج");
            
            int choice = getIntInput("لطفاً گزینه مورد نظر را انتخاب کنید: ");
            
            switch (choice) {
                case 1:
                    createEmployeeMenu();
                    break;
                case 2:
                    viewEmployeePerformanceMenu();
                    break;
                case 3:
                    viewLoanStatisticsMenu();
                    break;
                case 4:
                    viewStudentStatisticsMenu();
                    break;
                case 5:
                    changePasswordMenu(manager);
                    break;
                case 6:
                    return;
                default:
                    System.out.println("گزینه نامعتبر!");
            }
        }
    }

    private void createEmployeeMenu() {
        System.out.println("\n--- تعریف کارمند جدید ---");
        String username = getStringInput("نام کاربری: ");
        String password = getStringInput("رمز عبور: ");
        String name = getStringInput("نام کامل: ");
        
        Employee newEmployee = manager.createEmployee(username, password, name);
        addEmployee(newEmployee);
        System.out.println("کارمند جدید با موفقیت ایجاد شد.");
    }

    private void viewEmployeePerformanceMenu() {
        System.out.println("\n--- عملکرد کارمندان ---");
        if (employees.isEmpty()) {
            System.out.println("هیچ کارمندی تعریف نشده است.");
            return;
        }
        
        for (Employee employee : employees) {
            EmployeeStatistics stats = manager.getEmployeeStatistics(employee);
            System.out.println("\nکارمند: " + employee.getName());
            System.out.println("کتاب‌های ثبت شده: " + stats.getBooksRegistered());
            System.out.println("کتاب‌های امانت داده شده: " + stats.getBooksLoanedOut());
            System.out.println("کتاب‌های تحویل گرفته شده: " + stats.getBooksReceived());
        }
    }

    private void viewLoanStatisticsMenu() {
        System.out.println("\n--- آمار امانت‌ها ---");
        LoanStatistics stats = manager.getLoanStatistics(this);
        
        System.out.println("تعداد درخواست‌های امانت ثبت شده: " + stats.getTotalLoanRequests());
        System.out.println("تعداد کل امانت‌های داده شده: " + stats.getTotalLoansApproved());
        System.out.println("میانگین مدت امانت (روز): " + String.format("%.2f", stats.getAverageLoanDuration()));
    }

    private void viewStudentStatisticsMenu() {
        System.out.println("\n--- آمار کلی دانشجویان ---");
        StudentOverallStatistics stats = manager.getStudentOverallStatistics(this);
        
        System.out.println("دانشجویان با بیشترین تاخیر در تحویل کتاب:");
        List<Student> topDelayed = stats.getTopDelayedStudents();
        if (topDelayed.isEmpty()) {
            System.out.println("هیچ دانشجویی با سابقه تاخیر وجود ندارد.");
        } else {
            for (int i = 0; i < topDelayed.size(); i++) {
                Student student = topDelayed.get(i);
                StudentStatistics studentStats = new StudentStatistics(student);
                System.out.println((i + 1) + ". " + student.getName() + 
                    " - تعداد تاخیرها: " + studentStats.getDelayedReturnsCount());
            }
        }
    }

    // منوی مهمان
    private void guestMenu() {
        Guest guest = new Guest();
        while (true) {
            System.out.println("\n--- منوی مهمان ---");
            System.out.println("1. مشاهده تعداد دانشجویان");
            System.out.println("2. جستجوی کتاب");
            System.out.println("3. مشاهده آمار کلی");
            System.out.println("4. بازگشت به منوی اصلی");
            
            int choice = getIntInput("لطفاً گزینه مورد نظر را انتخاب کنید: ");
            
            switch (choice) {
                case 1:
                    System.out.println("تعداد دانشجویان ثبت‌نام شده: " + guest.getRegisteredStudentCount(this));
                    break;
                case 2:
                    String title = getStringInput("عنوان کتاب: ");
                    List<Book> results = guest.searchBooks(this, title);
                    System.out.println("\nنتایج جستجو:");
                    for (Book book : results) {
                        System.out.println(book);
                    }
                    break;
                case 3:
                    GuestStatistics stats = guest.getStatistics(this);
                    System.out.println("آمار کلی کتابخانه:");
                    System.out.println("تعداد دانشجویان: " + stats.getTotalStudents());
                    System.out.println("تعداد کتاب‌ها: " + stats.getTotalBooks());
                    System.out.println("تعداد کل امانت‌ها: " + stats.getTotalLoans());
                    System.out.println("تعداد امانت‌های جاری: " + stats.getCurrentLoans());
                    break;
                case 4:
                    return;
                default:
                    System.out.println("گزینه نامعتبر!");
            }
        }
    }

    // متدهای کمکی
    private void searchBooksMenu(Student student) {
        System.out.println("\n--- جستجوی کتاب ---");
        String title = getStringInput("عنوان کتاب (Enter برای نادیده گرفتن): ");
        String yearStr = getStringInput("سال نشر (Enter برای نادیده گرفتن): ");
        String author = getStringInput("نام نویسنده (Enter برای نادیده گرفتن): ");
        
        Integer year = null;
        if (!yearStr.isEmpty()) {
            try {
                year = Integer.parseInt(yearStr);
            } catch (NumberFormatException e) {
                System.out.println("سال نامعتبر!");
                return;
            }
        }
        
        List<Book> results = student.searchBooks(this, title.isEmpty() ? null : title, 
                                                year, author.isEmpty() ? null : author);
        
        System.out.println("\nنتایج جستجو:");
        if (results.isEmpty()) {
            System.out.println("کتابی یافت نشد.");
        } else {
            for (int i = 0; i < results.size(); i++) {
                System.out.println((i + 1) + ". " + results.get(i));
            }
        }
    }

    private void requestLoanMenu(Student student) {
        System.out.println("\n--- درخواست امانت کتاب ---");
        List<Book> availableBooks = new ArrayList<>();
        for (Book book : books) {
            if (book.isAvailable()) {
                availableBooks.add(book);
            }
        }
        
        if (availableBooks.isEmpty()) {
            System.out.println("هیچ کتابی برای امانت موجود نیست.");
            return;
        }
        
        System.out.println("کتاب‌های موجود:");
        for (int i = 0; i < availableBooks.size(); i++) {
            System.out.println((i + 1) + ". " + availableBooks.get(i).getTitle());
        }
        
        int bookChoice = getIntInput("شماره کتاب مورد نظر را انتخاب کنید: ") - 1;
        if (bookChoice < 0 || bookChoice >= availableBooks.size()) {
            System.out.println("شماره کتاب نامعتبر!");
            return;
        }
        
        Book selectedBook = availableBooks.get(bookChoice);
        
        System.out.print("تاریخ شروع امانت (YYYY-MM-DD): ");
        String startDateStr = scanner.nextLine();
        System.out.print("تاریخ پایان امانت (YYYY-MM-DD): ");
        String endDateStr = scanner.nextLine();
        
        try {
            LoanRequest request = student.requestBookLoan(selectedBook, 
                LocalDate.parse(startDateStr), 
                LocalDate.parse(endDateStr));
            addLoanRequest(request);
            System.out.println("درخواست امانت با موفقیت ثبت شد!");
        } catch (Exception e) {
            System.out.println("خطا در ثبت درخواست: " + e.getMessage());
        }
    }

    private void showLoanHistory(Student student) {
        System.out.println("\n--- تاریخچه امانت‌های شما ---");
        List<Loan> history = student.getLoanHistory();
        if (history.isEmpty()) {
            System.out.println("هیچ امانتی ثبت نشده است.");
        } else {
            for (Loan loan : history) {
                System.out.println(loan);
            }
        }
    }

    private void changePasswordMenu(User user) {
        String newPassword = getStringInput("رمز عبور جدید: ");
        user.changePassword(newPassword);
        System.out.println("رمز عبور با موفقیت تغییر یافت.");
    }

    private void listAllBooks() {
        System.out.println("\n--- لیست تمام کتاب‌ها ---");
        if (books.isEmpty()) {
            System.out.println("هیچ کتابی ثبت نشده است.");
        } else {
            for (int i = 0; i < books.size(); i++) {
                System.out.println((i + 1) + ". " + books.get(i));
            }
        }
    }

    private void listAllStudents() {
        System.out.println("\n--- لیست تمام دانشجویان ---");
        if (students.isEmpty()) {
            System.out.println("هیچ دانشجویی ثبت‌نام نکرده است.");
        } else {
            for (int i = 0; i < students.size(); i++) {
                Student student = students.get(i);
                System.out.println((i + 1) + ". " + student.getName() + 
                    " (" + student.getStudentId() + ") - وضعیت: " + 
                    (student.isActive() ? "فعال" : "غیرفعال"));
            }
        }
    }

    // متدهای اصلی سیستم (بقیه متدها بدون تغییر)
    public Student registerStudent(String username, String password, String name, String studentId) {
        Student student = new Student(username, password, name, studentId);
        students.add(student);
        return student;
    }

    public Student studentLogin(String username, String password) {
        for (Student student : students) {
            if (student.login(username, password)) {
                return student;
            }
        }
        return null;
    }

    public Employee employeeLogin(String username, String password) {
        for (Employee employee : employees) {
            if (employee.login(username, password)) {
                return employee;
            }
        }
        return null;
    }

    public List<Book> searchBooks(String title, Integer year, String author) {
        List<Book> result = new ArrayList<>();
        for (Book book : books) {
            boolean matches = true;
            
            if (title != null && !book.getTitle().toLowerCase().contains(title.toLowerCase())) {
                matches = false;
            }
            
            if (year != null && book.getPublicationYear() != year) {
                matches = false;
            }
            
            if (author != null && !book.getAuthor().toLowerCase().contains(author.toLowerCase())) {
                matches = false;
            }
            
            if (matches) {
                result.add(book);
            }
        }
        return result;
    }

    public List<Book> searchBooksByTitle(String title) {
        List<Book> result = new ArrayList<>();
        for (Book book : books) {
            if (book.getTitle().toLowerCase().contains(title.toLowerCase())) {
                result.add(book);
            }
        }
        return result;
    }

    public void addLoanRequest(LoanRequest request) {
        loanRequests.add(request);
    }

    public List<LoanRequest> getPendingLoanRequests() {
        List<LoanRequest> pending = new ArrayList<>();
        for (LoanRequest request : loanRequests) {
            if (!request.isApproved()) {
                pending.add(request);
            }
        }
        return pending;
    }

    public Loan approveLoanRequest(LoanRequest request, Employee employee) {
        if (employee.approveLoanRequest(request)) {
            request.setApproved(true);
            Loan loan = new Loan(request, employee);
            loans.add(loan);
            return loan;
        }
        return null;
    }

    // Getter methods
    public int getStudentCount() { return students.size(); }
    public int getBookCount() { return books.size(); }
    public int getLoanCount() { return loans.size(); }
    public int getCurrentLoansCount() { 
        int count = 0;
        for (Loan loan : loans) {
            if (!loan.isReturned()) {
                count++;
            }
        }
        return count;
    }
    
    public List<Student> getStudents() { return new ArrayList<>(students); }
    public List<Book> getBooks() { return new ArrayList<>(books); }
    public List<LoanRequest> getLoanRequests() { return new ArrayList<>(loanRequests); }
    public List<Loan> getLoans() { return new ArrayList<>(loans); }
    public Manager getManager() { return manager; }
    
    public void addEmployee(Employee employee) { employees.add(employee); }
    public void addBook(Book book) { books.add(book); }
    public void addStudent(Student student) { students.add(student); }
    public void addLoan(Loan loan) { loans.add(loan); }
    public List<Employee> getEmployees() { return new ArrayList<>(employees); }

    // متدهای کمکی برای ورودی
    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("لطفاً یک عدد وارد کنید!");
            }
        }
    }
}
