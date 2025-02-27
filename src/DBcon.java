import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBcon {
    private static final String URL = "jdbc:mysql://localhost:3306/sas_v3"; // Replace with your DB URL
    private static final String USERNAME = "root"; // Replace with your DB username
    private static final String PASSWORD = "Ahlawi-2015"; // Replace with your DB password
    public Connection getConnection() {
        Connection connection = null;
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establish the connection
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Database connection successful!");
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
        return connection;
    }
}
