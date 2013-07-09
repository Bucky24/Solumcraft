package com.thepastimers.Home;

import com.thepastimers.Database.Database;
import com.thepastimers.Database.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: derp
 * Date: 10/2/12
 * Time: 9:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class HomeData extends Table {
    public static String table = "home";
    
    int id;
    
    public HomeData() {
        id = -1;
    }
    
    String player;
    String name;
    String world;
    double x;
    double y;
    double z;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public static List<HomeData> parseResult(ResultSet result) throws SQLException {
        List<HomeData> ret = new ArrayList<HomeData>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            HomeData p = new HomeData();

            p.setId(result.getInt("id"));
            p.setName(result.getString("name"));
            p.setPlayer(result.getString("player"));
            p.setWorld(result.getString("world"));
            p.setX(result.getDouble("x"));
            p.setY(result.getDouble("y"));
            p.setZ(result.getDouble("z"));

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
            String columns = "(name,player,world,x,y,z)";
            String values = "('" + d.makeSafe(name) + "','" + d.makeSafe(player) + "','" + d.makeSafe(world)
                    + "'," + x + "," + y + "," + z + ")";
            return d.query("INSERT INTO " + table + columns + " VALUES" + values);
        } else {
            StringBuilder query = new StringBuilder();
            query.append("UPDATE " + table + " SET ");

            query.append("name = '" + d.makeSafe(name) + "'" + ", ");
            query.append("player = '" + d.makeSafe(player) + "', ");
            query.append("world = '" + d.makeSafe(world) + "', ");
            query.append("x = " + x + ", ");
            query.append("y = " + y + ", ");
            query.append("z = " + z + " ");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder builder = new StringBuilder(table);

        builder.append(" int id, string name, string player, string world, double x, double y, double z");

        return builder.toString();
    }
}
