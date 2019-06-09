package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;

public class DatabaseConnection {
    private static final ThreadLocal<Connection> con = new DatabaseConnection.ThreadLocalConnection();

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver"); // touch the mysql driver
        } catch (ClassNotFoundException e) {
            Logging.exceptionLog(e.getStackTrace());
        }
    }


    public static Connection getConnection() {
        Connection c = con.get();
        try {
            c.getMetaData();
        } catch (SQLException e) { // connection is dead, therefore discard old object
            con.remove();
            c = con.get();
        }
        return c;
    }

    public static void closeAll() throws SQLException {
        for (final Connection connection : DatabaseConnection.ThreadLocalConnection.allConnections) {
            if (connection != null) {
                connection.close();
            }
        }
    }

    private static class ThreadLocalConnection extends ThreadLocal<Connection> {
        public static final LinkedList<Connection> allConnections = new LinkedList<>();

        @Override
        protected Connection initialValue() {
            try {

                return DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/brain?autoReconnect=true&useSSL=false", "root", "");
            } catch (SQLException e) {
                Logging.exceptionLog(e.getStackTrace());
                return null;
            }
        }
    }
}