package com.thepastimers.Spawner;

import com.thepastimers.Database.Database;
import com.thepastimers.Database.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 5/2/13
 * Time: 8:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class SpawnData extends Table {
    public static String table = "spawn_point";

    int id;

    public SpawnData() {
        id = -1;
    }

    float x;
    float y;
    float z;
    String what;
    String group;
    String world;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public String getWhat() {
        return what;
    }

    public void setWhat(String what) {
        this.what = what;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public static List<SpawnData> parseResult(ResultSet result) throws SQLException {
        List<SpawnData> ret = new ArrayList<SpawnData>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            SpawnData t = new SpawnData();
            t.setId(result.getInt("id"));
            t.setX(result.getInt("x"));
            t.setY(result.getInt("y"));
            t.setZ(result.getInt("z"));
            t.setWhat(result.getString("what"));
            t.setGroup(result.getString("group"));
            t.setWorld(result.getString("world"));

            ret.add(t);
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
            String columns = "(x,y,z,what,`group`,world)";
            String values = "(" + x + "," + y + "," + z + ",'" + d.makeSafe(what) + "','" + d.makeSafe(group) + "','" + d.makeSafe(world) + "')";
            return d.query("INSERT INTO " + table + columns + " VALUES" + values);
        } else {
            StringBuilder query = new StringBuilder();
            query.append("UPDATE " + table + " SET ");

            query.append("x = " + x + ", ");
            query.append("y = " + y + ", ");
            query.append("z = " + z + ", ");
            query.append("what = '" + d.makeSafe(what) + "', ");
            query.append("`group` = '" + d.makeSafe(group) + "', ");
            query.append("world = '" + d.makeSafe(world) + "' ");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder builder = new StringBuilder(table);
        builder.append(" int id, float x, float y, float z, string what, string group, string world");

        return builder.toString();
    }
}
