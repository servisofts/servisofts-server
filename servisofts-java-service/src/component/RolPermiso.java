package component;

import java.sql.SQLException;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;
import Server.SSSAbstract.SSSessionAbstract;
import Servisofts.SPGConect;

public class RolPermiso {

    public RolPermiso(JSONObject data, SSSessionAbstract session) {
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
            String key="", nombre="";
            if(!obj.isNull("key_permiso")){
                key = obj.getString("key_permiso");
                nombre = "key_permiso";
            }else if(!obj.isNull("key_rol")){
                key = obj.getString("key_rol");
                nombre = "key_rol";
            }

            String consulta =  "select get_all('rol_permiso','"+nombre+"','"+key+"') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            SPGConect.historico(obj.getString("key_usuario"), key, "rol_permiso_"+nombre+"_getAll", obj);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (SQLException e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }

    }

    public void getByKey(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta =  "select get_by_key('rol_permiso','"+obj.getString("key")+"') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            SPGConect.historico(obj.getString("key_usuario"), obj.getString("key"), "rol_permiso_getByKey", obj);
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
            JSONObject rol_permiso = obj.getJSONObject("data");
            rol_permiso.put("key",UUID.randomUUID().toString());
            rol_permiso.put("fecha_on","now()");
            rol_permiso.put("estado",1);
            SPGConect.insertArray("rol_permiso", new JSONArray().put(rol_permiso));
            SPGConect.historico(obj.getString("key_usuario"), rol_permiso.getString("key"), "rol_permiso_registro", rol_permiso);

            obj.put("send",getSend(rol_permiso.getString("key_permiso"), true));

            obj.put("data", rol_permiso);
            obj.put("estado", "exito");
        } catch (SQLException e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }

    public JSONObject getSend(String key_permiso, boolean is_activo) throws SQLException{
        String consulta = "select to_json(tabla.*) as json "+
                            "from ( "+
                            "select  "+
                            "    ( "+
                            "    select to_json(page.*) as page "+
                            "    from permiso, "+
                            "    page "+
                            "    where permiso.key = '"+key_permiso+"' "+
                            "    and page.key = permiso.key_page "+
                            "    and permiso.estado > 0 "+
                            "    and page.estado > 0 "+
                            "    ), "+
                            "    ( "+
                            "        select to_json(permiso.*) as permiso "+
                            "        from permiso "+
                            "        where permiso.key = '"+key_permiso+"' "+
                            "        and permiso.estado > 0 "+
                            "    ), "+
                            "    ( "+
                            "    select array_to_json(array_agg(tabla.key_usuario)) as usuarios "+
                            "    from ( "+
                            "        select usuario_rol.key_usuario, "+
                            "        count(usuario_rol.key) as cant_rol "+
                            "        from rol_permiso, "+
                            "        usuario_rol "+
                            "        where rol_permiso.key_permiso = '"+key_permiso+"' "+
                            "        and usuario_rol.estado > 0 "+
                            "        and usuario_rol.key_rol = rol_permiso.key_rol "+
                            "        group by usuario_rol.key_usuario "+
                            "    ) tabla "+
                            "    where tabla.cant_rol = 1 "+
                            "    ) "+
                            ") tabla";
        JSONObject obj = SPGConect.ejecutarConsultaObject(consulta);
        obj.put("is_activo", is_activo);
        return obj;
    }

    public void editar(JSONObject obj, SSSessionAbstract session) {
        try {
            
            JSONObject rol_permiso = obj.getJSONObject("data");
            obj.put("send",getSend(rol_permiso.getString("key_permiso"), false));
            
            SPGConect.editObject("rol_permiso", rol_permiso);
            SPGConect.historico(obj.getString("key_usuario"), rol_permiso.getString("key"), "rol_permiso_editar", rol_permiso);
            obj.put("data",rol_permiso);
            obj.put("estado", "exito");

        } catch (SQLException e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }

    }
}