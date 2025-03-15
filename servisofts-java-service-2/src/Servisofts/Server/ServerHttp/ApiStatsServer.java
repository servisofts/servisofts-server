package Servisofts.Server.ServerHttp;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.sql.rowset.serial.SerialBlob;

import org.jboss.com.sun.net.httpserver.HttpExchange;
import org.json.JSONObject;

import Servisofts.StatsServer;
import Servisofts._component._Manejador;

public class ApiStatsServer {
    public static void GET(HttpExchange t) throws IOException {
        try {

            t.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            t.getResponseHeaders().add("Access-Control-Allow-Headers", "*");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(t.getRequestBody(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String i;
            while ((i = bufferedReader.readLine()) != null) {
                sb.append(i);
            }
            bufferedReader.close();
            JSONObject response = StatsServer.getStats();
            byte[] bs = response.toString().getBytes("UTF-8");
            t.sendResponseHeaders(200, bs.length);
            OutputStream os = t.getResponseBody();
            os.write(bs);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
