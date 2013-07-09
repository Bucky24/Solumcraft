package com.thepastimers.Kit;

import com.thepastimers.Database.Database;
import com.thepastimers.Database.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 5/30/13
 * Time: 7:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserKit extends Table {
    public static String table = "user_kit";

    int id;

    public UserKit() {
        id = -1;
    }

    String player;
    int kit;
    Timestamp lastUsed;

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

    public int getKit() {
        return kit;
    }

    public void setKit(int kit) {
        this.kit = kit;
    }

    public Timestamp getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(Timestamp lastUsed) {
        this.lastUsed = lastUsed;
    }

    public static List<UserKit> parseResult(ResultSet result) throws SQLException {
        List<UserKit> ret = new ArrayList<UserKit>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            UserKit p = new UserKit();

            p.setId(result.getInt("id"));
            p.setPlayer(result.getString("player"));
            p.setKit(result.getInt("kit"));
            p.setLastUsed(result.getTimestamp("last_used"));

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
            String columns = "(player,kit,last_used)";
            String values = "('" + d.makeSafe(player) + "'," + kit + ",'" + lastUsed + "')";
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
            query.append("kit = " + kit + ", ");
            query.append("last_used = '" + lastUsed + "' ");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder builder = new StringBuilder(table);

        builder.append(" int id, string player, int kit, timestamp last_used");

        return builder.toString();
    }
}
