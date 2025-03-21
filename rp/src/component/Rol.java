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

public class Rol {

    public Rol(JSONObject data, SSSessionAbstract session) {
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
            case "registros_por_rol":
                registros_por_rol(data, session);
            break;
            
        }
    }


    public void getAll(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta =  "select get_all('rol','key_servicio','"+obj.getJSONObject("servicio").getString("key")+"') as json";
            if(obj.has("key_empresa")){
                consulta =  "select get_all('rol','key_empresa   ','"+obj.getString("key_empresa")+"') as json";
            }
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (SQLException e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }

    public static JSONObject getAll(String key_usuario) {
        try {
            String consulta =  "select rol_get_all('"+key_usuario+"') as json";
            return SPGConect.ejecutarConsultaObject(consulta);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void getByKey(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta =  "select rol_get_by_key('"+obj.getString("key")+"') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
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
            JSONObject rol = obj.getJSONObject("data");
            rol.put("key",UUID.randomUUID().toString());
            rol.put("fecha_on",fecha_on);
            rol.put("key_servicio",obj.getJSONObject("servicio").getString("key"));
            rol.put("estado","1");
            SPGConect.insertArray("rol", new JSONArray().put(rol));
            obj.put("data", rol);
            obj.put("estado", "exito");
        } catch (SQLException e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }

    }

    public void editar(JSONObject obj, SSSessionAbstract session) {
        try {
            JSONObject rol = obj.getJSONObject("data");
            SPGConect.editObject("rol", rol);
            obj.put("data", rol);
            obj.put("estado", "exito");
        } catch (SQLException e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public void registros_por_rol(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta =  "select registros_por_rol('key_servicio','"+obj.getJSONObject("servicio").getString("key")+"') as json";
            if(obj.has("key_empresa")){
                consulta =  "select registros_por_rol('key_empresa','"+obj.getString("key_empresa")+"') as json";
            }
            JSONArray data = SPGConect.ejecutarConsultaArray(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (SQLException e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }
}
