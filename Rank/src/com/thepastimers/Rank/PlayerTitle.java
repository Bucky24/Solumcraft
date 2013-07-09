package com.thepastimers.Rank;

import com.thepastimers.Database.Database;
import com.thepastimers.Database.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 2/23/13
 * Time: 3:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlayerTitle extends Table {
    public static String table = "title";

    int id;

    public PlayerTitle() {
        id = -1;
    }

    String player;
    String title;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static List<PlayerTitle> parseResult(ResultSet result) throws SQLException {
        List<PlayerTitle> ret = new ArrayList<PlayerTitle>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            PlayerTitle t = new PlayerTitle();
            t.setId(result.getInt("id"));
            t.setPlayer(result.getString("player"));
            t.setTitle(result.getString("title"));

            ret.add(t);
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
        return d.query("DELETE FROM " + table + " WHERE id = " + id);
    }

    public boolean save(Database d) {
        if (d == null) {
            return false;
        }
        if (id == -1) {
            String columns = "(player,title)";
            String values = "('" + d.makeSafe(player) + "','" + d.makeSafe(title)  + "')";
            return d.query("INSERT INTO " + table + columns + " VALUES" + values);
        } else {
            StringBuilder query = new StringBuilder();
            query.append("UPDATE " + table + " SET ");

            query.append("player = '" + d.makeSafe(player) + "'" + ", ");
            query.append("title = '" + d.makeSafe(title) + "'");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder builder = new StringBuilder(table);
        builder.append(" int id, string player, string title");

        return builder.toString();
    }
}
