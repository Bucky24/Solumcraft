package com.thepastimers.Worlds;

import com.thepastimers.Database.Database;
import com.thepastimers.Database.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 12/8/13
 * Time: 1:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class WorldCoords extends Table {
    public static String table = "world_coord";

    int id;

    public WorldCoords() {
        id = -1;
    }

    String player;
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

    public boolean delete(Database d) {
        if (id == -1) {
            return true;
        }
        if (d == null) {
            return false;
        }
        boolean result = d.query("DELETE FROM " + table + " WHERE ID = " + id);

        return result;
    }

    public static List<WorldCoords> parseResult(ResultSet result) throws SQLException {
        List<WorldCoords> ret = new ArrayList<WorldCoords>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            WorldCoords p = new WorldCoords();

            p.setId(result.getInt("id"));
            p.setPlayer(result.getString("player"));
            p.setWorld(result.getString("world"));
            p.setX(result.getDouble("x"));
            p.setY(result.getDouble("y"));
            p.setZ(result.getDouble("z"));

            ret.add(p);
        }

        return ret;
    }

    public boolean save(Database d) {
        if (d == null) {
            return false;
        }
        if (id == -1) {
            String columns = "(player,world,x,y,z)";
            String values = "('" + d.makeSafe(player) + "','" + d.makeSafe(world) + "'," + x + "," + y + "," + z + ")";
            boolean result = d.query("INSERT INTO " + table + columns + " VALUES" + values);
            return result;
        } else {
            StringBuilder query = new StringBuilder();
            query.append("UPDATE " + table + " SET ");

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

        builder.append(" int id, string player, string world, double x, double y, double z");

        return builder.toString();
    }

    public static boolean createTables(Database d, Logger l) {
        if (d == null) return false;
        StringBuilder definition = new StringBuilder("CREATE TABLE " + table + "(");
        definition.append("`id` int(11) NOT NULL AUTO_INCREMENT,");

        definition.append("`player` varchar(50) NOT NULL,");
        definition.append("`world` varchar(50) NOT NULL,");
        definition.append("`x` double NOT NULL,");
        definition.append("`y` double NOT NULL,");
        definition.append("`z` double NOT NULL,");

        definition.append("PRIMARY KEY (`id`)");
        definition.append(");");
        boolean result = d.createTableIfNotExists(table,definition.toString());

        if (!result && l != null) {
            l.warning("Unable to create table " + table);
        }

        return result;
    }
}
