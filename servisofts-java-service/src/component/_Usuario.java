package component;

import java.io.File;
import java.sql.SQLException;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import Server.SSSAbstract.SSServerAbstract;
import Server.SSSAbstract.SSSessionAbstract;
import Servisofts.SConsole;

public class _Usuario {

    // DATA TABLE = usuario

    // key CV
    // user CV
    // pass CV
    // key_persona CV
    // telefono CV
    // correo CV
    // estado INT

    public _Usuario(JSONObject data, SSSessionAbstract session) {
        switch (data.getString("type")) {
        case "identificacion":
            identificaion(data, session);
            break;
        }

    }

    public void identificaion(JSONObject obj, SSSessionAbstract session) {
        String deviceKey = obj.getString("deviceKey");
        JSONObject data = obj.getJSONObject("data");
        try {
            session.setKeyDevice(deviceKey);
            session.setKeyUsuario(data.getString("key"));
            SConsole.info(session.getIdSession() + "\t|\t" + "Session identified with user=(" + data.getString("key")
                    + ") device=(" + deviceKey + ") ");
        } catch (Exception e) {
            SConsole.warning(session.getIdSession() + "\t|\t" + "Session identified without user");
        }
        obj.put("estado", "exito");
    }

}