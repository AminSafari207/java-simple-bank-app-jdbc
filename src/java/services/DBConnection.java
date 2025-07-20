package services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection  {
    private static final String url = "jdbc:postgresql://localhost:5432/bank_app";
    private static final String user = "postgres";
    private static final String pass = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }
}
