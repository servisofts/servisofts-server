package Servisofts.SocketCliente;

import org.json.JSONObject;

import Servisofts.SConfig;
import Servisofts.SConsole;
import Servisofts.SSL;

public class ServiciosHabilitados {
    public static JSONObject SERVICIOS_HABILITADOS = new JSONObject();

    public static void init() throws Exception {
        JSONObject socket_clients_in_config = SConfig.getJSON("socket_client");
        JSONObject servicio = socket_clients_in_config.getJSONObject("servicio");
        ServiciosHabilitados.setServicioHabilitado("servicio", servicio);
        SERVICIOS_HABILITADOS.put("servicio", servicio);
        SSL.servicioCert();
    }

    public static JSONObject getServicioHabilitado(String key_servicio) {
        return SERVICIOS_HABILITADOS.getJSONObject(key_servicio);
    }

    public static void setServicioHabilitado(String key_servicio, JSONObject servicio) {
        SERVICIOS_HABILITADOS.put(key_servicio, servicio);
    }

    public static void reconnectServices() {
        SERVICIOS_HABILITADOS.keys().forEachRemaining(key -> {
            // JSONObject servicio = SERVICIOS_HABILITADOS.getJSONObject(key);
            SocketCliente session = SocketCliente.getCliente(key);
            if (session == null || !session.isOpen) {

                try {
                    SConsole.warning("Reconnect Service: " + key, "Try to reconnect");
                    new SocketCliente(key);
                } catch (Exception e) {
                    SConsole.error("Reconnect Service: " + key, e.getMessage());
                }
            } 

        });
    }
}
