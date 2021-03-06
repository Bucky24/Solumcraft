package com.thepastimers.TradeSign;

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
 * Date: 3/4/13
 * Time: 6:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class SignData extends Table {
    public static String table = "sign_data";
    
    int id;

    private static Map<Integer,SignData> dataMap;
    
    public SignData() {
        id = -1;
        serverSign = false;
    }
    
    String player;
    int x;
    int y;
    int z;
    String contains;
    int amount;
    int cost;
    int dispense;
    String world;
    String name;
    String metadata;
    boolean serverSign;

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

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public String getContains() {
        return contains;
    }

    public void setContains(String contains) {
        this.contains = contains;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getCostAsInt() {
        return cost;
    }

    public double getCost() {
        return ((double)cost)/100;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setCost(double cost) {
        this.cost = (int)(cost*100);
    }

    public int getDispense() {
        return dispense;
    }

    public void setDispense(int dispense) {
        this.dispense = dispense;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public boolean isServerSign() {
        return serverSign;
    }

    public void setServerSign(boolean serverSign) {
        this.serverSign = serverSign;
    }

    public static List<SignData> parseResult(ResultSet result) throws SQLException {
        List<SignData> ret = new ArrayList<SignData>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            SignData p = new SignData();

            p.setId(result.getInt("id"));
            p.setPlayer(result.getString("player"));
            p.setX(result.getInt("x"));
            p.setY(result.getInt("y"));
            p.setZ(result.getInt("z"));
            p.setContains(result.getString("contains"));
            p.setAmount(result.getInt("amount"));
            p.setCost(result.getInt("cost"));
            p.setDispense(result.getInt("dispense"));
            p.setWorld(result.getString("world"));
            p.setMetadata(result.getString("metadata"));
            p.setServerSign(result.getBoolean("server"));

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
            String columns = "(player,x,y,z,contains,amount,cost,dispense,world,metadata,server)";
            String values = "('" + d.makeSafe(player) + "'," + x + "," + y + "," + z
                    +  ",'" + d.makeSafe(contains) + "'," + amount + "," + cost + "," + dispense
                    + ",'" + d.makeSafe(world) + "','" + d.makeSafe(metadata) + "'," + serverSign + ")";
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
            query.append("x = " + x + ", ");
            query.append("y = " + y + ", ");
            query.append("z = " + z + ", ");
            query.append("contains = '" + d.makeSafe(contains) + "', ");
            query.append("amount = " + amount + ", ");
            query.append("cost = " + cost + ", ");
            query.append("dispense = " + dispense + ", ");
            query.append("world = '" + d.makeSafe(world) + "', ");
            query.append("metadata = '" + d.makeSafe(metadata) + "', ");
            query.append("server = " + serverSign + " ");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder builder = new StringBuilder(table);

        builder.append(" int id, string player, int x, int y, int z, string contains, int amount, int cost, int dispense, string world, string metadata, boolean server");

        return builder.toString();
    }

    public static void refreshCache(Database d, Logger l) {
        if (d == null) {
            return;
        }
        List<SignData> SignDataList = (List<SignData>)d.select(SignData.class,"");

        dataMap = new HashMap<Integer, SignData>();

        if (SignDataList == null) {
            return;
        }

        for (SignData pd : SignDataList) {
            dataMap.put(pd.getId(),pd);
        }

        if (l != null) {
            l.info("Cache refresh complete, have " + dataMap.keySet().size() + " entries.");
        }
    }

    public static boolean createTables(Database d, Logger l) {
        if (d == null) return false;
        String definition = "CREATE TABLE `sign_data` (" +
                "`id` int(11) NOT NULL AUTO_INCREMENT,`player` varchar(50) NOT NULL,  " +
                "`x` int(11) NOT NULL,  `y` int(11) NOT NULL,  `z` int(11) NOT NULL,  " +
                "`contains` varchar(50) NOT NULL,  `amount` int(11) NOT NULL,  `cost` int(11) NOT NULL,  " +
                "`dispense` int(11) NOT NULL,  `world` varchar(100) NOT NULL,  `metadata` varchar(100) NOT NULL,  " +
                "`server` int(1) NOT NULL, PRIMARY KEY (`id`)) ENGINE=InnoDB AUTO_INCREMENT=887 DEFAULT CHARSET=latin1";
        boolean result = d.createTableIfNotExists(table,definition);
        if (!result && l != null) {
            l.warning("Unable to create table " + table);
        }

        return result;
    }

    // table specific get instructions

    public static SignData getData(int id) {
        for (Integer i : dataMap.keySet()) {
            if (i == id) {
                return dataMap.get(i);
            }
        }

        return null;
    }

    public static SignData getSignAt(int x, int y, int z) {
        for (Integer i : dataMap.keySet()) {
            SignData sd = dataMap.get(i);

            if (sd.getX() == x && sd.getY() == y && sd.getZ() == z) {
                return sd;
            }
        }

        return null;
    }

    public static List<SignData> getPlayerSigns(String player) {
        List<SignData> ret = new ArrayList<SignData>();
        if (player == null) return ret;
        for (Integer i : dataMap.keySet()) {
            SignData sd = dataMap.get(i);
            if (player.equalsIgnoreCase(sd.getPlayer())) {
                ret.add(sd);
            }
        }
        return ret;
    }
}
