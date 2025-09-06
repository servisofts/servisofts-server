import org.json.JSONObject;

import Servisofts.Server.SSSAbstract.SSServerAbstract;
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
       
    }

}
