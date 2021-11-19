package ServerHttp;

import Servisofts.SConfig;
import Servisofts.SConsole;

import java.io.IOException;
import java.net.InetSocketAddress;
import org.jboss.com.sun.net.httpserver.HttpServer;
import org.jboss.com.sun.net.httpserver.HttpContext;

public class ServerHttp {

    public static void Start(int puerto) {
        HttpServer server;
        try {
            SConsole.warning("Initializing HttpServer on port ( " + puerto + " )");
            System.setProperty("org.eclipse.jetty.util.log.class", "org.eclipse.jetty.util.log.StdErrLog");
            System.setProperty("org.eclipse.jetty.LEVEL", "OFF");
            org.eclipse.jetty.util.log.Log.getProperties().setProperty("org.eclipse.jetty.util.log.announce", "false");
            org.eclipse.jetty.util.log.Log.getRootLogger().setDebugEnabled(false);
            server = HttpServer.create(new InetSocketAddress(puerto), 0);
            HttpContext apiContext = server.createContext("/api");
            apiContext.setHandler(Api::POST);
            server.start();
            SConsole.succes("HttpServer on port ( " + puerto + " ) is ready!");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}