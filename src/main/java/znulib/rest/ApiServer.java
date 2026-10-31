package znulib.rest;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import znulib.service.LibrarySystem;

// راه‌اندازی Jetty و Jersey؛ همه‌ی آدرس‌ها زیر /api هستند
public class ApiServer {
    public static Server start(LibrarySystem system, int port) throws Exception {
        ResourceConfig config = new ResourceConfig()
                .register(JacksonFeature.class) // JSON با Jackson
                .registerClasses(GuestResource.class, BookResource.class, StudentResource.class,
                        EmployeeResource.class, ManagerResource.class, ErrorMapper.class)
                .register(new AbstractBinder() {
                    @Override
                    protected void configure() {
                        bind(system).to(LibrarySystem.class);
                    }
                });

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");
        context.addServlet(new ServletHolder(new ServletContainer(config)), "/api/*");

        Server server = new Server(port);
        server.setHandler(context);
        server.start();
        return server;
    }
}
