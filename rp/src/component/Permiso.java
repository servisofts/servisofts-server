package component;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;

import Servisofts.Server.SSSAbstract.SSSessionAbstract;
import Servisofts.SPGConect;
import Servisofts.SUtil;

public class Permiso {

    public Permiso(JSONObject data, SSSessionAbstract session) {
        switch (data.getString("type")) {
            case "getAll":
                getAll(data, session);
                break;
            case "getAllUsuarios":
                getAllUsuarios(data, session);
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
            case "clone_rol":
                clone_rol(data, session);
                break;
        }
    }

    public void getAll(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_all('permiso') as json";
            if (obj.has("key_page")) {
                consulta = "select get_all('permiso','key_page','" + obj.getString("key_page") + "') as json";
            }
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (SQLException e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }

    public void getAllUsuarios(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select usuarios_permisos_get_all('" + obj.getString("key_permiso") + "', '"
                    + obj.getJSONObject("servicio").getString("key") + "') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (SQLException e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }

    public void getByKey(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select permiso_get_by_key('" + obj.getString("key") + "') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            SPGConect.historico(obj.getString("key_usuario"), obj.getString("key"), "permiso_getByKey", obj);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (SQLException e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }

    public void registro(JSONObject obj, SSSessionAbstract session) {
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
            String fecha_on = formatter.format(new Date());

            JSONObject permiso = obj.getJSONObject("data");
            permiso.put("key", UUID.randomUUID().toString());
            permiso.put("fecha_on", fecha_on);
            permiso.put("estado", 1);
            SPGConect.insertArray("permiso", new JSONArray().put(permiso));
            obj.put("data", permiso);
            obj.put("estado", "exito");
        } catch (SQLException e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }

    }

    public void editar(JSONObject obj, SSSessionAbstract session) {
        try {
            JSONObject permiso = obj.getJSONObject("data");
            SPGConect.editObject("permiso", permiso);
            obj.put("data", permiso);
            obj.put("estado", "exito");
        } catch (SQLException e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public void clone_rol(JSONObject obj, SSSessionAbstract session) {
        try {
            String key_rol_from = obj.getString("key_rol_from");
            String key_rol_to = obj.getString("key_rol_to");
            JSONArray arr = SPGConect.ejecutarConsultaArray("select array_to_json(array_agg(rol_permiso.*)) as json\n" + //
                    "from permiso \n" + //
                    "JOIN rol_permiso ON rol_permiso.key_permiso = permiso.\"key\"\n" + //
                    "WHERE rol_permiso.key_rol = '" + key_rol_from + "'\n" + //
                    "AND rol_permiso.estado > 0");
            for (int i = 0; i < arr.length(); i++) {
                arr.getJSONObject(i).put("key", SUtil.uuid());
                arr.getJSONObject(i).put("key_rol", key_rol_to);
                arr.getJSONObject(i).put("fecha_on", SUtil.now());
            }
            SPGConect.insertArray("rol_permiso", arr);
            // JSONObject permiso = obj.getJSONObject("data");
            // SPGConect.editObject("permiso", permiso);
            // obj.put("data", );
            obj.put("estado", "exito");
        } catch (SQLException e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
}