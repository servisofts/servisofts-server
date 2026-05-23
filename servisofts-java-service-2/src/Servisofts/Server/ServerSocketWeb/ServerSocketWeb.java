package Servisofts.Server.ServerSocketWeb;

import Servisofts.Server.SSSAbstract.SSServerAbstract;
import Servisofts.SConsole;

import io.undertow.Undertow;
import io.undertow.Handlers;
import io.undertow.server.handlers.PathHandler;
import io.undertow.util.Headers;
import io.undertow.websockets.core.*;
import io.undertow.websockets.spi.WebSocketHttpExchange;

import java.io.IOException;

import org.xnio.OptionMap;
import org.xnio.Xnio;
import org.xnio.XnioWorker;


import io.undertow.servlet.api.DeploymentManager;
import io.undertow.websockets.jsr.WebSocketDeploymentInfo;



import static io.undertow.servlet.Servlets.defaultContainer;
import static io.undertow.servlet.Servlets.deployment;
import static io.undertow.websockets.jsr.WebSocketDeploymentInfo.ATTRIBUTE_NAME;

public class ServerSocketWeb extends SSServerAbstract {

    public ServerSocketWeb(int puerto) {
        super(puerto, TIPO_SOCKET_WEB);
    }

    @Override
    public void Start(int puerto) {
        StartConfig1(puerto);

    }

    public void StartConfig1(int puerto) {
        int portNumber = puerto;
        try {
            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        SConsole.warning("Try initializing WebSocket on port ( " + portNumber + " )");

                        final Xnio xnio = Xnio.getInstance("nio", Undertow.class.getClassLoader());
                        final XnioWorker xnioWorker = xnio.createWorker(OptionMap.builder().getMap());
                        final WebSocketDeploymentInfo webSockets = new WebSocketDeploymentInfo()
                                .addEndpoint(Session.class)
                                .setWorker(xnioWorker);
                        final DeploymentManager deployment = defaultContainer()
                                .addDeployment(deployment()
                                        .setClassLoader(ServerSocketWeb.class.getClassLoader())
                                        .setContextPath("/")
                                        .setDeploymentName("embedded-websockets")
                                        .addServletContextAttribute(ATTRIBUTE_NAME, webSockets));

                        deployment.deploy();
                        Undertow.builder().addListener(portNumber, "0.0.0.0")
                                .setHandler(deployment.start())
                                .build()
                                .start();

                        System.out.println("Undertow WebSocket server started at ws://localhost:"+portNumber+"/ws");

                    } catch (Exception e) {
                        e.printStackTrace();
                        // TODO: handle exception
                    }
                }
            };
            t.start();
        } catch (Exception e) {
            e.printStackTrace();
            // printLog("Error: " + e.getLocalizedMessage());
        }

    }

    // private static PathHandler createWebSocketHandler() {
    //     // El path "/ws" aceptará conexiones WebSocket
    //     return Handlers.path().addPrefixPath("/ws", Handlers.websocket((exchange, channel) -> {
    //         // "onOpen": Se invoca cuando un nuevo cliente termina el handshake WebSocket
    //         Session session = new Session();
    //         System.out.println("New WebSocket connection opened: " + channel);
    //         session.onOpen(channel);

    //         // onClose => usas channel.getCloseSetter()
    //         channel.getCloseSetter().set(c -> {
    //             System.out.println("WebSocket connection closed: " + channel);
    //             session.onClose(100, "Cierre");
    //         });

    //         // onMessage => usas un ReceiveListener
    //         channel.getReceiveSetter().set(new AbstractReceiveListener() {
    //             @Override
    //             protected void onFullTextMessage(WebSocketChannel ch, BufferedTextMessage message) {
    //                 String text = message.getData();
    //                 System.out.println("Received: " + text);
    //                 session.onMessage(text);
    //                 // WebSockets.sendText("Echo: " + text, ch, null);
    //             }

    //             @Override
    //             protected void onError(WebSocketChannel channel, Throwable error) {
    //                 // onError
    //                 System.err.println("Error in WebSocket: " + error.getMessage());
    //                 // Manejo de la excepción...
    //                 session.onError(error);
    //             }
    //         });
    //         channel.resumeReceives();
    //     }));
    // }

    @Override
    public void printLog(String mensaje) {
        System.out.println(getTipoServer() + ": " + mensaje);
    }

}
