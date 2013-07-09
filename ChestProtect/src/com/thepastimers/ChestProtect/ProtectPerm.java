package com.thepastimers.ChestProtect;

import com.thepastimers.Database.Database;
import com.thepastimers.Database.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: derp
 * Date: 10/8/12
 * Time: 8:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProtectPerm extends Table {
    public static String table = "protect_perm";
    
    int id;
    
    public ProtectPerm() {
        id = -1;
    }
    
    int protect;
    String player;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProtect() {
        return protect;
    }

    public void setProtect(int protect) {
        this.protect = protect;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public static List<ProtectPerm> parseResult(ResultSet result) throws SQLException {
        List<ProtectPerm> ret = new ArrayList<ProtectPerm>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            ProtectPerm p = new ProtectPerm();

            p.setId(result.getInt("id"));
            p.setPlayer(result.getString("player"));
            p.setProtect(result.getInt("protect"));
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
            String columns = "(player,protect)";
            String values = "('" + d.makeSafe(player) + "'," + protect + ")";
            return d.query("INSERT INTO " + table + columns + " VALUES" + values);
        } else {
            StringBuilder query = new StringBuilder();
            query.append("UPDATE " + table + " SET ");

            query.append("player = '" + d.makeSafe(player) + "', ");
            query.append("protect = " + protect + " ");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder builder = new StringBuilder(table);

        builder.append(" int id, string player, int protect");

        return builder.toString();
    }
}
