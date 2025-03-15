package Servisofts;

import org.json.JSONObject;

import Servisofts.Server.SSSAbstract.SSSessionAbstract;
import Servisofts.SocketCliente.ServiciosHabilitados;
import Servisofts.SocketCliente.SocketCliente;
import Servisofts._component._servicio;

public class Servisofts {
    @FunctionalInterface
    public interface Manejador<T, U> {
        public void apply(T t, U u);
    }
    
    public static Manejador<JSONObject, SSSessionAbstract> Manejador;
    public static Manejador<JSONObject, JSONObject> ManejadorCliente;
    public static boolean DEBUG = true;

    public static void initialize() {
        SConsole.warning("Start Servisofts Java Service 2");
        try {
            SConfig.validate();
            SSL.getKeyStore();
            SSL.defaultCert();
            ServiciosHabilitados.init();
            new SocketCliente("servicio");
            SPGConect.setConexion(SConfig.getJSON("data_base"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
