package znulib.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

// جستجوی کتاب (ترکیبی از عنوان، سال نشر و نویسنده؛ همه اختیاری)
@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
public class BookResource extends BaseResource {
    @GET
    public List<Map<String, Object>> search(@QueryParam("title") String title,
                                            @QueryParam("year") String year,
                                            @QueryParam("author") String author) {
        Integer y = optionalInt(year, "year");
        return read(() -> views(system.searchBooks(blankToNull(title), y, blankToNull(author)), this::view));
    }
}
