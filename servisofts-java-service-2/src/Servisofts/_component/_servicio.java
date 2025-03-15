package Servisofts._component;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.bouncycastle.util.encoders.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Servisofts.Regex;
import Servisofts.SConfig;
import Servisofts.SConsole;
import Servisofts.SMyIp;
import Servisofts.SSL;
import Servisofts.Server.Server;
import Servisofts.Server.SSSAbstract.SSServerAbstract;
import Servisofts.Server.SSSAbstract.SSSessionAbstract;
import Servisofts.Server.ServerHttp.ImageCompressor;
import Servisofts.SocketCliente.ServiciosHabilitados;
import Servisofts.SocketCliente.SocketCliente;

public class _servicio {
    public static JSONObject ServiciosConocidos = new JSONObject();

    public _servicio(JSONObject obj, SSSessionAbstract sesion) {
        if (!obj.isNull("type")) {
            switch (obj.getString("type")) {
                case "initClient":
                    initClient(obj, sesion);
                    break;
                case "comprimir":
                    comprimir(obj);
                    break;

            }
        }
    }

    private void getEstadoServicio(JSONObject obj, SSSessionAbstract sesion) {
        if (obj.getString("estado").equals("error")) {
            SConsole.error("ERROR EN EL ESTADO DEL SERVICIO ", obj.getString("error"));
            // sesion.onClose(obj);
            return;
        }
        JSONObject servicio = obj.getJSONObject("data");
        // SConsole.warning("Servicio conocido insertado", servicio);
        ServiciosConocidos.put(servicio.getString("nombre"), servicio);
        sesion.setServicio(servicio);
        obj.put("component", "servicio");
        obj.put("socket", "usuario");
        obj.put("type", "initClient");
        obj.put("estado", "exito");
        obj.put("data", servicio);
        sesion.send(obj.toString());

        // services_sessions.put(obj.getString("key"), sesion);
        SConsole.succes("SERVICIO Identificado", "\t|\t", obj.getString("id_session"), "\t|\t",
                servicio.getString("nombre"));
    }

    private void initClient(JSONObject obj, SSSessionAbstract sesion) {
        if (sesion == null) {
            String name = obj.getJSONObject("info").getJSONObject("cert").getString("OU");
            SConsole.succes("SERVER INICIADO:\t\t" + name);
            return;
        }

        SConsole.succes("Indentificado como: " + obj.getString("id") + " - " + obj.getJSONObject("data").toString());
        JSONObject data = obj.getJSONObject("data");
        String OU = data.getJSONObject("cert").getString("OU");

        JSONObject objSend = new JSONObject();
        objSend.put("component", "servicio");
        objSend.put("type", "getEstadoServicio");
        objSend.put("data", data);
        objSend.put("id_session", sesion.getIdSession());
        objSend.put("estado", "cargando");
        if (obj.has("_sincrone_key_" + OU)) {
            objSend.put("_sincrone_key_" + OU, obj.optString("_sincrone_key_" + OU));
        }

        if (ServiciosConocidos.has(OU)) {
            SConsole.warning(
                    "Se inicio un servicio conocido para evitar ir a servicio intentando resolver el error de desconeccion",
                    OU);
            objSend.put("data", ServiciosConocidos.getJSONObject(OU));
            objSend.put("estado", "exito");

            getEstadoServicio(objSend, sesion);
            obj.put("noSend", true);
            return;

        }
        JSONObject resp = SocketCliente.sendSinc("servicio", objSend, 15000);
        if (resp == null) {
            SConsole.error("NO SE PUDO CONECTAR CON EL SERVICIO", OU);
            return;
        } else {
            getEstadoServicio(resp, sesion);
        }

        obj.put("noSend", true);
    }

