package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = ConfigLoader.getDbUrl();
    private static final String USER = ConfigLoader.getDbUsername();
    private static final String PASSWORD = ConfigLoader.getDbPassword();

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
