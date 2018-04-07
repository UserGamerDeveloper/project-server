package com.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseUtil {
    private static final Connection sConnection = createConnection();

    static Connection createConnection(){
        try {
            final String url = "jdbc:mysql://localhost:3306/database?user=root&password=123456&autoReconnect=true&useSSL=false";
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    static Connection getConnection(){
        return sConnection;
    }
}
