package com.thepastimers.VoteHandler;

import com.thepastimers.Database.Database;
import com.thepastimers.Database.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 7/5/13
 * Time: 11:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class VoteCredits extends Table {
    public static String table = "vote_credits";

    int id;

    public VoteCredits() {
        id = -1;
    }

    String player;
    int credits;

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

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public static List<VoteCredits> parseResult(ResultSet result) throws SQLException {
        List<VoteCredits> ret = new ArrayList<VoteCredits>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            VoteCredits p = new VoteCredits();

            p.setId(result.getInt("id"));
            p.setPlayer(result.getString("player"));
            p.setCredits(result.getInt("credits"));

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
            String columns = "(player,credits)";
            String values = "('" + d.makeSafe(player) + "'," + credits + ")";
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
            query.append("credits = " +  credits + " ");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder builder = new StringBuilder(table);

        builder.append(" int id, string player, int credits");

        return builder.toString();
    }
}
