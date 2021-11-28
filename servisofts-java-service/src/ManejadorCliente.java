import org.json.JSONObject;

import Server.SSSAbstract.SSServerAbstract;
import Servisofts.SConsole;

public class ManejadorCliente {
    public static void onMessage(JSONObject data, JSONObject config) {
        if (!data.isNull("component")) {
            switch (data.getString("component")) {
            default:
                // SConsole.log("ManejadorCliente", data.toString());
                break;
            }
        } else {
            data.put("error", "No existe el componente");
        }
        if (data.has("id_session")) {
            try {
                SSServerAbstract.getSession(data.getString("id_session")).send(data.toString());
            } catch (Exception e) {
               SConsole.error("session no encontrada");
            }
        }
    }

}
