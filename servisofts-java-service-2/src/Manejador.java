import org.json.JSONObject;
import Servisofts.Server.SSSAbstract.SSSessionAbstract;
import Servisofts.SConsole;

public class Manejador {
    public static void onMessage(JSONObject obj, SSSessionAbstract session) {
        SConsole.log("Manejador","\t|\t", obj.optString("component"), obj.optString("type"));
        // if (!obj.isNull("component")) {
        // switch (obj.getString("component")) {
        // case "permiso": new Permiso(obj, session); break;
        // case "page": new Page(obj, session); break;
        // case "tipoWidget": new TipoWidget(obj, session); break;
        // case "widget": new Widget(obj, session); break;
        // case "rol": new Rol(obj, session); break;
        // case "rolPermiso": new RolPermiso(obj, session); break;
        // case "usuarioRol": new UsuarioRol(obj, session); break;
        // case "usuarioPage": new UsuarioPage(obj, session); break;
        // }
        // }
    }
}
