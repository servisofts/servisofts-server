package Servisofts;

import org.json.JSONObject;

import Servisofts.Server.SSSAbstract.SSSessionAbstract;
import Servisofts.SocketCliente.ServiciosHabilitados;
import Servisofts.SocketCliente.SocketCliente;

public class Servisofts {
    @FunctionalInterface
    public interface Manejador<T, U> {
        public void apply(T t, U u);
    }

    public static Manejador<JSONObject, SSSessionAbstract> Manejador;
    public static Manejador<JSONObject, JSONObject> ManejadorCliente;
    public static boolean DEBUG = true;

    public static void initialize() throws Exception {
        // System.setProperty("xnio.spi.log", "ALL");
        SConsole.warning("Start Servisofts Java Service 2");
        SConfig.validate();
        SConsole.warning("servisofts-java-service version: 1.0.2");
        SSL.getKeyStore();
        SSL.defaultCert();
        ServiciosHabilitados.init();
        new SocketCliente("servicio");
        if (SConfig.getJSON().has("data_base")) {
            SPGConect.setConexion(SConfig.getJSON("data_base"));
        }

    }
}
