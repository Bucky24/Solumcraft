package com.thepastimers.CastleWars;

import com.thepastimers.Database.Database;
import com.thepastimers.Database.Table;
import com.thepastimers.Plot.PlotData;

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
 * Date: 8/17/13
 * Time: 12:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class CastleData extends Table {
    public static String table = "castle_data";

    private static Map<Integer,CastleData> dataMap;
    private static Map<Integer,CastleData> plotDataMap;
    
    int plot;
    int level;
    String owner;

    public int getPlot() {
        return plot;
    }

    public void setPlot(int plot) {
        this.plot = plot;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public static List<CastleData> parseResult(ResultSet result) throws SQLException {
        List<CastleData> ret = new ArrayList<CastleData>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            CastleData p = new CastleData();

            p.setId(result.getInt("id"));
            p.setPlot(result.getInt("plot"));
            p.setLevel(result.getInt("level"));
            p.setOwner(result.getString("owner"));

            ret.add(p);
        }

        return ret;
    }

    public boolean save(Database d) {
        if (d == null) {
            return false;
        }
        if (id == -1) {
            String columns = "(plot,level,owner)";
            String values = "(" + plot + "," + level + ",'" + d.makeSafe(owner) + "')";
            boolean result = d.query("INSERT INTO " + table + columns + " VALUES" + values);

            ResultSet keys = d.getGeneratedKeys();

            if (keys != null && result) {
                try {
                    if (keys.next()) {
                        setId(keys.getInt(1));
                        dataMap.put(getId(),this);
                        plotDataMap.put(plot,this);
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

            query.append("plot = " + plot + ", ");
            query.append("level = " + level + ", ");
            query.append("owner = '" + owner + "' ");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder builder = new StringBuilder(table);

        builder.append(" int id, int plot, int level, string owner");

        return builder.toString();
    }

    public static void refreshCache(Database d, Logger l) {
        if (d == null) {
            return;
        }
        List<CastleData> CastleDataList = (List<CastleData>)d.select(CastleData.class,"");

        dataMap = new HashMap<Integer, CastleData>();
        plotDataMap = new HashMap<Integer, CastleData>();

        if (CastleDataList == null) {
            return;
        }

        for (CastleData pd : CastleDataList) {
            dataMap.put(pd.getId(),pd);
            plotDataMap.put(pd.getPlot(),pd);
        }

        if (l != null) {
            l.info("Cache refresh complete, have " + dataMap.keySet().size() + " entries.");
        }
    }

    public static CastleData getCastleForPlot(PlotData pd) {
        if (plotDataMap.containsKey(pd.getId())) {
            return plotDataMap.get(pd.getId());
        } else {
            return null;
        }
    }
}
