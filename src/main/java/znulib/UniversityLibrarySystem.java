package znulib;

import org.eclipse.jetty.server.Server;
import znulib.rest.ApiServer;
import znulib.service.LibrarySystem;

// کلاس اصلی برای اجرای برنامه (سرور REST)
public class UniversityLibrarySystem {
    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        LibrarySystem system = new LibrarySystem();
        Server server = ApiServer.start(system, port);
        System.out.println("سرویس کتابخانه اجرا شد: http://localhost:" + port + "/api");
        server.join();
    }
}
