package component;

import java.sql.SQLException;
import org.json.JSONObject;

import Servisofts.Server.SSSAbstract.SSSessionAbstract;
import Servisofts.SPGConect;
import Servisofts.SUtil;

public class TipoWidget {

    private static final String COMPONENT = "tipo_widget";

    public TipoWidget(JSONObject data, SSSessionAbstract session) {
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
            String consulta =  "select get_all('"+COMPONENT+"','key_servicio','"+obj.getJSONObject("servicio").getString("key")+"') as json";
            if(obj.has("key_empresa") && !obj.isNull("key_empresa")){
                consulta =  "select get_all('"+COMPONENT+"','key_empresa','"+obj.getString("key_empresa")+"') as json";
            }
            if(obj.has("key_usuario") && !obj.isNull("key_usuario")){
                consulta =  "select get_all('"+COMPONENT+"','key_usuario','"+obj.getString("key_usuario")+"') as json";
            }
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
            String consulta =  "select get_by_key('"+COMPONENT+"','"+obj.getString("key")+"') as json";
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
            
            JSONObject data = obj.getJSONObject("data");
            
            if(!data.has("key")){
                data.put("key", SUtil.uuid());
            }
            data.put("fecha_on",SUtil.now());
            data.put("key_servicio",obj.getJSONObject("servicio").getString("key"));
            data.put("estado",1);
            SPGConect.insertObject(COMPONENT, data);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (SQLException e) {
            if(e.getMessage().indexOf("ERROR: duplicate key value violates unique constraint")>-1){
                // error duplicate key
                editar(obj, session);
            }else{
                obj.put("estado", "error");
                obj.put("error", e.getLocalizedMessage());
                e.printStackTrace();
            }
            
        }

    }

    public void editar(JSONObject obj, SSSessionAbstract session) {
        try {
            JSONObject data = obj.getJSONObject("data");
            SPGConect.editObject(COMPONENT, data);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (SQLException e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
}