package com.thepastimers.VoteHandler;

import com.thepastimers.Database.Database;
import com.thepastimers.Database.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: rwijtman
 * Date: 8/13/13
 * Time: 11:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class Votes extends Table {
    public static String table = "votes";
    
    int id;
    
    public Votes() {
        id = -1;
    }
    
    String player;
    Timestamp when;
    boolean redeemed;

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

    public Timestamp getWhen() {
        return when;
    }

    public void setWhen(Timestamp when) {
        this.when = when;
    }

    public boolean isRedeemed() {
        return redeemed;
    }

    public void setRedeemed(boolean redeemed) {
        this.redeemed = redeemed;
    }

    public static List<Votes> parseResult(ResultSet result) throws SQLException {
        List<Votes> ret = new ArrayList<Votes>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            Votes p = new Votes();

            p.setId(result.getInt("id"));
            p.setPlayer(result.getString("player"));
            p.setWhen(result.getTimestamp("when"));
            p.setRedeemed(result.getBoolean("redeemed"));

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
            String columns = "(player,`when`,redeemed)";
            String values = "('" + d.makeSafe(player) + "','" + when + "'," + redeemed + ")";
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
            query.append("`when` = '" + when + "', ");
            query.append("redeemed = " + redeemed + " ");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder builder = new StringBuilder(table);

        builder.append(" int id, string player, timestamp when, boolean redeemed");

        return builder.toString();
    }
}
