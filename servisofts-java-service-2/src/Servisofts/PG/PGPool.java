package Servisofts.PG;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;
import org.postgresql.util.GT;
import org.postgresql.util.PSQLException;
import org.postgresql.util.PSQLState;

import Servisofts.SConsole;

public class PGPool {

    private int min = 10;
    private int max = 10;
    private PGConnectionProps conf;

    private final LinkedList<Connection> connections = new LinkedList<>();
    private final LinkedList<Connection> usedConnections = new LinkedList<>();

    public PGPool(PGConnectionProps conf) throws SQLException {
        SConsole.warning("Try to instance PGPool to db ",conf.bd_name, conf.ip);
        this.conf = conf;
        for (int i = 0; i < min; i++) {
            createConnection();
        }
    }

    private Connection createConnection() throws SQLException {
        Connection con = DriverManager.getConnection(
                "jdbc:postgresql://" + conf.ip + ":" + conf.puerto + "/" + conf.bd_name,
                conf.user,
                conf.pass);
        connections.add(con);
        SConsole.succes("DB Pool New Conexion = " + connections.size() + "/" + (this.min));
        return con;
    }

    public Connection getConnection() throws SQLException {
        for (int i = 0; i < 10 * 60; i++) {
            Connection con = getConnection1();
            if(con != null) {
                return con;
            }
            // if (!connections.isEmpty()) {
            //     Connection con = connections.get(connections.size() - 1);
            //     if (con != null && !con.isClosed() && con.isValid(5)) {
            //         connections.remove(con);
            //         usedConnections.add(con);
            //         SConsole.log("DB Pool P = " + connections.size() + "/" + (this.min));
            //         return con;
            //     } else {
            //         connections.remove(con);
            //         try {
            //             if(con != null) {
            //                 con.close();
            //             }
            //         } catch (Exception e) {
            //             e.printStackTrace();
            //         }
            //         createConnection();
            //     }

            // }
            SConsole.error("DB Pool ND-" + i + " = " + connections.size() + "/" + (this.min));
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        throw new SQLException("No connections available");
    }

    private synchronized Connection getConnection1() throws SQLException {
        if (!connections.isEmpty()) {
            Connection con = connections.get(connections.size() - 1);
            if (con != null && !con.isClosed() && con.isValid(5)) {
                connections.remove(con);
                usedConnections.add(con);
                // SConsole.log("DB Pool P = " + connections.size() + "/" + (this.min));
                return con;
            } else {
                connections.remove(con);
                try {
                    if(con != null) {
                        con.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                createConnection();
            }
        }
        return null;
    }



    public synchronized void releaseConnection(Connection con) {
        connections.add(con);
        usedConnections.remove(con);
        // SConsole.log("DB Pool L = " + connections.size() + "/" + (this.min));
        // SConsole.log("Conexion desocupada: ", connections.size());
    }

    public synchronized void shutdown() throws SQLException {
        for (Connection connection : connections) {
            connection.close();
        }
        for (Connection connection : usedConnections) {
            connection.close();
        }
        connections.clear();
        usedConnections.clear();
    }

    private boolean isConexionDisponible() {
        // despues se lo mejora 
        // 1. verificar si se puede crear mas conexiones
        if(connections.size() > 0) {
            return true;
        }
        if(getTotalConexiones() > max) {
            // por el momento retorno false
            // mejorar para que valide conexiones que estan cerradas
            return false;
        }

        return false;
    }

    private int getTotalConexiones() {
        return connections.size() + usedConnections.size();
    }

    private void checkClosed(Connection con) throws SQLException {
    if (con.isClosed()) {
        // ver que hacaer
        throw new PSQLException(GT.tr("This connection has been closed."),
            PSQLState.CONNECTION_DOES_NOT_EXIST);
        }
    }

    
    public JSONObject getStats() {
        JSONObject obj = new JSONObject();
        obj.put("connections", connections.size()+usedConnections.size());
        obj.put("available_connections", connections.size());
        obj.put("used_connections", usedConnections.size());
        return obj;
    }

}
