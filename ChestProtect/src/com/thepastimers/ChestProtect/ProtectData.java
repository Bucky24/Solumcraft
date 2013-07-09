package com.thepastimers.ChestProtect;

import com.thepastimers.Database.Database;
import com.thepastimers.Database.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: derp
 * Date: 10/7/12
 * Time: 4:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProtectData extends Table {
    public static String table = "protect";

    int id;

    public ProtectData() {
        id = -1;
        link = -1;
    }

    String owner;
    int x;
    int y;
    int z;
    String world;
    int link;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
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

    public int getLink() {
        return link;
    }

    public void setLink(int link) {
        this.link = link;
    }

    public static List<ProtectData> parseResult(ResultSet result) throws SQLException {
        List<ProtectData> ret = new ArrayList<ProtectData>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            ProtectData p = new ProtectData();

            p.setId(result.getInt("id"));
            p.setOwner(result.getString("owner"));
            p.setX(result.getInt("x"));
            p.setY(result.getInt("y"));
            p.setZ(result.getInt("z"));
            p.setWorld(result.getString("world"));
            p.setLink(result.getInt("link"));

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
            String columns = "(owner,x,y,z,world,link)";
            String values = "('" + d.makeSafe(owner) + "'," + x + "," + y + "," + z
                    + ",'" + d.makeSafe(world) + "'," + link + ")";
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

            query.append("owner = '" + d.makeSafe(owner) + "', ");
            query.append("x = " + x + ", ");
            query.append("y = " + y + ", ");
            query.append("z = " + z + ", ");
            query.append("world = '" + d.makeSafe(world) + "', ");
            query.append("link = " + link + " ");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder builder = new StringBuilder(table);

        builder.append(" int id, string owner, int x, int y, int z, string world, int link");

        return builder.toString();
    }
}
