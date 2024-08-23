package _component;

import org.json.JSONArray;
import org.json.JSONObject;
import Server.SSSAbstract.SSSessionAbstract;
import Servisofts.SConsole;
import Servisofts.SPGConect;
import SocketCliente.SocketCliente;

public class _Test {

    // DATA TABLE = usuario

    // key CV
    // user CV
    // pass CV
    // key_persona CV
    // telefono CV
    // correo CV
    // estado INT

    public _Test(JSONObject data, SSSessionAbstract session) {
        switch (data.getString("type")) {
            case "live":
                live(data, session);
            break;
            case "testServicioHabilitado":
                testServicioHabilitado(data, session);
            break;
            case "restartServicio":
                restartServicio(data, session);
            break;
            case "testBD":
                testBD(data, session);
            break;
            case "restartBD":
                restartBD(data, session);
            break;
            case "closeBD":
                closeBD(data, session);
            break;
            case "callServicio":
                callServicio(data, session);
            break;
            case "callServicioN":
                callServicioN(data, session);
            break;
        }
    }

    public void live(JSONObject obj, SSSessionAbstract session) {
        SConsole.info("live", obj.toString());
        obj.put("estado", "exito");
    }

    public void testServicioHabilitado(JSONObject obj, SSSessionAbstract session) {
        SConsole.info("testServicioHabilitado", obj.toString());
        JSONObject send = new JSONObject();
        send.put("version", "2.0");
        send.put("component", "test");
        send.put("type", "live");
        send.put("estado", "cargando");
        try {
            send = SocketCliente.sendSinc(obj.getString("nombre"), send, 5000);
            obj.put("data", send);
        } catch (Exception e) {
            send.put("estado", "error");
            send.put("error", e.getMessage());
            obj.put("data", send);
        }
        SConsole.info("testServicioHabilitado Respuesta", obj.toString());
    }

    public void restartServicio(JSONObject obj, SSSessionAbstract session) {
        SConsole.info("restartServicio", obj.toString());
        try {
            SocketCliente.reconect(obj.getString("nombre"));
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getMessage());
        }
    }

    public void testBD(JSONObject obj, SSSessionAbstract session) {
        if(SPGConect.isLive()) {
            obj.put("estado", "exito");
        } else {
            obj.put("estado", "error"); 
            obj.put("error", "No Conectado al a Base de Datos"); 
        }
    }

    public void restartBD(JSONObject obj, SSSessionAbstract session) {
        if(SPGConect.restartConexion(true)) {
            obj.put("estado", "exito");
        } else {
            obj.put("estado", "error"); 
            obj.put("error", "No Conectado al a Base de Datos"); 
        }
    }

    public void closeBD(JSONObject obj, SSSessionAbstract session) {
        SPGConect.desconectar();
        obj.put("estado", "exito");
    }

    public void callServicio(JSONObject obj, SSSessionAbstract session) {
        // SConsole.info("callServicio", obj.toString());
        JSONObject send = new JSONObject(obj.getJSONObject("data").toString());
        send.put("version", "2.0");
        // send.put("component", obj.get("component"));
        // send.put("type", obj.get("type"));
        send.put("estado", "cargando");
        try {
            send = SocketCliente.sendSinc(obj.getString("nombre"), send, 5000);
            obj.put("data", send);
        } catch (Exception e) {
            send.put("estado", "error");
            send.put("error", e.getMessage());
            obj.put("data", send);
        }
        // SConsole.info("callServicio Respuesta", obj.toString());
    }

    public void callServicioN(JSONObject obj, SSSessionAbstract session) {
        // SConsole.info("callServicioN", obj.toString());
        int cantidad = obj.getInt("cantidad");
        // JSONArray resp = new JSONArray();
        for (int i = 0; i < cantidad; i++) {
            // SConsole.info("********************", "Intento ", i, "********************");
            JSONObject objNew = new JSONObject(obj.toString());
            callServicio(objNew, session);
            // resp.put(objNew);
            SConsole.info("Intento ", i, objNew.getJSONObject("data").getString("estado"));
            // SConsole.info("*************************************************************");
        }

        // obj.put("response", resp);
    }

}