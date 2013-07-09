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
 * Time: 11:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class VoteSettings extends Table {
    public static String table = "vote_settings";

    int id;

    public VoteSettings() {
        id = -1;
    }

    String player;
    String reward;

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

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public static List<VoteSettings> parseResult(ResultSet result) throws SQLException {
        List<VoteSettings> ret = new ArrayList<VoteSettings>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            VoteSettings p = new VoteSettings();

            p.setId(result.getInt("id"));
            p.setPlayer(result.getString("player"));
            p.setReward(result.getString("reward"));

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
            String columns = "(player,reward)";
            String values = "('" + d.makeSafe(player) + "','" + d.makeSafe(reward) + "')";
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
            query.append("reward = '" + d.makeSafe(reward) + "' ");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder builder = new StringBuilder(table);

        builder.append(" int id, string player, string reward");

        return builder.toString();
    }
}
