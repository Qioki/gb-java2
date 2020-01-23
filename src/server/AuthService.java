package server;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

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
                return rs.getString(1);
                //return login + " " + rs.getString(1);
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

    public static Collection<? extends String> getBlacklist(String login) {
        ArrayList<String> arr = new ArrayList<>();
        String sql = String.format("SELECT blocked.login\n" +
                "FROM blacklist\n" +
                "LEFT JOIN main as client ON client.id = blacklist.id\n" +
                "LEFT JOIN main as blocked ON blocked.id = blacklist.blocked\n" +
                "WHERE client.login = '%s'", login);
        try {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                arr.add(rs.getString(1));
            }
            return arr;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return arr;
    }

    public static void addToBlacklist(String user, String blockUser) {
        String sql = String.format("SELECT blocked.login\n" +
                "FROM blacklist\n" +
                "LEFT JOIN main as client ON client.id = blacklist.id \n" +
                "LEFT JOIN main as blocked ON blocked.id = blacklist.blocked\n" +
                "WHERE client.login = '%s' AND blocked.nickname = '%s'", user, blockUser);
        try {
            System.out.println(1);
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()) {
                System.out.println(2);
                sql = String.format("DELETE FROM blacklist\n" +
                        "WHERE id IN (\n" +
                        "  SELECT main.id\n" +
                        "  FROM blacklist\n" +
                        "  INNER JOIN main ON main.id = blacklist.id\n" +
                        "  WHERE main.login = '%s'\n" +
                        ") AND \n" +
                        "blocked IN (\n" +
                        "  SELECT main.id\n" +
                        "  FROM blacklist\n" +
                        "  INNER JOIN main ON main.id = blacklist.blocked\n" +
                        "  WHERE main.nickname = '%s'\n" +
                        ");", user, blockUser);

                rs = stmt.executeQuery(sql);
            } else {
                System.out.println(3);
                sql = String.format("SELECT id\n" +
                        "  FROM main\n" +
                        "  WHERE main.login = '%s'", user);
                rs = stmt.executeQuery(sql);
                int idClient = 0;
                if (rs.next()) {
                    System.out.println(4);
                    if (!rs.getString(1).equals(user)) {
                        idClient = rs.getInt(1);
                    }
                }
                sql = String.format("SELECT id\n" +
                        "  FROM main\n" +
                        "  WHERE main.nickname = '%s'", blockUser);
                rs = stmt.executeQuery(sql);
                int idBlocked = 0;
                if (rs.next()) {
                    System.out.println(5);
                    if (!rs.getString(1).equals(user)) {
                        idBlocked = rs.getInt(1);
                    }
                }
                System.out.println(idClient + "   "  + idBlocked);
                if(idClient == idBlocked || idClient == 0 || idBlocked == 0) return;
                sql = String.format("INSERT INTO blacklist(id,  blocked)\n" +
                        "SELECT %s, %s", idClient, idBlocked);
                rs = stmt.executeQuery(sql);

            }
            return;
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
