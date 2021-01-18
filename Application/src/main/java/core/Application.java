import java.sql.*;

public class Application {

    public static void connect() {
        Connection conn = null;
        try {
            // create a connection to the database
            conn = DriverManager.getConnection("jdbc:sqlite:database.db");
            System.out.println("Connection to SQLite has been established.");

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM project");

            System.out.println("ID\t Name\t\t State");

            while ( rs.next() ) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String selector = rs.getString("selector");
                System.out.println(id+"\t"+name+"\t\t"+selector);
            }
            rs.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        connect();
    }
}
