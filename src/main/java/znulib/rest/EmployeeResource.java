package znulib.rest;

import znulib.model.Book;
import znulib.model.Employee;
import znulib.model.Loan;
import znulib.model.LoanRequest;
import znulib.model.Student;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// منوی کارمند؛ نام کاربری کارمند در آدرس می‌آید (فعلاً بدون احراز هویت)
@Path("/employees/{username}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EmployeeResource extends BaseResource {
    public static class BookBody {
        public String title, author, isbn;
        public Integer publicationYear;
    }

    public static class ActiveBody {
        public Boolean active;
    }

    // ثبت کتاب جدید
    @POST
    @Path("/books")
    public Response registerBook(@PathParam("username") String username, BookBody body) {
        required(body, "body");
        return Response.status(201).entity(write(() -> {
            Employee employee = findEmployee(username);
            Book book = employee.registerBook(required(body.title, "title"), required(body.author, "author"),
                    required(body.publicationYear, "publicationYear"), required(body.isbn, "isbn"));
            system.addBook(book);
            return view(book);
        })).build();
    }

    // ویرایش اطلاعات کتاب (فقط فیلدهای ارسال‌شده تغییر می‌کنند)
    @PUT
    @Path("/books/{bookId}")
    public Map<String, Object> updateBook(@PathParam("username") String username,
                                          @PathParam("bookId") int bookId, BookBody body) {
        required(body, "body");
        return write(() -> {
            Employee employee = findEmployee(username);
            Book book = findBook(bookId);
            employee.updateBook(book, blankToNull(body.title), blankToNull(body.author),
                    body.publicationYear, blankToNull(body.isbn));
            return view(book);
        });
    }

    // درخواست‌های امانت در انتظار تایید
    @GET
    @Path("/loan-requests/pending")
    public List<Map<String, Object>> pendingRequests(@PathParam("username") String username) {
        return read(() -> {
            findEmployee(username);
            return views(system.getPendingLoanRequests(), this::view);
        });
    }

    // تایید درخواست امانت
    @POST
    @Path("/loan-requests/{requestId}/approve")
    public Map<String, Object> approve(@PathParam("username") String username,
                                       @PathParam("requestId") int requestId) {
        return write(() -> {
            Employee employee = findEmployee(username);
            LoanRequest request = findLoanRequest(requestId);
            if (request.isApproved()) throw error(409, "این درخواست قبلاً تایید شده است");
            Loan loan = system.approveLoanRequest(request, employee);
            if (loan == null) throw error(400, "درخواست امانت نمی‌تواند تایید شود (تاریخ شروع نامعتبر)");
            return view(loan);
        });
    }

    // امانت‌های فعال (تحویل داده نشده)
    @GET
    @Path("/loans/active")
    public List<Map<String, Object>> activeLoans(@PathParam("username") String username) {
        return read(() -> {
            findEmployee(username);
            List<Loan> active = new ArrayList<>();
            for (Loan loan : system.getLoans()) {
                if (!loan.isReturned()) active.add(loan);
            }
            return views(active, this::view);
        });
    }

    // ثبت دریافت کتاب بازگشتی
    @POST
    @Path("/loans/{loanId}/return")
    public Map<String, Object> receiveReturn(@PathParam("username") String username,
                                             @PathParam("loanId") int loanId) {
        return write(() -> {
            Employee employee = findEmployee(username);
            Loan loan = findLoan(loanId);
            if (loan.isReturned()) throw error(409, "این کتاب قبلاً تحویل گرفته شده است");
            employee.receiveReturnedBook(loan);
            return view(loan);
        });
    }

    // مشاهده گزارش و آمار یک دانشجو
    @GET
    @Path("/students/{studentUsername}/report")
    public Map<String, Object> studentReport(@PathParam("username") String username,
                                             @PathParam("studentUsername") String studentUsername) {
        return read(() -> {
            Employee employee = findEmployee(username);
            Student student = findStudent(studentUsername);
            return studentReport(student, employee.getStudentStatistics(student));
        });
    }

    // فعال/غیرفعال کردن دانشجو
    @PUT
    @Path("/students/{studentUsername}/active")
    public Map<String, Object> setStudentActive(@PathParam("username") String username,
                                                @PathParam("studentUsername") String studentUsername,
                                                ActiveBody body) {
        required(body, "body");
        return write(() -> {
            Employee employee = findEmployee(username);
            Student student = findStudent(studentUsername);
            if (required(body.active, "active")) employee.activateStudent(student);
            else employee.deactivateStudent(student);
            return view(student);
        });
    }

    // تغییر رمز عبور
    @PUT
    @Path("/password")
    public Response changePassword(@PathParam("username") String username, PasswordBody body) {
        required(body, "body");
        writeVoid(() -> findEmployee(username).changePassword(required(body.password, "password")));
        return Response.noContent().build();
    }
}
