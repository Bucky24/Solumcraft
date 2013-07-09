package com.thepastimers.Director;

import com.thepastimers.Database.Database;
import com.thepastimers.Database.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 5/4/13
 * Time: 12:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class Arena extends Table {
    public static String table = "arena";

    int id;

    public Arena() {
        id = -1;
    }

    int x1;
    int y1;
    int z1;
    int x2;
    int y2;
    int z2;
    String world;
    Integer startx;
    Integer starty;
    Integer startz;
    boolean active;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getX1() {
        return x1;
    }

    public void setX1(int x1) {
        this.x1 = x1;
    }

    public int getY1() {
        return y1;
    }

    public void setY1(int y1) {
        this.y1 = y1;
    }

    public int getZ1() {
        return z1;
    }

    public void setZ1(int z1) {
        this.z1 = z1;
    }

    public int getX2() {
        return x2;
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public int getY2() {
        return y2;
    }

    public void setY2(int y2) {
        this.y2 = y2;
    }

    public int getZ2() {
        return z2;
    }

    public void setZ2(int z2) {
        this.z2 = z2;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public Integer getStartx() {
        return startx;
    }

    public void setStartx(Integer startx) {
        this.startx = startx;
    }

    public Integer getStarty() {
        return starty;
    }

    public void setStarty(Integer starty) {
        this.starty = starty;
    }

    public Integer getStartz() {
        return startz;
    }

    public void setStartz(Integer startz) {
        this.startz = startz;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public static List<Arena> parseResult(ResultSet result) throws SQLException {
        List<Arena> ret = new ArrayList<Arena>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            Arena p = new Arena();

            p.setId(result.getInt("id"));
            p.setX1(result.getInt("x1"));
            p.setY1(result.getInt("y1"));
            p.setZ1(result.getInt("z1"));
            p.setX2(result.getInt("x2"));
            p.setY2(result.getInt("y2"));
            p.setZ2(result.getInt("z2"));
            p.setWorld(result.getString("world"));
            p.setStartx(result.getInt("startx"));
            p.setStarty(result.getInt("starty"));
            p.setStartz(result.getInt("startz"));
            p.setActive(result.getBoolean("active"));

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
            String columns = "(x1,y1,z1,x2,y2,z2,world,startx,starty,startz,active)";
            String values = "(" + x1 + "," + y1 + "," + z1 + "," + x2 + "," + y2 + "," + z2 + ",'" + d.makeSafe(world) + "'," +
                    startx + "," + starty + "," + startz + "," + active + ")";
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

            query.append("x1 = " + x1 + ", ");
            query.append("y1 = " + y1 + ", ");
            query.append("z1 = " + z1 + ", ");
            query.append("x2 = " + x2 + ", ");
            query.append("y2 = " + y2 + ", ");
            query.append("z2 = " + z2 + ", ");
            query.append("world = '" + d.makeSafe(world) + "', ");
            query.append("startx = " + startx + ", ");
            query.append("starty = " + starty + ", ");
            query.append("startz = " + startz + ", ");
            query.append("active = " + active + " ");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder builder = new StringBuilder(table);

        builder.append(" int id, int x1, int y1, int z1, int x2, int y2, int z2, string world, int startx, int startz,");
        builder.append(" int startz, bool active");

        return builder.toString();
    }
}
