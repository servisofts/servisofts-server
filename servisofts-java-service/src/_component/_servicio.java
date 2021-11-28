package _component;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.bouncycastle.util.encoders.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

import Server.SSSAbstract.SSServerAbstract;
import Server.SSSAbstract.SSSessionAbstract;
import Server.ServerSocket.ServerSocket;
import Server.ServerSocketWeb.ServerSocketWeb;
import ServerHttp.ServerHttp;
import Servisofts.Regex;
import Servisofts.SConfig;
import Servisofts.SConsole;
import Servisofts.SMyIp;
import Servisofts.SSL;
import SocketCliente.SocketCliente;
// import SocketServer.SocketServer;

public class _servicio {
    public _servicio(JSONObject obj, SSSessionAbstract sesion) {
        if (!obj.isNull("type")) {
            SConsole.warning("Socket Client,", "servicio,", obj.getString("type"));
            switch (obj.getString("type")) {
            case "init":
                init(obj, sesion);
                break;
            case "initServer":
                initServer(obj, sesion);
                break;
            case "initClient":
                initClient(obj, sesion);
                break;
            case "getServicioHabilitado":
                getServicioHabilitado(obj, sesion);
                break;
            case "getEstadoServicio":
                getEstadoServicio(obj, sesion);
                break;
            case "getPem":
                getPem(obj);
                break;

            }
        }
    }

    private void getEstadoServicio(JSONObject obj, SSSessionAbstract sesion) {
        JSONObject servicio = obj.getJSONObject("data");
        SSServerAbstract.getSession(obj.getString("id_session")).setServicio(servicio);
        obj.put("component", "servicio");
        obj.put("socket", "usuario");
        obj.put("type", "initClient");
        obj.put("estado", "exito");
        obj.put("data", servicio);
        SSServerAbstract.getSession(obj.getString("id_session")).send(obj.toString());
        SConsole.succes("SERVICIO Identificado", "\t|\t", obj.getString("id_session"), "\t|\t",
                servicio.getString("nombre"));
    }

    private void getServicioHabilitado(JSONObject obj, SSSessionAbstract sesion) {
        JSONArray data = obj.getJSONArray("data");
        SConsole.info("----SERVICIO HABILITADOS---------------------------");
        JSONObject serviciosH = new JSONObject();
        for (int i = 0; i < data.length(); i++) {
            JSONObject serhabilitado = data.getJSONObject(i);
            SocketCliente.servicios_habilitados.put(serhabilitado.getJSONObject("servicio").getString("nombre"),
                    serhabilitado.getJSONObject("servicio"));

            SConsole.info(serhabilitado.getJSONObject("servicio").getString("nombre") + " - "
                    + serhabilitado.getJSONObject("servicio").getString("ip") + ":"
                    + serhabilitado.getJSONObject("servicio").getInt("puerto"));
            if (!serhabilitado.getJSONObject("servicio").getString("nombre").equals("servicio")) {
                SocketCliente.StartServicio(serhabilitado.getJSONObject("servicio").getString("nombre"));
            }
        }
        SConsole.info("---------------------------------------------------");
    }

    private void initServer(JSONObject obj, SSSessionAbstract sesion) {

        if (obj.getString("estado").equals("exito")) {
            new ServerSocket(obj.getJSONObject("data").getInt("puerto"));
            new ServerSocketWeb(obj.getJSONObject("data").getInt("puerto_ws"));
            ServerHttp.Start(obj.getJSONObject("data").getInt("puerto_http"));
            JSONObject objSend = new JSONObject();
            objSend.put("component", "servicio");
            objSend.put("type", "getServicioHabilitado");
            objSend.put("key", obj.getJSONObject("data").getString("key"));
            objSend.put("estado", "cargando");
            SocketCliente.send("servicio", objSend.toString());
        } else {
            SConsole.error("Socket Client,", "servicio,", obj.getString("type"), "\t|\t", obj.getString("error"));
            System.exit(0);
        }
    }

    private void initClient(JSONObject obj, SSSessionAbstract sesion) {
        if (sesion == null) {
            SConsole.succes("SERVER INICIADOOOOOOO");
            return;
        }
        SConsole.succes("Indentificado como: " + obj.getString("id") + " - " + obj.getJSONObject("data").toString());
        JSONObject data = obj.getJSONObject("data");
        JSONObject objSend = new JSONObject();
        objSend.put("component", "servicio");
        objSend.put("type", "getEstadoServicio");
        objSend.put("data", data);
        objSend.put("id_session", sesion.getIdSession());
        objSend.put("estado", "cargando");
        SocketCliente.send("servicio", objSend, sesion);
        obj.put("noSend", true);
    }

    private void init(JSONObject obj, SSSessionAbstract sesion) {
        JSONObject data = new JSONObject();
        data.put("cert", SConfig.getJSON("ssl").getJSONObject("cert"));
        data.put("ip", SMyIp.getLocalIp());
        data.put("ip_public", SMyIp.getPublicIp());
        String fingerp = SSL.getFingerPrint(SConfig.getJSON("ssl").getJSONObject("cert").getString("OU"));
        data.put("fingerp", fingerp);
        JSONObject objSend = new JSONObject();
        if (obj.getString("socket").equals("servicio")) {
            objSend.put("component", "servicio");
            objSend.put("type", "initServer");
            objSend.put("data", data);
            objSend.put("estado", "cargando");
            SocketCliente.send("servicio", objSend.toString());
        } else {
            objSend.put("component", "servicio");
            objSend.put("type", "initClient");
            objSend.put("data", data);
            objSend.put("estado", "cargando");
            SocketCliente.send(obj.getString("socket"), objSend.toString());
        }

    }

    private void getPem(JSONObject obj) {
        // System.out.println(obj.toString());

        try {
            String pem = obj.getString("data");
            byte[] prvBlob = Base64.decode(pem);
            X509Certificate cert = (X509Certificate) CertificateFactory.getInstance("X.509")
                    .generateCertificate(new ByteArrayInputStream(prvBlob));
            String nombre = Regex.findOU(cert.getSubjectX500Principal().getName());
            SSL.registerPem(nombre, cert);
            SocketCliente.servicios_habilitados.getJSONObject(nombre).put("pem", nombre + ".pem");
        } catch (CertificateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
