package com.thepastimers.CastleWars;

import com.thepastimers.Database.Database;
import com.thepastimers.Database.Table;
import com.thepastimers.Plot.PlotData;
import sun.java2d.pipe.SpanShapeRenderer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
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
    int y;
    int defenseLevel;
    String upgradeTime;

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

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getDefenseLevel() {
        return defenseLevel;
    }

    public void setDefenseLevel(int defenseLevel) {
        this.defenseLevel = defenseLevel;
    }

    public String getUpgradeTime() {
        return upgradeTime;
    }

    public Timestamp getUpgradeTimestamp() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date d = format.parse(upgradeTime);
            return new Timestamp(d.getTime());
        } catch (ParseException e) {
            return new Timestamp(0);
        }
    }

    public void setUpgradeTime(String upgradeTime) {
        this.upgradeTime = upgradeTime;
    }

    public void setUpgradeTime(Timestamp t) {
        Date d = new Date(t.getTime());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        upgradeTime = format.format(d);
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
            plotDataMap.remove(plot);
        }

        return result;
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
            p.setY(result.getInt("y"));
            p.setDefenseLevel(result.getInt("defenseLevel"));
            p.setUpgradeTime(result.getString("upgradeTime"));

            ret.add(p);
        }

        return ret;
    }

    public boolean save(Database d) {
        if (d == null) {
            return false;
        }
        if (id == -1) {
            String columns = "(plot,level,owner,y,defenseLevel,upgradeTime)";
            String values = "(" + plot + "," + level + ",'" + d.makeSafe(owner) + "'," + y + "," + defenseLevel
                    + ",'" + upgradeTime + "')";
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
            query.append("owner = '" + owner + "', ");
            query.append("y = " + y + ", ");
            query.append("defenseLevel = " + defenseLevel + ", ");
            query.append("upgradeTime = '" + upgradeTime + "' ");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder builder = new StringBuilder(table);

        builder.append(" int id, int plot, int level, string owner, int y, int defenseLevel, string upgradeTime");

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
