package component;

import org.json.JSONObject;
import Server.SSSAbstract.SSSessionAbstract;
import Servisofts.SConsole;
import Servisofts.Servisofts;

public class Manejador {
    public Manejador(JSONObject data, SSSessionAbstract sesion) {

        if (!data.isNull("component")) {
          
            switch (data.getString("component")) {
            case "servicio":
                new _servicio(data, sesion);
                break;
            case "usuario":
                new _Usuario(data, sesion);
                break;
            }
            if (Servisofts.Manejador != null) {
                Servisofts.Manejador.apply(data, sesion);
            }
        } else {
            data.put("error", "No existe el componente");
        }

    }
}