package database;

import java.sql.*;
import java.util.ArrayList;

public class Query {

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

    public static Results runQuery(String[] select,
                                   String from,
                                   String where,
                                   String groupBy,
                                   String having,
                                   String orderBy,
                                   String[] params) {

        Connection conn = Query.connect();
        ArrayList<String[]> results = new ArrayList<String[]>();
        int colsLength = select.length;

        // CONSTRUCT QUERY
        String query = ("SELECT ");
        for (int i = 0; i < colsLength; ++i) {
            query += select[i];
            if (i < colsLength - 1) query += ", ";
        }
        query += " FROM ";
        query += from;
        if (where != null) {
            query += ' ';
            query += where;
        }

        if (groupBy != null) {
            query += ' ';
            query += groupBy;
        }

        if (having != null) {
            query += ' ';
            query += having;
        }

        if (orderBy != null) {
            query += ' ';
            query += orderBy;
        }
        query += ';';

        System.out.println(query);
        // TRY QUERY
        try {
            PreparedStatement stmt = conn.prepareStatement(query);

            if (params != null)
                for (int i = 0; i < params.length; i++) {
                    stmt.setString(i + 1, params[i]);
                }
            ResultSet rs = stmt.executeQuery();
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

        Query.disconnect(conn);
        return new Results(results);
    }

}
