package znulib;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import znulib.rest.ApiServer;
import znulib.service.LibrarySystem;
import znulib.ui.UiServer;

// کلاس اصلی برای اجرای برنامه (سرور REST + رابط کاربری وب)
public class UniversityLibrarySystem {
    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        LibrarySystem system = new LibrarySystem();

        Server server = new Server(port);
        server.setHandler(new ContextHandlerCollection(ApiServer.context(system), UiServer.context()));
        server.start();

        System.out.println("API: http://localhost:" + port + "/api");
        System.out.println("UI : http://localhost:" + port + "/ui/");
        server.join();
    }
}
