package znulib.ui;

import org.eclipse.jetty.servlet.ServletContextHandler;

// رابط کاربری وب (HTML/CSS/JS) زیر آدرس /ui
public class UiServer {
    public static ServletContextHandler context() {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/ui");
        context.addServlet(StaticServlet.class, "/*");
        return context;
    }
}
