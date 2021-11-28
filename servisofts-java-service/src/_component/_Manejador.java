package _component;

import org.json.JSONObject;
import Server.SSSAbstract.SSSessionAbstract;
import Servisofts.Servisofts;
import SocketCliente.SocketCliente;

public class _Manejador {
    public _Manejador(JSONObject data, SSSessionAbstract sesion) {

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
            if (data.has("service")) {
                SocketCliente.send(data.getString("service"), data, sesion);
            }
        } else {
            data.put("error", "No existe el componente");
        }

    }
}