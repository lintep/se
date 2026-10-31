package znulib.rest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Collections;

// همه‌ی خطاها به شکل JSON برگردانده می‌شوند: {"error": "..."}
@Provider
public class ErrorMapper implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable t) {
        Response.ResponseBuilder builder;
        if (t instanceof WebApplicationException) {
            builder = Response.fromResponse(((WebApplicationException) t).getResponse());
        } else if (t instanceof IOException) {
            builder = Response.status(400); // JSON نامعتبر در بدنه‌ی درخواست
        } else {
            builder = Response.serverError();
        }
        String message = String.valueOf(t.getMessage()).split("\n")[0];
        return builder.type(MediaType.APPLICATION_JSON)
                .entity(Collections.singletonMap("error", message))
                .build();
    }
}
