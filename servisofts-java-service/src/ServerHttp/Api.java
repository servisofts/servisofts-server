package ServerHttp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.jboss.com.sun.net.httpserver.HttpExchange;
import org.json.JSONObject;
import _component._Manejador;

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
            new _Manejador(request, null);
            byte[] bs = request.toString().getBytes("UTF-8");
            t.sendResponseHeaders(200, bs.length);
            OutputStream os = t.getResponseBody();
            os.write(bs);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
