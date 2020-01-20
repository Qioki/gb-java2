package server;

import java.sql.*;

public class AuthService {
    private static Connection connection;
    private static Statement stmt;

    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:mydb.db");
            stmt = connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getNickByLoginAndPass(String login, String pass) {
        String sql = String.format("SELECT nickname FROM main where login = '%s' and password = '%s'", login, pass);

        try {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                //return rs.getString(1);
                return login + " " + rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getFriendsInfo(String user) {

        String sql = String.format("SELECT login, nickname FROM main");

        try {
            ResultSet rs = stmt.executeQuery(sql);

            String strFL = "";
            int i = 0;
            while (rs.next()) {
                if(!rs.getString(1).equals(user)) {
                    strFL += rs.getString(1) + ":" + rs.getString(2) + " ";
                }
            }
            return strFL;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
