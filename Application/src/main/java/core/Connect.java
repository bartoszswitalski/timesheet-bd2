package core;

import java.sql.*;
import java.util.ArrayList;
import utils.User;

public class Connect {

    public static Connection connect() {
        Connection conn = null;
        try {
            // create a connection to the database
            conn = DriverManager.getConnection("jdbc:sqlite:database.db");
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }

    public static void disconnect(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static Results runQuery(String[] cols, String table, String where) {
        Connection conn = Connect.connect();
        ArrayList<String[]> results = new ArrayList<String[]>();
        int colsLength = cols.length;

        // CONSTRUCT QUERY
        String query = new String("SELECT ");
        for (int i = 0; i < colsLength; ++i) {
            query += cols[i];
            if (i < colsLength - 1) query += ", ";
        }
        query += " FROM ";
        query += table;
        if (!where.equals("")) {
            query += ' ';
            query += where;
        }

        query += ';';

        System.out.println(query);
        // TRY QUERY
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();

            while (rs.next()) {
                String[] result = new String[colsLength];

                for (int i = 0; i < colsLength; ++i) {
                    result[i] = rs.getString(rsmd.getColumnName(i + 1));
                }

                results.add(result);

            }

            rs.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        Connect.disconnect(conn);
        return new Results(results);
    }
}
