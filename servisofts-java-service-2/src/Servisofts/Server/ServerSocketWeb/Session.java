package Servisofts.Server.ServerSocketWeb;

import org.json.JSONObject;

import Servisofts.SConsole;
import io.undertow.websockets.core.WebSocketChannel;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/")
public class Session {
    private SessionSocketWeb MiSession;

    @OnOpen
    public void onOpen(jakarta.websocket.Session session) {
        this.MiSession = new SessionSocketWeb(session);
    }

    @OnMessage
    public void onMessage(String msg) {
        MiSession.onMessage(msg);
    }

    @OnError
    public void onError(Throwable cause) {
        JSONObject obj = new JSONObject();
        obj.put("estado", "error");
        obj.put("error", cause.getMessage());
        MiSession.onError(obj);
        cause.printStackTrace();
    }

    @OnClose
    public void onClose() {
        // SConsole.warning("Close session");
        JSONObject obj = new JSONObject();
        obj.put("estado", "close");
        // obj.put("statusCode", statusCode);
        // obj.put("reasom", reason);
        MiSession.onClose(obj);
    }

}