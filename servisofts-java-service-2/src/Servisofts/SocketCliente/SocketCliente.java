package Servisofts.SocketCliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.json.JSONObject;

import Servisofts.SConfig;
import Servisofts.SConsole;
import Servisofts.SSL;
import Servisofts.SUtil;
import Servisofts.Server.SSSAbstract.SSSessionAbstract;
import Servisofts._component._servicio;

public class SocketCliente extends Thread {

    public static HashMap<String, SocketCliente> clientes = new HashMap<>();

    public static SocketCliente getCliente(String nombre) {
        return clientes.get(nombre);
    }

    public static JSONObject sendSinc(String nombre, JSONObject data, int timeout) {
        SocketCliente cliente = getCliente(nombre);
        if (cliente != null) {
            return cliente.sendSync(data, timeout);
        }
        return null;

    }

    public static void send(String nombre, JSONObject data, SSSessionAbstract session) {
        if (session != null) {
            data.put("id_session", session.getIdSession());
        }
        SocketCliente cliente = getCliente(nombre);
        cliente.send(data);

    }

    public static void send(String nombre, JSONObject data) {
        SocketCliente cliente = getCliente(nombre);
        cliente.send(data);

    }

    public static void reconect(String string) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'reconect'");
    }

    // CLASS

    public String nombre;
    private JSONObject service;
    private SSLSocket socket;
    private PrintWriter response;
    private BufferedReader request;
    public volatile boolean isOpen = false;

    private SocketClienteSincroned sincroned = new SocketClienteSincroned(this);

    public SocketCliente(String nombre) throws Exception {
        this.nombre = nombre;
        service = ServiciosHabilitados.SERVICIOS_HABILITADOS.getJSONObject(nombre);
        SSLContext ss = SSL.getSSLContext();
        SSLSocketFactory ssf = ss.getSocketFactory();
        socket = (SSLSocket) ssf.createSocket(service.getString("ip"), service.getInt("puerto"));
        socket.setSoTimeout(30000);
        socket.startHandshake();
        this.isOpen = true;
        response = new PrintWriter(socket.getOutputStream(), true);
        request = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        clientes.put(nombre, this);
        SConsole.succes("[SocketCliente]", "[" + this.nombre + "]", "Conexión establecida.");
        start();
    }

    public void onMessage(JSONObject data) {
        new Thread(() -> {
            try {

                if (data.optString("component").equals("servicio")) {
                    switch (data.optString("type")) {
                        case "init":
                            if (data.optString("socket").equals("servicio")) {
                                _servicio.initServer();
                            } else {
                                _servicio.initClient(this);
                            }
                            break;
                    }

                }

            } catch (Exception e) {
                SConsole.error("Error al enviar respuesta: " + e.getMessage());
            }
        }).start();

    };

    public void run() {
        try {
            while (this.isOpen) {
                try {
                    String line = request.readLine();
                    if (line == null) {
                        SConsole.warning("[SocketCliente]", this.nombre, "Conexión cerrada por el servidor.");
                        break;
                    }
                    // System.out.println(line);
                    JSONObject data = new JSONObject(line);
                    this.sincroned.verifySincroneOnMessage(data);
                    this.onMessage(data);
                } catch (SocketTimeoutException e) {
                    // SConsole.warning("[SocketCliente]", this.nombre,"Tiempo de espera agotado.
                    // Verificando conexión...");
                    if (socket.isClosed() || !socket.isConnected()) {
                        break;
                    }
                } catch (SocketException e) {
                    SConsole.error("[SocketCliente]", this.nombre, "Error de conexión: " + e.getMessage());
                    break;
                } catch (IOException e) {
                    SConsole.error("[SocketCliente]", this.nombre, "Error en la lectura del socket: " + e.getMessage());
                    break;
                }
            }
        } finally {
            close();
        }
    }

    public void close() {
        this.isOpen = false;
        clientes.remove(this.nombre);
        try {
            if (request != null)
                request.close();
            if (response != null)
                response.close();
            if (socket != null && !socket.isClosed())
                socket.close();
            SConsole.error("[SocketCliente]", this.nombre, "Conexión cerrada.");
        } catch (IOException e) {
            SConsole.error("[SocketCliente]", this.nombre, "Error al cerrar la conexión: " + e.getMessage());
        }

    }

    public void send(JSONObject data) {
        response.println(data.toString());
        response.flush();
    }

    public JSONObject sendSync(JSONObject data) {
        return this.sincroned.send(data, 30000);
    }

    public JSONObject sendSync(JSONObject data, int timeOut) {
        return this.sincroned.send(data, timeOut);

    }

}
