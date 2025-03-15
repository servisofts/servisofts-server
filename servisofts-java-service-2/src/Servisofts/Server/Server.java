package Servisofts.Server;

import org.json.JSONObject;

import Servisofts.SConsole;
import Servisofts.Server.ServerHttp.ServerHttp;
import Servisofts.Server.ServerSocket.ServerSocket;
import Servisofts.Server.ServerSocketWeb.ServerSocketWeb;
import Servisofts.SocketCliente.ServiciosHabilitados;
import Servisofts.SocketCliente.SocketCliente;

public class Server extends Thread {

    private static Server INSTANCE = null;

    public static Server getInstance() {
        return INSTANCE;
    }
    public static void startServer(JSONObject servicio) {
        if (INSTANCE != null) {
            SConsole.warning("Server is already running");
            return;
        }
        INSTANCE = new Server(servicio);
        INSTANCE.start();
    }

    public JSONObject servicio;

    public Server(JSONObject servicio) {
        this.servicio = servicio;
    }

    @Override
    public void run() {
        super.run();
        // SConsole.succes(this.servicio.getString("nombre"), "Server started");

        new ServerSocket(this.servicio.getInt("puerto"));
        new ServerSocketWeb(this.servicio.getInt("puerto_ws"));
        ServerHttp.Start(this.servicio.getInt("puerto_http"));
        while (true) {
            try {
                Thread.sleep(5000);
                ServiciosHabilitados.reconnectServices();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }
}
