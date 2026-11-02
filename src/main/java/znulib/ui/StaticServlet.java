package znulib.ui;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

// فایل‌های ثابت رابط کاربری (HTML/CSS/JS) را از classpath می‌خواند: znulib/ui/web/
public class StaticServlet extends HttpServlet {
    private static final String BASE = "/znulib/ui/web/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        if (path == null || path.equals("/")) path = "/index.html";
        // فقط نام ساده‌ی فایل‌های html/css/js مجاز است
        if (!path.matches("/[A-Za-z0-9_-]+\\.(html|css|js)")) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        try (InputStream in = StaticServlet.class.getResourceAsStream(BASE + path.substring(1))) {
            if (in == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            resp.setContentType(path.endsWith(".html") ? "text/html;charset=UTF-8"
                    : path.endsWith(".css") ? "text/css;charset=UTF-8" : "application/javascript;charset=UTF-8");
            resp.setHeader("Cache-Control", "no-cache");
            OutputStream out = resp.getOutputStream();
            byte[] buffer = new byte[8192];
            int n;
            while ((n = in.read(buffer)) > 0) out.write(buffer, 0, n);
        }
    }
}
