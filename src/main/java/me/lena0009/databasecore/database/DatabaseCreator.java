package me.lena0009.databasecore.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class DatabaseCreator {
    private Connection connection;

    public DatabaseCreator (String host, String port, String database, String username, String password) throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false", username, password);
    }

    public Connection getConnection() {
        return connection;
    }

    public boolean isConnected() {
        return (connection == null ? false : true);
    }

    public void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public abstract void createTable();
}
