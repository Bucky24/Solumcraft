package com.thepastimers.Director;

import com.thepastimers.Database.Database;
import com.thepastimers.Database.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 5/4/13
 * Time: 12:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class Arena extends Table {
    public static String table = "arena";

    private static Map<Integer,Arena> dataMap;

    int id;

    public Arena() {
        id = -1;
    }

    String name;
    int x1;
    int y1;
    int z1;
    int x2;
    int y2;
    int z2;
    String world;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public static List<Arena> parseResult(ResultSet result) throws SQLException {
        List<Arena> ret = new ArrayList<Arena>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            Arena p = new Arena();

            p.setId(result.getInt("id"));
            p.setName(result.getString("name"));
            p.setX1(result.getInt("x1"));
            p.setY1(result.getInt("y1"));
            p.setZ1(result.getInt("z1"));
            p.setX2(result.getInt("x2"));
            p.setY2(result.getInt("y2"));
            p.setZ2(result.getInt("z2"));
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

        boolean result = d.query("DELETE FROM " + table + " WHERE ID = " + id);

        if (result) {
            dataMap.remove(id);
        }
        return result;
    }

    public boolean save(Database d) {
        if (d == null) {
            return false;
        }
        if (id == -1) {
            String columns = "(name,x1,y1,z1,x2,y2,z2,world)";
            String values = "('" + d.makeSafe(name) + "'," + x1 + "," + y1 + "," + z1 + "," + x2 + "," + y2 + "," + z2 + ",'" + d.makeSafe(world) + "')";
            boolean result = d.query("INSERT INTO " + table + columns + " VALUES" + values);

            ResultSet keys = d.getGeneratedKeys();


            if (keys != null && result) {
                try {
                    if (keys.next()) {
                        setId(keys.getInt(1));
                        dataMap.put(getId(),this);
                    }
                } catch (SQLException e) {
                    // fallback method
                    refreshCache(d,null);
                }
            }
            return result;
        } else {
            StringBuilder query = new StringBuilder();
            query.append("UPDATE " + table + " SET ");

            query.append("name = '" + d.makeSafe(name) + "', ");
            query.append("x1 = " + x1 + ", ");
            query.append("y1 = " + y1 + ", ");
            query.append("z1 = " + z1 + ", ");
            query.append("x2 = " + x2 + ", ");
            query.append("y2 = " + y2 + ", ");
            query.append("z2 = " + z2 + ", ");
            query.append("world = '" + d.makeSafe(world) + "' ");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder builder = new StringBuilder(table);

        builder.append(" int id, string name int x1, int y1, int z1, int x2, int y2, int z2, string world");

        return builder.toString();
    }

    public static void refreshCache(Database d, Logger l) {
        if (d == null) {
            return;
        }
        List<Arena> ArenaList = (List<Arena>)d.select(Arena.class,"");

        dataMap = new HashMap<Integer, Arena>();

        if (ArenaList == null) {
            return;
        }

        for (Arena pd : ArenaList) {
            dataMap.put(pd.getId(),pd);
        }

        if (l != null) {
            l.info("Cache refresh complete, have " + dataMap.keySet().size() + " entries.");
        }
    }

    public static Arena getArena(int x, int y, int z, String world) {
        if (world == null) return null;
        for (Integer key : dataMap.keySet()) {
            Arena a = dataMap.get(key);
            if (a.getX1() <= x && a.getX2() >= x || a.getY1() <= y && a.getY2() >= y || a.getZ1() <= z && a.getZ2() >= z && world.equalsIgnoreCase(a.getWorld())) {
                return a;
            }
        }
        return null;
    }

    public static Arena getArenaById(int id) {
        return dataMap.get(id);
    }
}
