import java.time.LocalDate;

// کلاس کارمند
public class Employee extends User {
    private String name;
    private int booksRegistered;
    private int booksLoanedOut;
    private int booksReceived;

    // 3-1 ورود به سیستم (ثبت نام کارمند توسط مدیر کتابخانه انجام می شود)
    public Employee(String username, String password, String name) {
        super(username, password);
        this.name = name;
        this.booksRegistered = 0;
        this.booksLoanedOut = 0;
        this.booksReceived = 0;
    }

    // 3-3 ثبت اطلاعات کتاب
    public Book registerBook(String title, String author, int publicationYear, String isbn) {
        booksRegistered++;
        return new Book(title, author, publicationYear, isbn, this);
    }

    // 3-4 جستجو و ویرایش اطلاعات کتاب
    public void updateBook(Book book, String title, String author, Integer publicationYear, String isbn) {
        if (title != null) book.setTitle(title);
        if (author != null) book.setAuthor(author);
        if (publicationYear != null) book.setPublicationYear(publicationYear);
        if (isbn != null) book.setIsbn(isbn);
    }

    // 3-5 بررسی و تایید درخواست امانت کتاب که تاریخ شروع آنها برای همان روز یا روز قبل هست
    public boolean approveLoanRequest(LoanRequest request) {
        if (DateUtil.isTodayOrYesterday(request.getStartDate())) {
            booksLoanedOut++;
            return true;
        }
        return false;
    }

    // 3-8 دریافت کتاب به امانت برده شده و ثبت زمان دریافت کتاب
    public void receiveReturnedBook(Loan loan) {
        loan.setReturnDate(LocalDate.now());
        booksReceived++;
    }

    // 3-6 مشاهده گزارش تاریخچه امانات یک دانشجو به همراه اطلاعات آماری
    public StudentStatistics getStudentStatistics(Student student) {
        return new StudentStatistics(student);
    }

    // 3-7 فعال و غیر فعال کردن دانشجو
    public void activateStudent(Student student) {
        student.setActive(true);
    }
    
    public void deactivateStudent(Student student) {
        student.setActive(false);
    }

    // Getter methods for statistics
    public int getBooksRegistered() { return booksRegistered; }
    public int getBooksLoanedOut() { return booksLoanedOut; }
    public int getBooksReceived() { return booksReceived; }
    public String getName() { return name; }
}
