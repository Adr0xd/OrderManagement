package org.mishu.Data;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionFactory {

    private static final Logger LOGGER =
            Logger.getLogger(ConnectionFactory.class.getName());

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/OrderManagement";
    private static final String USER = "mihai";
    private static final String PASS = "parola123";

    private static final ConnectionFactory instance = new ConnectionFactory();

    private ConnectionFactory() {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "error connection");
        }
    }

    public static Connection getConnection() {
        return instance.createConnection();
    }

    public static void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "error closing connection", e);
            }
        }
    }

    public static void close(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "error closing connection", e);
            }
        }
    }

    public static void close(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "error closing connection", e);
            }
        }
    }

    private Connection createConnection() {
        try {
            return DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "error creating connection", e);
            return null;
        }
    }


}
