package Servisofts.Server.ServerSocketWebOld;

import Servisofts.Server.SSSAbstract.SSServerAbstract;
import Servisofts.SConsole;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class ServerSocketWeb extends SSServerAbstract {

    public ServerSocketWeb(int puerto) {
        super(puerto, TIPO_SOCKET_WEB);
    }

    @Override
    public void Start(int puerto) {
        StartConfig2(puerto);

    }
    public void StartConfig1(int puerto) {
        int portNumber = puerto;
        try {
            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        SConsole.warning("Try initializing WebSocket on port ( " + portNumber + " )");
                        Server server = new Server(portNumber);
                        WebSocketHandler wsHandler = new WebSocketHandler() {
                            @Override
                            public void configure(WebSocketServletFactory factory) {
                                factory.getPolicy().setMaxTextMessageSize(1000000);
                                factory.register(Session.class);
                            }
                        };
                        server.setHandler(wsHandler);
                        server.start();
                        SConsole.succes("WebSocket on port ( " + portNumber + " ) is ready!");
                        server.join();
                      
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


    public void StartConfig2(int puerto) {
        int portNumber = puerto;
        try {
            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        SConsole.warning("Try initializing WebSocket on port ( " + portNumber + " )");
                        // Ajustar thread pool
                        QueuedThreadPool threadPool = new QueuedThreadPool(500, 50);
                        // Crear Server con el thread pool
                        Server server = new Server(threadPool);

                        // Configurar el conector
                        ServerConnector connector = new ServerConnector(server);
                        connector.setPort(portNumber);
                        connector.setAcceptQueueSize(1024);
                        connector.setIdleTimeout(30000);

                        server.addConnector(connector);
                        WebSocketHandler wsHandler = new WebSocketHandler() {
                            @Override
                            public void configure(WebSocketServletFactory factory) {
                                // Ajustar políticas
                                factory.getPolicy().setIdleTimeout(30000);     // 30s
                                factory.getPolicy().setMaxTextMessageSize(1000000);
                                factory.getPolicy().setMaxBinaryMessageSize(1000000);
                                factory.register(Session.class); // Registrar tu endpoint
                            }
                        };
                        server.setHandler(wsHandler);
                        server.start();
                        SConsole.succes("WebSocket on port ( " + portNumber + " ) is ready!");
                        server.join();
                      
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

    @Override
    public void printLog(String mensaje) {
        System.out.println(getTipoServer() + ": " + mensaje);
    }

}
