package Servisofts.Server.SSSInterface;

import org.json.JSONObject;


public interface SSSessionInterface {

    public boolean isOpen();

    public void onClose(JSONObject obj);

    public void onMessage(String mensaje);

    public void onError(JSONObject obj);

    public void send(String mensaje);


    public JSONObject sendSync(JSONObject mensaje);

    public JSONObject sendSync(JSONObject mensaje, int timeOut);

    // public void send(String mensaje, MensajeSocket mensajeSocket);

    public void printLog(String mensaje);

}
