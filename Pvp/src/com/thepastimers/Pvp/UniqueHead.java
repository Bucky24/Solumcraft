package com.thepastimers.Pvp;

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
 * User: rwijtman
 * Date: 1/7/14
 * Time: 10:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class UniqueHead extends Table {
    public static String table = "unique_head";
    private static Map<Integer,UniqueHead> dataMap;
    
    int id;
    
    public UniqueHead() {
        id = -1;
    }
    
    String player;
    String killed;

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

    public String getKilled() {
        return killed;
    }

    public void setKilled(String killed) {
        this.killed = killed;
    }

    public static List<UniqueHead> parseResult(ResultSet result) throws SQLException {
        List<UniqueHead> ret = new ArrayList<UniqueHead>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            UniqueHead p = new UniqueHead();

            p.setId(result.getInt("id"));
            p.setPlayer(result.getString("player"));
            p.setKilled(result.getString("killed"));
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
            String columns = "(player,killed)";
            String values = "('" + d.makeSafe(player) + "','" + d.makeSafe(killed) + "')";
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

            query.append("player = '" + d.makeSafe(player) + "', ");
            query.append("killed = '" + d.makeSafe(killed) + "' ");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder ret = new StringBuilder();
        ret.append(table);
        ret.append(": int id, string player, string killed");

        return ret.toString();
    }

    public static boolean createTables(Database d, Logger l) {
        if (d == null) return false;
        StringBuilder definition = new StringBuilder("CREATE TABLE " + table + "(");
        definition.append("`id` int(11) NOT NULL AUTO_INCREMENT,");

        definition.append("`player` varchar(50) NOT NULL,");
        definition.append("`killed` varchar(50) NOT NULL,");

        definition.append("PRIMARY KEY (`id`)");
        definition.append(");");
        boolean result = d.createTableIfNotExists(table,definition.toString());

        if (!result && l != null) {
            l.warning("Unable to create table " + table);
        }

        return result;
    }

    public static void refreshCache(Database d, Logger l) {
        if (d == null) {
            return;
        }
        List<UniqueHead> UniqueHeadList = (List<UniqueHead>)d.select(UniqueHead.class,"");

        dataMap = new HashMap<Integer, UniqueHead>();

        if (UniqueHeadList == null) {
            return;
        }

        for (UniqueHead pd : UniqueHeadList) {
            dataMap.put(pd.getId(),pd);
        }

        if (l != null) {
            l.info("Cache refresh complete, have " + dataMap.keySet().size() + " entries.");
        }
    }

    public static List<UniqueHead> getUniqueHeadsForPlayer(String player) {
        List<UniqueHead> ret = new ArrayList<UniqueHead>();
        if (player == null) return ret;
        for (Integer i : dataMap.keySet()) {
            UniqueHead hc = dataMap.get(i);

            if (hc.getPlayer().equalsIgnoreCase(player)) {
                ret.add(hc);
            }
        }
        return ret;
    }
}
