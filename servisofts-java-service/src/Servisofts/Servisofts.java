package Servisofts;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.util.function.Function;

import javax.websocket.OnMessage;

import org.json.JSONException;
import org.json.JSONObject;

import Server.SSSAbstract.SSSessionAbstract;
import SocketCliente.SocketCliente;

public class Servisofts {
    @FunctionalInterface
    public interface Manejador<T, U> {
        public void apply(T t, U u);
    }

    public static Manejador<JSONObject, SSSessionAbstract> Manejador;
    public static Manejador<JSONObject, JSONObject> ManejadorCliente;

    public static void initialize() throws KeyStoreException, JSONException, CertificateException, IOException {
        SConsole.succes("------------------------------------------------------------------------");
        SConsole.succes("------------------------------------------------------------------------");
        SConsole.succes("                                                                        ");
        SConsole.succes("                                SERVISOFTS                              ");
        SConsole.succes("                                                                        ");
        SConsole.succes("------------------------------------------------------------------------");
        SConsole.succes("------------------------------------------------------------------------");
        initSConfig();
        initSSL();
        initDefaultCert();
        if (!SConfig.getJSON("ssl").getJSONObject("cert").getString("OU").equals("servicio")) {
            initServicioCert();
        }
        initSPGConect();
        initSocketClient();

    }

    public static void initSConfig() {
        SConsole.warning("Initializing", SConfig.configFile);
        if (!SConfig.validate()) {
            SConsole.error("Failed to initilizing", SConfig.configFile);
            System.exit(1);
        }
        SConsole.succes(SConfig.configFile, "ready!");
    }

    public static void initSSL() {
        SConsole.warning("Initilizing JKS from ./" + SConfig.getJSON("ssl").getString("nombre_jks") + ".jks");
        SSL.getKeyStore();
        SConsole.succes("JSK ready!");
    }

    public static void initDefaultCert() throws KeyStoreException, JSONException, CertificateException, IOException {
        if (!SSL.defaultCert()) {
            SConsole.error("Failed to load default certificate.");
            System.exit(1);
        }
        JSONObject certificado = SSL.verificarCertificado(SConfig.getJSON("ssl").getJSONObject("cert").getString("OU"));
        SConsole.info("");
        SConsole.info("---SSL Certificate------------------------------");
        SConsole.log("         OU = " + certificado.getString("OU"));
        SConsole.info("");
        SConsole.log("FingerPrint = " + certificado.getString("fingerp"));
        SConsole.info("");
        SConsole.log("        PEM = " + certificado.getString("pem"));
        SConsole.info("------------------------------");
        SConsole.info("");
    }

    public static void initServicioCert() throws KeyStoreException, JSONException, CertificateException, IOException {
        SConsole.warning("Initilizing certificate ( OU=servicio )");
        if (!SSL.servicioCert()) {
            System.out.println("Server closed.");
            System.exit(1);
        }
    }

    public static void initSPGConect() throws KeyStoreException, JSONException, CertificateException, IOException {
        SPGConect.setConexion(SConfig.getJSON("data_base"));
    }

    public static void initSocketClient() {
        SConsole.warning("Initilizing Socket Client");
        SocketCliente.enableReconect(true);
        SocketCliente.Start(SConfig.getJSON("socket_client").getJSONObject("servicio"));
    }
}
