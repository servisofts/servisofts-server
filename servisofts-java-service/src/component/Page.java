package component;

import java.sql.SQLException;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;

import Server.SSSAbstract.SSSessionAbstract;
import Servisofts.SPGConect;

public class Page {

    public Page(JSONObject data, SSSessionAbstract session) {
        switch (data.getString("type")) {
            case "getAll":
                getAll(data, session);
            break;
            case "getByKey":
                getByKey(data, session);
                break;
            case "registro":
                registro(data, session);
            break;
            case "editar":
                editar(data, session);
            break;
        }
    }


    public void getAll(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta =  "select get_all('page','key_servicio','"+obj.getJSONObject("servicio").getString("key")+"') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            SPGConect.historico(obj.getString("key_usuario"), "page_getAll", obj);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (SQLException e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }

    public void getByKey(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta =  "select get_by_key('page','"+obj.getString("key")+"') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            SPGConect.historico(obj.getString("key_usuario"), obj.getString("key"), "page_getByKey", obj);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (SQLException e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }

    public void registro(JSONObject obj, SSSessionAbstract session) {
        try {
            JSONObject page = obj.getJSONObject("data");
            page.put("key",UUID.randomUUID().toString());
            page.put("fecha_on","now()");
            page.put("key_servicio",obj.getJSONObject("servicio").getString("key"));
            page.put("estado","1");
            SPGConect.insertArray("page", new JSONArray().put(page));
            SPGConect.historico(obj.getString("key_usuario"), page.getString("key"), "page_registro", page);
            obj.put("data", page);
            obj.put("estado", "exito");
        } catch (SQLException e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }

    }

    public void editar(JSONObject obj, SSSessionAbstract session) {
        try {
            JSONObject page = obj.getJSONObject("data");
            SPGConect.editObject("page", page);
            SPGConect.historico(obj.getString("key_usuario"), page.getString("key"), "page_editar", obj);
            obj.put("data", page);
            obj.put("estado", "exito");
        } catch (SQLException e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
}