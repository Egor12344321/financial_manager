package org.example.jdbc.dao;

import java.sql.Connection;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("успешно");
        } catch (SQLException e) {
            System.out.println("ошибка подключения: " + e.getMessage());
        }
    }
}