package znulib.rest;

import znulib.model.Book;
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
import java.util.List;
import java.util.Map;

// منوی دانشجو
@Path("/students")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StudentResource extends BaseResource {
    public static class StudentBody {
        public String username, password, name, studentId;
    }

    public static class LoanRequestBody {
        public Integer bookId;
        public String startDate, endDate;
    }

    // فهرست دانشجویان
    @GET
    public List<Map<String, Object>> list() {
        return read(() -> views(system.getStudents(), this::view));
    }

    // 1-1 ثبت‌نام دانشجو
    @POST
    public Response register(StudentBody body) {
        required(body, "body");
        return Response.status(201).entity(write(() -> {
            required(body.username, "username");
            required(body.password, "password");
            required(body.name, "name");
            required(body.studentId, "studentId");
            for (Student s : system.getStudents()) {
                if (s.getUsername().equals(body.username)) throw error(409, "نام کاربری تکراری است");
            }
            return view(system.registerStudent(body.username, body.password, body.name, body.studentId));
        })).build();
    }

    // 1-4 ثبت درخواست امانت کتاب
    @POST
    @Path("/{username}/loan-requests")
    public Response requestLoan(@PathParam("username") String username, LoanRequestBody body) {
        required(body, "body");
        return Response.status(201).entity(write(() -> {
            Student student = findStudent(username);
            Book book = findBook(required(body.bookId, "bookId"));
            java.time.LocalDate start = parseDate(body.startDate, "startDate");
            java.time.LocalDate end = parseDate(body.endDate, "endDate");
            if (!book.isAvailable()) throw error(409, "کتاب در حال حاضر امانت داده شده است");
            try {
                LoanRequest request = student.requestBookLoan(book, start, end);
                system.addLoanRequest(request);
                return view(request);
            } catch (IllegalStateException e) {
                throw error(409, "حساب دانشجو غیرفعال است");
            }
        })).build();
    }

    // مشاهده تاریخچه امانت‌ها
    @GET
    @Path("/{username}/loans")
    public List<Map<String, Object>> loanHistory(@PathParam("username") String username) {
        return read(() -> views(findStudent(username).getLoanHistory(), this::view));
    }

    // 1-2 تغییر رمز عبور
    @PUT
    @Path("/{username}/password")
    public Response changePassword(@PathParam("username") String username, PasswordBody body) {
        required(body, "body");
        writeVoid(() -> findStudent(username).changePassword(required(body.password, "password")));
        return Response.noContent().build();
    }
}
