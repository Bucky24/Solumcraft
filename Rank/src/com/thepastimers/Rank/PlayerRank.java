package com.thepastimers.Rank;

import com.thepastimers.Database.Database;
import com.thepastimers.Database.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: derp
 * Date: 10/1/12
 * Time: 8:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlayerRank extends Table {
    public static String table = "rank";

    int id;

    public PlayerRank() {
        id = -1;
    }

    String player;
    String rank;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public static List<PlayerRank> parseResult(ResultSet result) throws SQLException {
        List<PlayerRank> ret = new ArrayList<PlayerRank>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            PlayerRank r = new PlayerRank();
            r.setId(result.getInt("id"));
            r.setPlayer(result.getString("player"));
            r.setRank(result.getString("rank"));

            ret.add(r);
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
            String columns = "(player,rank)";
            String values = "('" + d.makeSafe(player) + "','" + d.makeSafe(rank)  + "')";
            return d.query("INSERT INTO " + table + columns + " VALUES" + values);
        } else {
            StringBuilder query = new StringBuilder();
            query.append("UPDATE " + table + " SET ");

            query.append("player = '" + d.makeSafe(player) + "'" + ", ");
            query.append("rank = '" + d.makeSafe(rank) + "'");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder builder = new StringBuilder(table);
        builder.append(" int id, string player, string rank");

        return builder.toString();
    }
}
