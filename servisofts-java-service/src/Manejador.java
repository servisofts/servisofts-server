import org.json.JSONObject;
import Server.SSSAbstract.SSSessionAbstract;
import Servisofts.SConsole;
import component.*;

public class Manejador {
    public static void onMessage(JSONObject obj, SSSessionAbstract session) {
        if (session != null) {
            SConsole.log(session.getIdSession(), "\t|\t", obj.getString("component"), obj.getString("type"));
        }
        if (!obj.isNull("component")) {
            switch (obj.getString("component")) {
            case "permiso": {
                new Permiso(obj, session);
                break;
            }
            case "page": {
                new Page(obj, session);
                break;
            }
            case "rol": {
                new Rol(obj, session);
                break;
            }
            case "rolPermiso": {
                new RolPermiso(obj, session);
                break;
            }
            case "usuarioRol": {
                new UsuarioRol(obj, session);
                break;
            }
            case "usuarioPage": {
                new UsuarioPage(obj, session);
                break;
            }
            }
        }
    }
}
