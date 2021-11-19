package ServerHttp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.jboss.com.sun.net.httpserver.HttpExchange;
import org.json.JSONObject;

import component.Manejador;

public class Api {
    public static void POST(HttpExchange t) throws IOException {
        try {
            StringBuilder sb = new StringBuilder();
            InputStream ios = t.getRequestBody();
            int i;
            while ((i = ios.read()) != -1) {
                sb.append((char) i);
            }
            JSONObject request = new JSONObject(sb.toString());
            new Manejador(request, null);
            t.sendResponseHeaders(200, request.toString().length());
            OutputStream os = t.getResponseBody();
            os.write(request.toString().getBytes());
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
