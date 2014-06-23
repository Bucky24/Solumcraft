package com.thepastimers.Worlds;

import com.thepastimers.Database.Database;
import com.thepastimers.Database.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 6/22/14
 * Time: 8:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlayerWorld extends Table {
    public static String table = "player_world";

    int id;

    public PlayerWorld() {
        id = -1;
    }

    String worldName;
    String playerId;
    int mode;
    boolean flying;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public boolean isFlying() {
        return flying;
    }

    public void setFlying(boolean flying) {
        this.flying = flying;
    }

    public static List<PlayerWorld> parseResult(ResultSet result) throws SQLException {
        List<PlayerWorld> ret = new ArrayList<PlayerWorld>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            PlayerWorld p = new PlayerWorld();

            p.setId(result.getInt("id"));
            p.setWorldName(result.getString("world_name"));
            p.setPlayerId(result.getString("player_id"));
            p.setMode(result.getInt("mode"));
            p.setFlying(result.getBoolean("flying"));

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
        return result;
    }

    public boolean save(Database d) {
        if (d == null) {
            return false;
        }
        if (id == -1) {
            String columns = "(world_name,player_id,mode,flying)";
            String values = "('" + d.makeSafe(worldName) + "','" + d.makeSafe(playerId) + "'," + mode + "," + flying + ")";
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

            query.append("world_name = '" + d.makeSafe(worldName) + "', ");
            query.append("player_id = '" + d.makeSafe(playerId) + "', ");
            query.append("mode = " + mode + ", ");
            query.append("flying = " + flying + " ");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder builder = new StringBuilder(table);

        builder.append(" int id, string world_name, string player_id, int mode, bool flying");

        return builder.toString();
    }

    public static boolean createTables(Database d, Logger l) {
        if (d == null) return false;
        String definition = "CREATE TABLE " + table + " (" +
                "`id` int(11) NOT NULL AUTO_INCREMENT," +
                "`world_name` varchar(50) NOT NULL,  " +
                "`player_id` varchar(255) NOT NULL, " +
                "`mode` int(2) NOT NULL, " +
                "`flying` int(1) NOT NULL, " +
                " PRIMARY KEY (`id`)) " +
                "ENGINE=InnoDB AUTO_INCREMENT=887 DEFAULT CHARSET=latin1";
        boolean result = d.createTableIfNotExists(table,definition);
        if (!result && l != null) {
            l.warning("Unable to create table " + table);
        }

        return result;
    }
}
