package znulib.rest;

import znulib.model.Book;
import znulib.model.Employee;
import znulib.model.Loan;
import znulib.model.LoanRequest;
import znulib.model.Student;
import znulib.service.LibrarySystem;
import znulib.statistics.StudentStatistics;
import znulib.storage.DataStore;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

// کلاس پایه‌ی منابع REST: دسترسی به سیستم، قفل هم‌زمانی، ذخیره‌سازی، اعتبارسنجی و ساخت خروجی JSON
public abstract class BaseResource {
    // بدنه‌ی مشترک برای تغییر رمز عبور
    public static class PasswordBody {
        public String password;
    }

    @Inject
    protected LibrarySystem system;

    // خواندن اطلاعات (با قفل)
    protected <T> T read(Supplier<T> action) {
        synchronized (system) {
            return action.get();
        }
    }

    // تغییر اطلاعات (با قفل) و ذخیره در پایگاه داده
    protected <T> T write(Supplier<T> action) {
        synchronized (system) {
            T result = action.get();
            DataStore.save(system);
            return result;
        }
    }

    protected void writeVoid(Runnable action) {
        write(() -> {
            action.run();
            return null;
        });
    }

    // ---------- خطاها و اعتبارسنجی ----------
    protected static WebApplicationException error(int status, String message) {
        return new WebApplicationException(message, status);
    }

    protected static <T> T required(T value, String field) {
        if (value == null || (value instanceof String && ((String) value).trim().isEmpty())) {
            throw error(400, "فیلد الزامی است: " + field);
        }
        return value;
    }

    protected static String blankToNull(String value) {
        return value == null || value.trim().isEmpty() ? null : value;
    }

    protected static Integer optionalInt(String value, String field) {
        if (blankToNull(value) == null) return null;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw error(400, "مقدار عددی نامعتبر: " + field);
        }
    }

    protected static LocalDate parseDate(String value, String field) {
        try {
            return LocalDate.parse(required(value, field));
        } catch (DateTimeParseException e) {
            throw error(400, "تاریخ نامعتبر (قالب YYYY-MM-DD): " + field);
        }
    }

    // ---------- یافتن موجودیت‌ها ----------
    protected Student findStudent(String username) {
        for (Student s : system.getStudents()) {
            if (s.getUsername().equals(username)) return s;
        }
        throw error(404, "دانشجو پیدا نشد: " + username);
    }

    protected Employee findEmployee(String username) {
        for (Employee e : system.getEmployees()) {
            if (e.getUsername().equals(username)) return e;
        }
        throw error(404, "کارمند پیدا نشد: " + username);
    }

    // شماره‌ی کتاب = جایگاه آن در فهرست کتاب‌ها (از ۱)
    protected Book findBook(int id) {
        List<Book> books = system.getBooks();
        if (id < 1 || id > books.size()) throw error(404, "کتاب پیدا نشد: " + id);
        return books.get(id - 1);
    }

    protected LoanRequest findLoanRequest(int id) {
        for (LoanRequest r : system.getLoanRequests()) {
            if (r.getId() == id) return r;
        }
        throw error(404, "درخواست امانت پیدا نشد: " + id);
    }

    protected Loan findLoan(int id) {
        for (Loan l : system.getLoans()) {
            if (l.getId() == id) return l;
        }
        throw error(404, "امانت پیدا نشد: " + id);
    }

    // ---------- ساخت خروجی JSON ----------
    protected static Map<String, Object> map(Object... keyValues) {
        Map<String, Object> m = new LinkedHashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            m.put((String) keyValues[i], keyValues[i + 1]);
        }
        return m;
    }

    protected <T> List<Map<String, Object>> views(List<T> items, Function<? super T, Map<String, Object>> view) {
        List<Map<String, Object>> out = new ArrayList<>();
        for (T item : items) out.add(view.apply(item));
        return out;
    }

    protected Map<String, Object> view(Book b) {
        return map("id", system.getBooks().indexOf(b) + 1, "title", b.getTitle(), "author", b.getAuthor(),
                "publicationYear", b.getPublicationYear(), "isbn", b.getIsbn(), "available", b.isAvailable());
    }

    protected Map<String, Object> view(Student s) {
        return map("username", s.getUsername(), "name", s.getName(), "studentId", s.getStudentId(),
                "active", s.isActive());
    }

    protected Map<String, Object> view(Employee e) {
        return map("username", e.getUsername(), "name", e.getName(), "active", e.isActive());
    }

    protected Map<String, Object> view(LoanRequest r) {
        return map("id", r.getId(), "student", r.getStudent().getUsername(), "bookId", system.getBooks().indexOf(r.getBook()) + 1,
                "bookTitle", r.getBook().getTitle(), "startDate", r.getStartDate().toString(),
                "endDate", r.getEndDate().toString(), "approved", r.isApproved());
    }

    protected Map<String, Object> view(Loan l) {
        return map("id", l.getId(), "student", l.getStudent().getUsername(), "bookId", system.getBooks().indexOf(l.getBook()) + 1,
                "bookTitle", l.getBook().getTitle(), "approvedBy", l.getApprovedBy().getUsername(),
                "startDate", l.getStartDate().toString(), "endDate", l.getEndDate().toString(),
                "returnDate", l.getReturnDate() == null ? null : l.getReturnDate().toString(),
                "returned", l.isReturned(), "overdue", l.isOverdue(), "daysOverdue", l.getDaysOverdue());
    }

    protected Map<String, Object> studentReport(Student s, StudentStatistics stats) {
        Map<String, Object> m = view(s);
        m.put("totalLoans", stats.getTotalLoans());
        m.put("notReturnedCount", stats.getNotReturnedCount());
        m.put("delayedReturnsCount", stats.getDelayedReturnsCount());
        return m;
    }
}
