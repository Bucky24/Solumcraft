package com.thepastimers.Plot;

import com.thepastimers.Database.Database;
import com.thepastimers.Database.Table;

import java.security.acl.Owner;
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
 * Date: 10/4/12
 * Time: 7:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlotPerms extends Table {
    public static String table = "plot_perms";

    private static Map<Integer, PlotPerms> dataMap;
    
    int id;
    
    public PlotPerms() {
        id = -1;
    }
    
    int plot;
    String player;
    int perm;

    public static int OWNER = 30;
    public static int COOWNER = 25;
    public static int RESIDENT = 20;
    public static int WORKER = 10;
    public static int NONE = 0;

    public int getPlot() {
        return plot;
    }

    public void setPlot(int plot) {
        this.plot = plot;
    }

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

    public int getPerm() {
        return perm;
    }

    public void setPerm(int perm) {
        this.perm = perm;
    }

    public static List<PlotPerms> parseResult(ResultSet result) throws SQLException {
        List<PlotPerms> ret = new ArrayList<PlotPerms>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            PlotPerms p = new PlotPerms();

            p.setId(result.getInt("id"));
            p.setPlot(result.getInt("plot"));
            p.setPlayer(result.getString("player"));
            p.setPerm(result.getInt("perm"));
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
            String columns = "(plot,player,perm)";
            String values = "(" + plot + ",'" + d.makeSafe(player) + "'," + perm + ")";
            boolean result =  d.query("INSERT INTO " + table + columns + " VALUES" + values);
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

            query.append("plot = " + plot + ", ");
            query.append("player = '" + d.makeSafe(player) + "', ");
            query.append("perm = " + perm + " ");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder builder = new StringBuilder(table);

        builder.append(" int id, int plot, string player, int perm");

        return builder.toString();
    }

    public static void refreshCache(Database d, Logger l) {
        if (d == null) {
            return;
        }
        List<PlotPerms> plotPermsList = (List<PlotPerms>)d.select(PlotPerms.class,"");

        dataMap = new HashMap<Integer, PlotPerms>();

        if (plotPermsList == null) {
            return;
        }

        for (PlotPerms pd : plotPermsList) {
            dataMap.put(pd.getId(),pd);
        }

        if (l != null) {
            l.info("Cache refresh complete, have " + dataMap.keySet().size() + " entries.");
        }
    }

    public static PlotPerms getPermsForPlotAndPlayer(int plot, String player) {
        PlotPerms pp = null;

        if (player == null) {
            return pp;
        }

        for (Integer i : dataMap.keySet()) {
            PlotPerms data = dataMap.get(i);

            if (player.equalsIgnoreCase(data.getPlayer()) && data.getPlot() == plot) {
                pp = data;
                break;
            }
        }

        return pp;
    }
}
