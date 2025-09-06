package component;

import java.sql.SQLException;
import org.json.JSONObject;
import Servisofts.Server.SSSAbstract.SSSessionAbstract;
import Servisofts.SPGConect;

public class UsuarioPage {

    public UsuarioPage(JSONObject data, SSSessionAbstract session) {
        switch (data.getString("type")) {
            case "getAll":
                getAll(data, session);
                break;
            case "getAllRol":
                getAllRol(data, session);
                break;
        }
    }

    public void getAll(JSONObject obj, SSSessionAbstract session) {
        try {
            if (!obj.has("servicio")) {
                obj.put("error", "no se encontro servicio");
                obj.put("estado", "error");
                return;
            }

            String consulta = "select usuario_page_get_all('" + obj.getString("key_usuario") + "') as json";

            if (obj.has("key_empresa")) {
                consulta = "select usuario_page_get_all('" + obj.getString("key_usuario") + "', '"
                        + obj.getString("key_empresa") + "') as json";
            }

            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("rol", Rol.getAll(obj.getString("key_usuario")));

            obj.put("estado", "exito");
        } catch (SQLException e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }

    }

    public void getAllRol(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select usuario_page_get_all_rol('" + obj.getString("key_rol") + "') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (SQLException e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }

    }
}