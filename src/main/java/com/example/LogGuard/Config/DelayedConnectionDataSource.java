package com.example.LogGuard.Config;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DelayedConnectionDataSource extends DriverManagerDataSource {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/logDb";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "Phoenix123$";
    private static final int CONNECTION_TIMEOUT = 5000; // Set the desired connection timeout in milliseconds

    @Override
    public Connection getConnection() throws SQLException {
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        throw new SQLException("Connection timeout");
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return getConnection();
    }

    @Override
    public Connection getConnectionFromDriver(String username, String password) throws SQLException {
        return getConnection();
    }
}
