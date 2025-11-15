package temp;

import java.util.List;

// کلاس مهمان
public class Guest {
    // 2-1 مشاهده تعداد دانشجویان ثبت نام کرده 
    public int getRegisteredStudentCount(LibrarySystem system) {
        return system.getStudentCount();
    }

    // 2-2 جستجو فقط بر اساس نام کتاب و مشاهده نتایج جستجو صرفا شامل اطلاعات کتاب
    public List<Book> searchBooks(LibrarySystem system, String title) {
        return system.searchBooksByTitle(title);
    }

    // 2-3 مشاهده اطلاعات آماری ساده
    public GuestStatistics getStatistics(LibrarySystem system) {
        return new GuestStatistics(system);
    }
}