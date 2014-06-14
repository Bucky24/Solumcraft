package com.thepastimers.Worlds;

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
 * Date: 6/3/14
 * Time: 10:25 AM
 * To change this template use File | Settings | File Templates.
 */

public class WorldTypes extends Table {
    public static String table = "world_types";
    private static Map<Integer,WorldTypes> dataMap;
    private static Map<String,WorldTypes> nameDataMap;
    
    int id;
    
    public WorldTypes() {
        id = -1;
    }
    
    String worldName;
    int worldType;

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

    public int getWorldType() {
        return worldType;
    }

    public void setWorldType(int worldType) {
        this.worldType = worldType;
    }

    public static List<WorldTypes> parseResult(ResultSet result) throws SQLException {
        List<WorldTypes> ret = new ArrayList<WorldTypes>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            WorldTypes p = new WorldTypes();

            p.setId(result.getInt("id"));
            p.setWorldName(result.getString("world_name"));
            p.setWorldType(result.getInt("world_type"));

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
            nameDataMap.remove(worldName);
        }

        return result;
    }

    public boolean save(Database d) {
        if (d == null) {
            return false;
        }
        if (id == -1) {
            String columns = "(world_name,world_type)";
            String values = "('" + d.makeSafe(worldName) + "'," + worldType + ")";
            boolean result = d.query("INSERT INTO " + table + columns + " VALUES" + values);

            ResultSet keys = d.getGeneratedKeys();

            if (keys != null && result) {
                try {
                    if (keys.next()) {
                        setId(keys.getInt(1));
                        dataMap.put(getId(),this);
                        nameDataMap.put(worldName,this);
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

            query.append("world_name = '" + d.makeSafe(worldName) + "', ");
            query.append("world_type = " + worldType + " ");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder builder = new StringBuilder(table);

        builder.append(" int id, string world_name, int world_type");

        return builder.toString();
    }

    public static void refreshCache(Database d, Logger l) {
        if (d == null) {
            return;
        }
        List<WorldTypes> WorldTypesList = (List<WorldTypes>)d.select(WorldTypes.class,"");

        dataMap = new HashMap<Integer, WorldTypes>();
        nameDataMap = new HashMap<String, WorldTypes>();

        if (WorldTypesList == null) {
            return;
        }

        for (WorldTypes pd : WorldTypesList) {
            dataMap.put(pd.getId(),pd);
            nameDataMap.put(pd.getWorldName(),pd);
        }

        if (l != null) {
            l.info("Cache refresh complete, have " + dataMap.keySet().size() + " entries.");
        }
    }

    // table specific get instructions

    public static WorldTypes getData(int id) {
        return dataMap.get(id);
    }

    public static WorldTypes getData(String name) {
        return nameDataMap.get(name);
    }

    public static boolean createTables(Database d, Logger l) {
        if (d == null) return false;
        String definition = "CREATE TABLE " + table + " (" +
                "`id` int(11) NOT NULL AUTO_INCREMENT,`world_name` varchar(50) NOT NULL,  " +
                "`world_type` int(2) NOT NULL, PRIMARY KEY (`id`)) " +
                "ENGINE=InnoDB AUTO_INCREMENT=887 DEFAULT CHARSET=latin1";
        boolean result = d.createTableIfNotExists(table,definition);
        if (!result && l != null) {
            l.warning("Unable to create table " + table);
        }

        return result;
    }
}
