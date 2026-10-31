package znulib.rest;

import znulib.model.Employee;
import znulib.model.Manager;
import znulib.model.Student;
import znulib.statistics.EmployeeStatistics;
import znulib.statistics.LoanStatistics;
import znulib.statistics.StudentOverallStatistics;
import znulib.statistics.StudentStatistics;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// منوی مدیر (مدیر یکی است و نام کاربری در آدرس نمی‌آید؛ فعلاً بدون احراز هویت)
@Path("/manager")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ManagerResource extends BaseResource {
    public static class EmployeeBody {
        public String username, password, name;
    }

    // 4-1 تعریف کارمند جدید
    @POST
    @Path("/employees")
    public Response createEmployee(EmployeeBody body) {
        required(body, "body");
        return Response.status(201).entity(write(() -> {
            required(body.username, "username");
            required(body.password, "password");
            required(body.name, "name");
            for (Employee e : system.getEmployees()) {
                if (e.getUsername().equals(body.username)) throw error(409, "نام کاربری تکراری است");
            }
            Employee employee = system.getManager().createEmployee(body.username, body.password, body.name);
            system.addEmployee(employee);
            return view(employee);
        })).build();
    }

    // 4-2 مشاهده عملکرد کارمندان
    @GET
    @Path("/employees")
    public List<Map<String, Object>> employeePerformance() {
        return read(() -> {
            List<Map<String, Object>> out = new ArrayList<>();
            for (Employee e : system.getEmployees()) {
                EmployeeStatistics stats = system.getManager().getEmployeeStatistics(e);
                Map<String, Object> m = view(e);
                m.put("booksRegistered", stats.getBooksRegistered());
                m.put("booksLoanedOut", stats.getBooksLoanedOut());
                m.put("booksReceived", stats.getBooksReceived());
                out.add(m);
            }
            return out;
        });
    }

    // 4-3 آمار امانت‌ها
    @GET
    @Path("/statistics/loans")
    public Map<String, Object> loanStatistics() {
        return read(() -> {
            LoanStatistics stats = system.getManager().getLoanStatistics(system);
            return map("totalLoanRequests", stats.getTotalLoanRequests(),
                    "totalLoansApproved", stats.getTotalLoansApproved(),
                    "averageLoanDuration", stats.getAverageLoanDuration());
        });
    }

    // 4-4 آمار کلی دانشجویان به همراه دانشجویان با بیشترین تاخیر
    @GET
    @Path("/statistics/students")
    public Map<String, Object> studentStatistics() {
        return read(() -> {
            StudentOverallStatistics stats = system.getManager().getStudentOverallStatistics(system);
            List<Map<String, Object>> all = new ArrayList<>();
            for (StudentStatistics s : stats.getAllStudentStats()) {
                all.add(studentReport(s.getStudent(), s));
            }
            List<Map<String, Object>> top = new ArrayList<>();
            for (Student s : stats.getTopDelayedStudents()) {
                top.add(studentReport(s, new StudentStatistics(s)));
            }
            return map("students", all, "topDelayedStudents", top);
        });
    }

    // تغییر رمز عبور مدیر
    @PUT
    @Path("/password")
    public Response changePassword(PasswordBody body) {
        required(body, "body");
        Manager manager = system.getManager();
        writeVoid(() -> manager.changePassword(required(body.password, "password")));
        return Response.noContent().build();
    }
}
