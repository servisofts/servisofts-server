package Servisofts.PG;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Servisofts.SConsole;

public class PGPool {

    private int min = 10;
    private PGConnectionProps conf;

    private final List<Connection> connections = new ArrayList<>();
    private final List<Connection> usedConnections = new ArrayList<>();

    public PGPool(PGConnectionProps conf) throws SQLException {
        this.conf = conf;
        for (int i = 0; i < min; i++) {
            connections.add(createConnection());
        }
    }

    private Connection createConnection() throws SQLException {
        Connection con = DriverManager.getConnection(
                "jdbc:postgresql://" + conf.ip + ":" + conf.puerto + "/" + conf.bd_name,
                conf.user,
                conf.pass);
        return con;
    }

    public Connection getConnection() throws SQLException {
        for (int i = 0; i < 10 * 60; i++) {
            if (!connections.isEmpty()) {
                Connection con = connections.get(connections.size() - 1);
                if (con != null && !con.isClosed()) {
                    connections.remove(con);
                    usedConnections.add(con);
                    SConsole.log("Conexion ocupada: " + connections.size());
                    return con;
                }

            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        throw new SQLException("No connections available");
    }

    public void releaseConnection(Connection con) {
        connections.add(con);
        usedConnections.remove(con);
        SConsole.log("Conexion desocupada: ", connections.size());
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

}
