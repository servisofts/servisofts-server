package Servisofts;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

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
        stats.put("memory", memoryStatus());
        stats.put("session_socket",
        SSServerAbstract.getServer(SSServerAbstract.TIPO_SOCKET).getSessiones().size());
        stats.put("session_web",
        SSServerAbstract.getServer(SSServerAbstract.TIPO_SOCKET_WEB).getSessiones().size());

        return stats;
    }

    public static JSONObject socketClientStatus() {
        JSONObject SocketClient = new JSONObject();
        SocketCliente.clientes.forEach((nombre, cliente) -> {
            JSONObject socket = new JSONObject();
            socket.put("nombre", nombre);
            SocketClient.put(nombre, cliente.isIdentificado);
        });
        return SocketClient;
    }

    public static JSONObject poolStatus() {
        JSONObject pool = new JSONObject();
        pool.put("connected", SPGConect.pool != null);
        if (SPGConect.pool != null) {
            pool.put("connections", SPGConect.pool.getStats());
        }
        return pool;
    }

    public static JSONObject memoryStatus() {

        JSONObject obj = new JSONObject();
        Runtime runtime = Runtime.getRuntime();
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();


        long memoriaTotal = runtime.totalMemory(); // Memoria total reservada por la JVM
        long memoriaLibre = runtime.freeMemory(); // Memoria libre dentro de la JVM
        long memoriaUsada = memoriaTotal - memoriaLibre; // Memoria efectivamente usada

        obj.put("used", memoriaUsada / (1024 * 1024) + " MB");
        obj.put("free", memoriaLibre / (1024 * 1024) + " MB");
        obj.put("total", memoriaTotal / (1024 * 1024) + " MB");
        obj.put("max", runtime.maxMemory() / (1024 * 1024) + " MB");
        obj.put("threads", threadBean.getThreadCount());
        return obj;
    }

}
