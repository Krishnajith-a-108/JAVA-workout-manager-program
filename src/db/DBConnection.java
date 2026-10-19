package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
 * MODEL LAYER (database part).
 * The only class that opens a MySQL connection.
 * Set GYM_DB_USER and GYM_DB_PASSWORD to your MySQL credentials.
 */
public class DBConnection {

    private static final String URL = System.getenv().getOrDefault(
            "GYM_DB_URL",
            "jdbc:mysql://localhost:3306/gym_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
    private static final String USER = System.getenv().getOrDefault("GYM_DB_USER", "root");
    private static final String PASSWORD = System.getenv().getOrDefault("GYM_DB_PASSWORD", "");

    public static Connection get() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");   // load the JDBC driver
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL driver jar is missing from the lib folder.");
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
