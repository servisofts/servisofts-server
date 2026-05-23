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

    public static int puerto = 0;
    public static int puerto_ws = 0;
    public static int puerto_http = 0;

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

        if(this.servicio.has("puerto") && Server.puerto == 0){ 
            Server.puerto = this.servicio.getInt("puerto");
        }
        if(this.servicio.has("puerto_ws") && Server.puerto_ws == 0){ 
            Server.puerto_ws = this.servicio.getInt("puerto_ws");
        }
        if(this.servicio.has("puerto_http") && Server.puerto_http == 0){ 
            Server.puerto_http = this.servicio.getInt("puerto_http");
        }

        new ServerSocket(Server.puerto);
        new ServerSocketWeb(Server.puerto_ws);
        ServerHttp.Start(Server.puerto_http);
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
