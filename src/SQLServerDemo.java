//import java.sql.*;
//
//public class SQLServerDemo {
//    public static void main(String[] args) {
//        // SQL Server connection details
//        String url = "jdbc:sqlserver://localhost:1433;databaseName=TestDB;encrypt=true;trustServerCertificate=true";
//        String user = "Admiral";  // Replace with your username
//        String password = "295336";  // Replace with your actual password
//
//        // SQL query to fetch data from the students table
//        String query = "SELECT * FROM Users";
//
//        // Establishing the connection and executing the query
//        try (Connection conn = DriverManager.getConnection(url, user, password);
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(query))
//        {
//
//            // Looping through the result set and printing the data
//            while (rs.next()) {
//                String id = rs.getString("id");
//                String name = rs.getString("username");
//                String email  rs.getString("email");
//
//                System.out.println("ID: " + id + ", Name: " + name + ", Age: " + email);
//            }
//
//        } catch (SQLException e) {
//            System.out.println("Connection failed!");
//            e.printStackTrace();
//        }
//    }
//}
