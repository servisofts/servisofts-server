package _component;

import org.json.JSONObject;
import Server.SSSAbstract.SSSessionAbstract;
import Servisofts.SConsole;
import Servisofts.Servisofts;
import SocketCliente.SocketCliente;

public class _Manejador {
    public _Manejador(JSONObject data, SSSessionAbstract sesion) {
        try{
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
                    String service = data.getString("service");
                    data.remove("service");
                    SocketCliente.send(service, data, sesion);
                }
            } else {
                data.put("error", "No existe el componente");
            }
        }catch(Exception e){
            SConsole.error("Error en el manejador de componentes: " + e.getMessage());
        }
    }
}