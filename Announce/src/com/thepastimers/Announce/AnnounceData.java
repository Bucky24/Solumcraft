package com.thepastimers.Announce;

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
 * Date: 12/4/13
 * Time: 11:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class AnnounceData extends Table {
    public static String table = "announcements";

    private static Map<Integer,AnnounceData> dataMap;

    int id;

    public AnnounceData() {
        id = -1;
    }

    String announce;
    int frequency;
    String units;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAnnounce() {
        return announce;
    }

    public void setAnnounce(String announce) {
        this.announce = announce;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
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

    public static List<AnnounceData> parseResult(ResultSet result) throws SQLException {
        List<AnnounceData> ret = new ArrayList<AnnounceData>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            AnnounceData p = new AnnounceData();

            p.setId(result.getInt("id"));
            p.setAnnounce(result.getString("announce"));
            p.setFrequency(result.getInt("frequency"));
            p.setUnits(result.getString("units"));

            ret.add(p);
        }

        return ret;
    }

    public boolean save(Database d) {
        if (d == null) {
            return false;
        }
        if (id == -1) {
            String columns = "(announce,frequency,units)";
            String values = "('" + d.makeSafe(announce) + "'," + frequency + ",'" + d.makeSafe(units) + "')";
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

            query.append("announce = '" + d.makeSafe(announce) + "', ");
            query.append("frequency = " + frequency + ", ");
            query.append("units = '" + d.makeSafe(units) + "' ");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder builder = new StringBuilder(table);

        builder.append(" int id, string announce, int frequency, string units");

        return builder.toString();
    }

    public static void refreshCache(Database d, Logger l) {
        if (d == null) {
            return;
        }
        List<AnnounceData> announceDataList = (List<AnnounceData>)d.select(AnnounceData.class,"");

        dataMap = new HashMap<Integer, AnnounceData>();

        if (announceDataList == null) {
            return;
        }

        if (l != null) {
            l.info("Cache refresh complete, have " + dataMap.keySet().size() + " entries.");
        }
    }

    public static List<AnnounceData> getAnnouncements() {
        List<AnnounceData> data = new ArrayList<AnnounceData>();
        for (int key : dataMap.keySet()) {
            data.add(dataMap.get(key));
        }

        return data;
    }

    public static boolean createTables(Database d, Logger l) {
        if (d == null) return false;
        StringBuilder definition = new StringBuilder("CREATE TABLE " + table + "(");
        definition.append("`id` int(11) NOT NULL AUTO_INCREMENT,");

        definition.append("`announce` text NOT NULL,");
        definition.append("`frequency` int(5) NOT NULL,");
        definition.append("`units` varchar(20)");

        definition.append("PRIMARY KEY (`id`)");
        definition.append(");");
        boolean result = d.createTableIfNotExists(table,definition.toString());

        if (!result && l != null) {
            l.warning("Unable to create table " + table);
        }

        return result;
    }
}
