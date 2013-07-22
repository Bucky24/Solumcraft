package com.thepastimers.Plot;

import com.google.common.io.LittleEndianDataInputStream;
import com.thepastimers.Database.Database;
import com.thepastimers.Database.Table;
import org.bukkit.Location;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedTransferQueue;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: derp
 * Date: 10/4/12
 * Time: 7:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlotData extends Table {
    public static String table = "plot";

    private static Map<Integer,PlotData> dataMap;
    
    int id;
    
    public PlotData() {
        id = -1;
        pvp = false;
        pve = false;
        creative = false;
    }
    
    String name;
    String owner;
    int x1;
    int z1;
    int x2;
    int z2;
    String world;
    boolean subPlot;
    int parent;
    
    boolean pvp;
    boolean pve;
    boolean creative;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getX1() {
        return x1;
    }

    public void setX1(int x1) {
        this.x1 = x1;
    }

    public int getZ1() {
        return z1;
    }

    public void setZ1(int z1) {
        this.z1 = z1;
    }

    public int getX2() {
        return x2;
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public int getZ2() {
        return z2;
    }

    public void setZ2(int z2) {
        this.z2 = z2;
    }

    public boolean isSubPlot() {
        return subPlot;
    }

    public void setSubPlot(boolean subPlot) {
        this.subPlot = subPlot;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public boolean isPvp() {
        return pvp;
    }

    public void setPvp(boolean pvp) {
        this.pvp = pvp;
    }

    public boolean isPve() {
        return pve;
    }

    public void setPve(boolean pve) {
        this.pve = pve;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public boolean isCreative() {
        return creative;
    }

    public void setCreative(boolean creative) {
        this.creative = creative;
    }

    public static List<PlotData> parseResult(ResultSet result) throws SQLException {
        List<PlotData> ret = new ArrayList<PlotData>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            PlotData p = new PlotData();

            p.setId(result.getInt("id"));
            p.setName(result.getString("name"));
            p.setOwner(result.getString("owner"));
            p.setX1(result.getInt("x1"));
            p.setZ1(result.getInt("z1"));
            p.setX2(result.getInt("x2"));
            p.setZ2(result.getInt("z2"));
            p.setSubPlot(result.getBoolean("subPlot"));
            p.setParent(result.getInt("parent"));
            p.setPvp(result.getBoolean("pvp"));
            p.setPve(result.getBoolean("pve"));
            p.setWorld(result.getString("world"));
            p.setCreative(result.getBoolean("creative"));

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
            String columns = "(name,owner,x1,z1,x2,z2,subPlot,parent,pvp,pve,world,creative)";
            String values = "('" + d.makeSafe(name) + "','" + d.makeSafe(owner) + "'," + x1 + "," + z1
                    +  "," + x2 + "," + z2 + "," + subPlot + "," + parent + "," + pvp + "," + pve + ",'"
                    + d.makeSafe(world) + "'," + creative + ")";
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

            query.append("name = '" + d.makeSafe(name) + "'" + ", ");
            query.append("owner = '" + d.makeSafe(owner) + "', ");
            query.append("x1 = " + x1 + ", ");
            query.append("z1 = " + z1 + ", ");
            query.append("x2 = " + x2 + ", ");
            query.append("z2 = " + z2 + ", ");
            query.append("subPlot = " + subPlot + ", ");
            query.append("parent = " + parent + ", ");
            query.append("pvp = " + pvp + ", ");
            query.append("pve = " + pve + ", ");
            query.append("world = '" + d.makeSafe(world) + "', ");
            query.append("creative = " + creative + " ");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder builder = new StringBuilder(table);

        builder.append(" int id, string name, string owner, int x1, int z1, int x2, int z2, string world, bool subPlot");
        builder.append(", int parent, bool pvp, bool pve, bool creative");

        return builder.toString();
    }

    public static void refreshCache(Database d, Logger l) {
        if (d == null) {
            return;
        }
        List<PlotData> plotDataList = (List<PlotData>)d.select(PlotData.class,"");

        dataMap = new HashMap<Integer, PlotData>();

        if (plotDataList == null) {
            return;
        }

        for (PlotData pd : plotDataList) {
            dataMap.put(pd.getId(),pd);
        }

        if (l != null) {
            l.info("Cache refresh complete, have " + dataMap.keySet().size() + " entries.");
        }
    }

    // table specific get instructions

    public static PlotData getPlotAtLocation(int x, int z, String world, boolean subPlot) {
        PlotData pd = null;

        if (world == null) {
            return null;
        }

        for (Integer i : dataMap.keySet()) {
            PlotData data = dataMap.get(i);

            if (data.getX1() <= x && data.getX2() >= x
                    && data.getZ1() <= z && data.getZ2() >= z
                    && world.equalsIgnoreCase(data.getWorld()) && data.isSubPlot() == subPlot) {
                pd = data;
                break;
            }
        }

        return pd;
    }

    public static List<PlotData> getAllPlotsInWorld(String world, boolean subPlot) {
        List<PlotData> ret = new ArrayList<PlotData>();

        if (world == null) {
            return ret;
        }

        for (Integer i : dataMap.keySet()) {
            PlotData data = dataMap.get(i);

            if (world.equalsIgnoreCase(data.getWorld()) && data.isSubPlot() == subPlot) {
                ret.add(data);
            }
        }

        return ret;
    }
}