    private void comprimir(JSONObject obj) {
        // System.out.println(obj.toString());

        try {
            String url = obj.getString("url");
            // String url = "/u01/servisoftsFiles/tapeke";
            // String url = "/u01/servisoftsFiles/tapeke_test/restaurant";
            String[] skipFolders = { "gpx" };
            SConsole.info("Comprimiendo a 128");
            ImageCompressor.compress(url, true, false, true, 128, skipFolders);
            SConsole.info("Comprimiendo a 512");
            ImageCompressor.compress(url, true, false, true, 512, skipFolders);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // STATICS -----

    public static JSONObject initServer() {
        // SConsole.info("ENTRO initServer");
        JSONObject objSend = new JSONObject();
        JSONObject data = new JSONObject();
        data.put("cert", SConfig.getJSON("ssl").getJSONObject("cert"));
        data.put("ip", SMyIp.getLocalIp());
        data.put("ip_public", SMyIp.getPublicIp());
        String fingerp = SSL.getFingerPrint(SConfig.getJSON("ssl").getJSONObject("cert").getString("OU"));
        data.put("fingerp", fingerp);
        objSend.put("component", "servicio");
        objSend.put("type", "initServer");
        objSend.put("data", data);
        objSend.put("estado", "cargando");

        JSONObject response = SocketCliente.getCliente("servicio").sendSync(objSend);
        if (response.optString("estado").equals("exito")) {
            JSONObject servicio = response.getJSONObject("data");
            getServiciosHabilitado(servicio);
            return servicio;
        }
        return null;

    }

    public static JSONObject initClient(SocketCliente socketCliente) {
        // SConsole.info("ENTRO initclient", socketCliente.nombre);
        JSONObject objSend = new JSONObject();
        JSONObject data = new JSONObject();
        data.put("cert", SConfig.getJSON("ssl").getJSONObject("cert"));
        data.put("ip", SMyIp.getLocalIp());
        data.put("ip_public", SMyIp.getPublicIp());
        String fingerp = SSL.getFingerPrint(SConfig.getJSON("ssl").getJSONObject("cert").getString("OU"));
        data.put("fingerp", fingerp);
        objSend.put("component", "servicio");
        objSend.put("type", "initClient");
        objSend.put("data", data);
        objSend.put("estado", "cargando");

        JSONObject response = socketCliente.sendSync(objSend, 1000);
        if (response == null || !response.optString("estado").equals("exito")) {
            // socketCliente.close();
            SConsole.error("SERVER NO INICIADO:\t\t" + socketCliente.nombre,
                    "Asegurece de que el servicio permite sincrone_key solo en las nuevas versiones");
        } else {
            SConsole.succes("SERVER INICIADO:\t\t" + socketCliente.nombre);
        }
        return null;

    }

    public static JSONObject getServiciosHabilitado(JSONObject _servicio) {
        String key_servicio = _servicio.getString("key");
        JSONObject objSend = new JSONObject();
        objSend.put("component", "servicio");
        objSend.put("type", "getServicioHabilitado");
        objSend.put("key", key_servicio);
        objSend.put("estado", "cargando");
        JSONObject response = SocketCliente.getCliente("servicio").sendSync(objSend);

        if (response.optString("estado").equals("exito")) {
            JSONArray servicios = response.getJSONArray("data");
            SConsole.info("---------SERVICIO HABILITADOS---------------------------");
            for (int i = 0; i < servicios.length(); i++) {
                JSONObject servicio_habilitado = servicios.getJSONObject(i);
                JSONObject servicio = servicio_habilitado.getJSONObject("servicio");
                SConsole.info(servicio.getString("ip") + ":" + servicio.getInt("puerto"), "\t",
                        servicio.getString("nombre"));

                if (!servicio.getString("nombre").equals("servicio")) {
                    ServiciosHabilitados.setServicioHabilitado(servicio.getString("nombre"), servicio);
                    try {
                        if (SocketCliente.getCliente(servicio.getString("nombre")) == null) {
                            getPem(servicio.getString("key"));
                            new SocketCliente(servicio.getString("nombre"));
                        }

                    } catch (Exception e) {
                        SConsole.error("Error in getServiciosHabilitado", e.getMessage());
                    }
                }

            }
            SConsole.info("---------------------------------------------------");

            Server.startServer(_servicio);
        }

        return null;

    }

    public static String getPem(String key_servicio) throws Exception {
        JSONObject objSend = new JSONObject();
        objSend.put("component", "servicio");
        objSend.put("type", "getPem");
        objSend.put("estado", "cargando");
        objSend.put("key_servicio", key_servicio);
        JSONObject resp = SocketCliente.getCliente("servicio").sendSync(objSend);
        if (resp.optString("estado").equals("exito")) {
            String pem = resp.getString("data");
            byte[] prvBlob = Base64.decode(pem);
            X509Certificate cert = (X509Certificate) CertificateFactory.getInstance("X.509")
                    .generateCertificate(new ByteArrayInputStream(prvBlob));
            String nombre = Regex.findOU(cert.getSubjectX500Principal().getName());
            SSL.registerPem(nombre, cert);
        }
        return null;
    }
}
