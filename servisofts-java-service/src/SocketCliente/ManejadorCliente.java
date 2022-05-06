package SocketCliente;

import org.json.JSONObject;
import Server.SSSAbstract.SSServerAbstract;
import Servisofts.Servisofts;
import _component._servicio;

public class ManejadorCliente {

    public static void onMessage(JSONObject action, JSONObject config) {
        try {
            switch (action.getString("component")) {
                case "servicio":
                    new _servicio(action, null);
                    break;
            }
        } catch (Exception e) {
            // SConsole.error("session no encontrada");
        }

        if (Servisofts.ManejadorCliente != null) {
            Servisofts.ManejadorCliente.apply(action, config);
        }
        if (action.has("id_session")) {
            try {
                SSServerAbstract.getSession(action.getString("id_session")).send(action.toString());
            } catch (Exception e) {
                // SConsole.error("session no encontrada");
            }
        }
    }
}