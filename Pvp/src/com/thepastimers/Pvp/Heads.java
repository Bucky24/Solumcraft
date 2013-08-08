package com.thepastimers.Pvp;

import com.thepastimers.Database.Database;
import com.thepastimers.Database.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: rwijtman
 * Date: 8/8/13
 * Time: 11:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class Heads extends Table {
    public static String table = "heads";

    int id;

    public Heads() {
        id = -1;
    }

    String player;
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

    public static List<Heads> parseResult(ResultSet result) throws SQLException {
        List<Heads> ret = new ArrayList<Heads>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            Heads p = new Heads();

            p.setId(result.getInt("id"));
            p.setPlayer(result.getString("player"));
            p.setX(result.getInt("x"));
            p.setY(result.getInt("y"));
            p.setZ(result.getInt("z"));

            ret.add(p);
        }

        return ret;
    }

    public boolean save(Database d) {
        if (d == null) {
            return false;
        }
        if (id == -1) {
            String columns = "(player,x,y,z)";
            String values = "('" + d.makeSafe(player) + "'," + x + "," + y + "," + z + ")";
            return d.query("INSERT INTO " + table + columns + " VALUES" + values);
        } else {
            StringBuilder query = new StringBuilder();
            query.append("UPDATE " + table + " SET ");

            query.append("player = '" + d.makeSafe(player) + "'" + ", ");
            query.append("x = " + x + ", ");
            query.append("y = " + y + ", ");
            query.append("z = " + z + " ");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder ret = new StringBuilder();
        ret.append(table);
        ret.append(": int id, string player, int x, int y, int z");

        return ret.toString();
    }
}
