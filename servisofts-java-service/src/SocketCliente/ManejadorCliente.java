package SocketCliente;

import org.json.JSONObject;

import Servisofts.Servisofts;
import component._servicio;

public class ManejadorCliente {

    public static void onMessage(JSONObject action, JSONObject config) {
        switch (action.getString("component")) {
        case "servicio":
            new _servicio(action, null);
            break;
        }
        if (Servisofts.ManejadorCliente != null) {
            Servisofts.ManejadorCliente.apply(action, config);
        }
    }
}