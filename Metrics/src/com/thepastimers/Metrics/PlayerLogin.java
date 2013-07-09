package com.thepastimers.Metrics;

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
 * Date: 3/2/13
 * Time: 4:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlayerLogin extends Table {
    public static String table = "player_login";

    int id;

    public PlayerLogin() {
        id = -1;
    }

    String player;
    String event;
    Timestamp date;
    int x;
    int y;
    int z;

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

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public static List<PlayerLogin> parseResult(ResultSet result) throws SQLException {
        List<PlayerLogin> ret = new ArrayList<PlayerLogin>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            PlayerLogin p = new PlayerLogin();

            p.setId(result.getInt("id"));
            p.setPlayer(result.getString("player"));
            p.setEvent(result.getString("event"));
            p.setDate(result.getTimestamp("date"));
            p.setX(result.getInt("x"));
            p.setY(result.getInt("y"));
            p.setZ(result.getInt("z"));

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
            String columns = "(player,`date`,event,x,y,z)";
            String values = "('" + d.makeSafe(player) + "','" + date + "','" + d.makeSafe(event) + "'," + x + "," + y + "," + z + ")";
            return d.query("INSERT INTO " + table + columns + " VALUES" + values);
        } else {
            StringBuilder query = new StringBuilder();
            query.append("UPDATE " + table + " SET ");

            query.append("player = '" + d.makeSafe(player) + "'" + ", ");
            query.append("`date` = '" + date + "', ");
            query.append("event = '" + d.makeSafe(event) + "', ");
            query.append("x = " + x + ", ");
            query.append("y = " + y + ", ");
            query.append("z = " + z + " ");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder builder = new StringBuilder(table);

        builder.append(" int id, string player, datetime date, string event, int x, int y, int z");

        return builder.toString();
    }
}
