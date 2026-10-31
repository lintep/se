package znulib.rest;

import znulib.model.Guest;
import znulib.statistics.GuestStatistics;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

// منوی مهمان
@Path("/guest")
@Produces(MediaType.APPLICATION_JSON)
public class GuestResource extends BaseResource {
    private final Guest guest = new Guest();

    // 2-1 تعداد دانشجویان ثبت‌نام‌شده
    @GET
    @Path("/students/count")
    public Map<String, Object> studentCount() {
        return read(() -> map("count", guest.getRegisteredStudentCount(system)));
    }

    // 2-2 جستجوی کتاب فقط بر اساس عنوان
    @GET
    @Path("/books")
    public List<Map<String, Object>> searchBooks(@QueryParam("title") String title) {
        return read(() -> views(guest.searchBooks(system, title == null ? "" : title), this::view));
    }

    // 2-3 آمار ساده
    @GET
    @Path("/statistics")
    public Map<String, Object> statistics() {
        return read(() -> {
            GuestStatistics s = guest.getStatistics(system);
            return map("totalStudents", s.getTotalStudents(), "totalBooks", s.getTotalBooks(),
                    "totalLoans", s.getTotalLoans(), "currentLoans", s.getCurrentLoans());
        });
    }
}
