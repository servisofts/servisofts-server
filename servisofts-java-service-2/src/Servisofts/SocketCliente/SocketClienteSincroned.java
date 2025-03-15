package Servisofts.SocketCliente;

import java.util.HashMap;

import org.json.JSONObject;

import Servisofts.SConfig;
import Servisofts.SUtil;

public class SocketClienteSincroned {
    SocketCliente socketCliente;

    HashMap<String, JSONObject> sincrones = new HashMap<>();

    public SocketClienteSincroned(SocketCliente socketCliente) {
        this.socketCliente = socketCliente;
    }

    public void verifySincroneOnMessage(JSONObject data) {
        String nombre = SConfig.getJSON().getString("nombre");
        if (data.has("_sincrone_key_" + nombre)) {
            String key = data.getString("_sincrone_key_" + nombre);
            if (sincrones.containsKey(key)) {
                JSONObject obj = sincrones.get(key);
                obj.put("response", data);
            }
        }
    }

    public JSONObject send(JSONObject data, int timeout)  {
        String nombre = SConfig.getJSON().getString("nombre");
        String key = SUtil.uuid();
        data.put("_sincrone_key_" + nombre, key);
        sincrones.put(key, new JSONObject().put("key", key).put("timeout", timeout));
        this.socketCliente.send(data);
        while (true) {
            if (sincrones.containsKey(key)) {
                JSONObject obj = sincrones.get(key);
                if (obj.has("response")) {
                    JSONObject response = obj.getJSONObject("response");
                    sincrones.remove(key);
                    response.remove("_sincrone_key_" + nombre);
                    return response;
                }
                if (obj.getInt("timeout") <= 0) {
                    sincrones.remove(key);
                    return null;
                }
                obj.put("timeout", obj.getInt("timeout") - 100);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    return null;
                }
            } else {
                return null;
            }
        }
    }
}
