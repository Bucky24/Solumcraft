package com.thepastimers.Permission;

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
 * User: derp
 * Date: 10/1/12
 * Time: 8:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlayerPerm extends Table {
    public static String table = "player_permissions";

    private static Map<Integer,PlayerPerm> dataMap;

    int id;

    public PlayerPerm() {
        id = -1;
    }

    String player;
    String permission;

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public static List<PlayerPerm> parseResult(ResultSet result) throws SQLException {
        List<PlayerPerm> ret = new ArrayList<PlayerPerm>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            PlayerPerm p = new PlayerPerm();

            p.setId(result.getInt("id"));
            p.setPlayer(result.getString("player"));
            p.setPermission(result.getString("permission"));

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
            String columns = "(player,permission)";
            String values = "('" + d.makeSafe(player) + "','" + d.makeSafe(permission)  + "')";
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

            query.append("player = '" + d.makeSafe(player) + "'" + ", ");
            query.append("permission = '" + d.makeSafe(permission) + "'");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static void refreshCache(Database d, Logger l) {
        if (d == null) {
            return;
        }
        List<PlayerPerm> PlayerPermList = (List<PlayerPerm>)d.select(PlayerPerm.class,"");

        dataMap = new HashMap<Integer, PlayerPerm>();

        if (PlayerPermList == null) {
            return;
        }

        for (PlayerPerm pd : PlayerPermList) {
            dataMap.put(pd.getId(),pd);
        }

        if (l != null) {
            l.info("Cache refresh complete, have " + dataMap.keySet().size() + " entries.");
        }
    }

    public static boolean hasPermission(String player, String permission) {
        if (player == null || permission == null) {
            return false;
        }

        for (Integer id : dataMap.keySet()) {
            PlayerPerm perm = dataMap.get(id);

            if (player.equals(perm.getPlayer()) && permission.equalsIgnoreCase(perm.getPermission())) {
                return true;
            }
        }

        return false;
    }

    public static boolean createTables(Database d, Logger l) {
        if (d == null) return false;
        StringBuilder definition = new StringBuilder("CREATE TABLE " + table + "(");
        definition.append("`id` int(11) NOT NULL AUTO_INCREMENT,");

        definition.append("`player` varchar(50) NOT NULL,");
        definition.append("`permission` varchar(50) NOT NULL,");

        definition.append("PRIMARY KEY (`id`)");
        definition.append(");");
        boolean result = d.createTableIfNotExists(table,definition.toString());

        if (!result && l != null) {
            l.warning("Unable to create table " + table);
        }

        return result;
    }
}
