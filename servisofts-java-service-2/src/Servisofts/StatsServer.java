package Servisofts;

import org.json.JSONArray;
import org.json.JSONObject;

import Servisofts.Server.Server;
import Servisofts.Server.SSSAbstract.SSServerAbstract;
import Servisofts.SocketCliente.ServiciosHabilitados;
import Servisofts.SocketCliente.SocketCliente;

public class StatsServer {
    public static JSONObject getStats() {
        JSONObject stats = new JSONObject();

        Server server = Server.getInstance();
        stats.put("nombre", SConfig.getJSON().optString("nombre"));
        // stats.put("servicio", server.servicio);
        // stats.put("servicios_habilitados",
        // ServiciosHabilitados.SERVICIOS_HABILITADOS);
        stats.put("pool", poolStatus());
        stats.put("SocketClient", socketClientStatus());
        // stats.put("session_socket",
        // SSServerAbstract.getServer(SSServerAbstract.TIPO_SOCKET).getSessiones().size());
        // stats.put("session_web",
        // SSServerAbstract.getServer(SSServerAbstract.TIPO_SOCKET_WEB).getSessiones().size());

        return stats;
    }

    public static JSONObject socketClientStatus() {
        JSONObject SocketClient = new JSONObject();
        SocketCliente.clientes.forEach((nombre, cliente) -> {
            JSONObject socket = new JSONObject();
            socket.put("nombre", nombre);
            SocketClient.put(nombre, cliente.isOpen);
        });
        return SocketClient;
    }

    public static JSONObject poolStatus() {
        JSONObject pool = new JSONObject();
        pool.put("connected", SPGConect.pool != null);
        if (SPGConect.pool != null) {
            pool.put("stats", SPGConect.pool.getStats());
        }
        return pool;
    }
}
