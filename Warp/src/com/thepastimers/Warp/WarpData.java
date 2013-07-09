package com.thepastimers.Warp;

import com.thepastimers.Database.Database;
import com.thepastimers.Database.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 7/7/13
 * Time: 8:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class WarpData extends Table {
    public static String table = "warp";

    int id;

    public WarpData() {
        id = -1;
    }

    String warp;
    int x;
    int y;
    int z;
    String world;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWarp() {
        return warp;
    }

    public void setWarp(String warp) {
        this.warp = warp;
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

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public static List<WarpData> parseResult(ResultSet result) throws SQLException {
        List<WarpData> ret = new ArrayList<WarpData>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            WarpData p = new WarpData();

            p.setId(result.getInt("id"));
            p.setWarp(result.getString("warp"));
            p.setX(result.getInt("x"));
            p.setY(result.getInt("y"));
            p.setZ(result.getInt("z"));
            p.setWorld(result.getString("world"));

            ret.add(p);
        }

        return ret;
    }

    public boolean save(Database d) {
        if (d == null) {
            return false;
        }
        if (id == -1) {
            String columns = "(warp,x,y,z,world)";
            String values = "('" + d.makeSafe(warp) + "'," + x + "," + y + "," + z + ",'" + d.makeSafe(world) + "')";
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

            query.append("warp = '" + d.makeSafe(warp) + "', ");
            query.append("x = " + z + ", ");
            query.append("y = " + y + ", ");
            query.append("z = " + z + ", ");
            query.append("world = '" + d.makeSafe(world) + "' ");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder builder = new StringBuilder(table);

        builder.append(" int id, string warp, int x, int y, int z, string world");

        return builder.toString();
    }
}
