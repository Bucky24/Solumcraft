package com.thepastimers.Metrics;

import com.thepastimers.Database.Database;
import com.thepastimers.Database.Table;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 2/23/13
 * Time: 7:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlayerDeath extends Table {
    public static String table = "player_death";

    int id;

    public PlayerDeath() {
        id = -1;
    }

    String player;
    Date date;
    String cause;

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public static List<PlayerDeath> parseResult(ResultSet result) throws SQLException {
        List<PlayerDeath> ret = new ArrayList<PlayerDeath>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            PlayerDeath p = new PlayerDeath();

            p.setId(result.getInt("id"));
            p.setPlayer(result.getString("player"));
            p.setDate(result.getDate("date"));
            p.setCause(result.getString("cause"));

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
            String columns = "(player,`date`,cause)";
            String values = "('" + d.makeSafe(player) + "','" + date + "','" + d.makeSafe(cause) + "')";
            return d.query("INSERT INTO " + table + columns + " VALUES" + values);
        } else {
            StringBuilder query = new StringBuilder();
            query.append("UPDATE " + table + " SET ");

            query.append("player = '" + d.makeSafe(player) + "'" + ", ");
            query.append("`date` = '" + date + "', ");
            query.append("cause = '" + d.makeSafe(cause) + "' ");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder builder = new StringBuilder(table);

        builder.append(" int id, string player, datetime date, string cause");

        return builder.toString();
    }
}
