import java.sql.*;

public class DBConnection {
    private static final String url = "jdbc:mysql://localhost:3306/oops_java_project";
    private static final String user = "root";
    private static final String pass = "user@123";

    public static Connection getConnection() throws SQLException{

        // Loading the JDBC Driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found. Include it in your library path!");
            e.printStackTrace();
        }

        return DriverManager.getConnection(url, user, pass);
    }
}

