package Servisofts;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONObject;

public class SConfig {
    private static JSONObject config = null;
    public static String configFile = "config.json";

    public static boolean validate() throws Exception {
        JSONObject obj = getJSON();
        if (obj != null) {
            if (!obj.has("nombre")) {
                throw new Exception("Parameter ( nombre ) type String not found.");
            }
            if (!obj.has("ss")) {
                throw new Exception("Parameter ( ss ) type String not found.");
            }
            if (!obj.has("ssl")) {
                throw new Exception("Parameter ( ssl ) type { nombre_jks, pass_jks, cert } not found.");
            } else {
                JSONObject ssl = obj.getJSONObject("ssl");
                if (!ssl.has("nombre_jks")) {
                    throw new Exception("Parameter ( nombre_jks ) type String not found.");
                }
                if (!ssl.has("pass_jks")) {
                    throw new Exception("Parameter ( pass_jks ) type String not found.");
                }
                if (!ssl.has("cert")) {
                    throw new Exception("Parameter ( cert ) type String not found.");
                }
                String OU = ssl.getJSONObject("cert").getString("OU");
                if (OU == null || OU.isEmpty()) {
                    throw new Exception("Parameter ( ssl/cert/OU ) type String not found.");
                }
                if (!OU.equals("servicio")) {
                    if (!obj.has("socket_client")) {
                        throw new Exception("Parameter ( socket_client ) type { servicio, reconectTime } not found.");
                    }
                    if (!obj.getJSONObject("socket_client").has("servicio")) {
                        throw new Exception("Parameter ( socket_client/servicio ) type { puerto, ip, cert } not found.");
                    }
                    if (!obj.getJSONObject("socket_client").getJSONObject("servicio").has("cert")) {
                        throw new Exception("Parameter ( socket_client/servicio/cert ) type { OU, pem } not found.");
                    }
                }
            }
            // if (!obj.has("data_base")) {
            //     SConsole.error("Parameter ( data_base ) type { bd_name, ip, puerto, user, pass } not found.");
            //     return false;
            // }
            return true;
        }
        return false;

    }

    public static JSONObject getJSON() {
        try {
            if (config == null) {

                FileReader file;
                file = new FileReader(configFile);
                int valor = file.read();
                String configJson = "";
                while (valor != -1) {
                    configJson = configJson + String.valueOf(((char) valor));
                    valor = file.read();
                    // System.out.print(".");
                }
                config = new JSONObject(configJson);
            }
        } catch (Exception e) {
            SConsole.error("Error in configuration file", e.getMessage());
            return null;
        }
        return config;
    }

    public static JSONObject getJSON(String key) {

        return getJSON().getJSONObject(key);
    }
}
