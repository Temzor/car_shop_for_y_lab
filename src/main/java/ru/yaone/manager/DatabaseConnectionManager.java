package ru.yaone.manager;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnectionManager implements AutoCloseable {
    private static Connection connection;

    private DatabaseConnectionManager() { }

    private static String[] getProperties() {
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        final String[] connectionData = new String[3];
        try (InputStream resourceAsStream = loader.getResourceAsStream("liquibase.properties")) {
            Properties properties = new Properties();
            properties.load(resourceAsStream);
            connectionData[0] = properties.getProperty("url");
            connectionData[1] = properties.getProperty("username");
            connectionData[2] = properties.getProperty("password");
        } catch (Exception e) {
            throw new RuntimeException("Some parameters are missing", e);
        }
        return connectionData;
    }

    public static Connection getConnection() {
        if (connection == null || !isConnectionValid()) {
            String[] properties = getProperties();
            try {
                connection = DriverManager.getConnection(properties[0], properties[1], properties[2]);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return connection;
    }

    private static boolean isConnectionValid() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    // Метод для явного закрытия соединения
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null; // Обнуляем переменную после закрытия
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error closing connection", e);
        }
    }
}