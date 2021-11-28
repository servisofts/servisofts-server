package component;

import java.sql.SQLException;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;
import Server.SSSAbstract.SSSessionAbstract;
import Servisofts.SPGConect;

public class UsuarioRol {

    public UsuarioRol(JSONObject data, SSSessionAbstract session) {
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
            String nombre="";
            String key="";
            if(!obj.isNull("key_usuario")){
                nombre = "key_usuario";
                key = obj.getString("key_usuario");
            }else if(!obj.isNull("key_rol")){
                nombre = "key_rol";
                key = obj.getString("key_rol");
            }
            String consulta =  "select get_all('usuario_rol','"+nombre+"','"+key+"') as json";
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
            String consulta =  "select get_all('usuario_rol','key_usuario','"+key_usuario+"') as json";
            return SPGConect.ejecutarConsultaObject(consulta);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    public void getByKey(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta =  "select usuario_rol_get_by_key('"+obj.getString("key")+"') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (SQLException e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }

    }

    public void registro(JSONObject obj, SSSessionAbstract session) {
        try {
            JSONObject usuario_rol = obj.getJSONObject("data");
            usuario_rol.put("key",UUID.randomUUID().toString());
            usuario_rol.put("fecha_on","now()");
            usuario_rol.put("estado",1);
            SPGConect.insertArray("usuario_rol", new JSONArray().put(usuario_rol));
            obj.put("data", usuario_rol);
            obj.put("estado", "exito");
        } catch (SQLException e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }

    }
    public void editar(JSONObject obj, SSSessionAbstract session) {
        try {
            JSONObject usuario_rol = obj.getJSONObject("data");
            SPGConect.editObject("usuario_rol", usuario_rol);
            obj.put("data", usuario_rol);
            obj.put("estado", "exito");
        } catch (SQLException e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }

    }
}