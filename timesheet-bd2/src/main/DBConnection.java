import java.sql.*;

public class DBConnection {

    // JDBC driver name and database URL
    private static final String DB_URL = "jdbc:sqlserver://uczelnia.database.windows.net:1433;databasename=timesheet-bd2;";

    // Database credentials
    private static final String USER = "lpokorzy";
    private static final String PASSWORD = "Bart0sz!";

    public static void exampleConnect(){
        Connection conn = null;
        Statement stmt = null;

        try{
            // Open a connection
            System.out.println("Connecting to a database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);

            // Execute query
            System.out.println("Creating statement...");
            stmt = conn.createStatement();
            String query = "SELECT TOP (10) * FROM [SalesLT].[CustomerAddress]";
            ResultSet rs = stmt.executeQuery(query);

            // Extract data from result set
            while(rs.next()){
                // Retrieve by column
                int customerID = rs.getInt("CustomerID");
                int addressID = rs.getInt("AddressID");
                String addressType = rs.getString("AddressType");
                String rowguid = rs.getString("rowguid");
                String date = rs.getString("ModifiedDate");

                System.out.print("CustomerID: " + customerID);
                System.out.print(", AddressID: " + addressID);
                System.out.print(", AddressType: " + addressType + "\n");
            }

            rs.close();
            stmt.close();
            conn.close();

        }catch(SQLException sqlExc){
            sqlExc.printStackTrace();
        }catch(Exception exc){
            exc.printStackTrace();
        }finally{
            // close resources
            try{
                if(stmt != null){
                    stmt.close();
                }
            }catch(SQLException sqlExc){
                // nothing we can do
            }

            try{
                if(conn != null){
                    conn.close();
                }
            }catch(SQLException sqlExc){
                sqlExc.printStackTrace();
            }
        }
        System.out.println("Done closing.");
    }

}
