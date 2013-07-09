package com.thepastimers.Login;

import com.thepastimers.Database.Database;
import com.thepastimers.Database.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 6/24/13
 * Time: 6:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoginData extends Table {
    public static String table = "login";
    
    int id;
    
    public LoginData() {
        id = -1;
    }
    
    String player;
    String password;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static List<LoginData> parseResult(ResultSet result) throws SQLException {
        List<LoginData> ret = new ArrayList<LoginData>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            LoginData p = new LoginData();

            p.setId(result.getInt("id"));
            p.setPlayer(result.getString("player"));
            p.setPlayer(result.getString("password"));

            ret.add(p);
        }

        return ret;
    }

    public boolean delete(Database d) {
        if (id == -1) {
            return true;
        }
        if (d == null) {
            return false;
        }
        return d.query("DELETE FROM " + table + " WHERE ID = " + id);
    }

    public boolean save(Database d) {
        if (d == null) {
            return false;
        }
        if (id == -1) {
            String columns = "(player,password)";
            String values = "('" + d.makeSafe(player) + "','" + d.makeSafe(password) + "')";
            boolean result = d.query("INSERT INTO " + table + columns + " VALUES" + values);

            ResultSet keys = d.getGeneratedKeys();

            if (keys != null && result) {
                try {
                    if (keys.next()) {
                        setId(keys.getInt(1));
                    }
                } catch (SQLException e) {
                    // fallback method
                }
            }

            return result;
        } else {
            StringBuilder query = new StringBuilder();
            query.append("UPDATE " + table + " SET ");

            query.append("player = '" + d.makeSafe(player) + "', ");
            query.append("password = '" + d.makeSafe(password) + "' ");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder builder = new StringBuilder(table);

        builder.append(" int id, string player, string password");

        return builder.toString();
    }
}
