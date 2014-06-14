package com.thepastimers.Money;

import com.thepastimers.Database.Database;
import com.thepastimers.Database.Table;
import org.bukkit.Location;

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
 * Time: 10:52 AM
 * To change this template use File | Settings | File Templates.
 */
public class ServerStock extends Table {
    public static String table = "server_stock";

    private static Map<Integer,ServerStock> dataMap;
    private static Map<String,ServerStock> itemDataMap;
    
    int id;
    
    public ServerStock() {
        id = -1;
    }
    
    String item;
    int quantity;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public static List<ServerStock> parseResult(ResultSet result) throws SQLException {
        List<ServerStock> ret = new ArrayList<ServerStock>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            ServerStock p = new ServerStock();

            p.setId(result.getInt("id"));
            p.setItem(result.getString("item"));
            p.setQuantity(result.getInt("quantity"));

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
            itemDataMap.remove(item);
        }

        return result;
    }

    public boolean save(Database d) {
        if (d == null) {
            return false;
        }
        if (id == -1) {
            String columns = "(item,quantity)";
            String values = "('" + d.makeSafe(item) + "'," + quantity + ")";
            boolean result = d.query("INSERT INTO " + table + columns + " VALUES" + values);

            ResultSet keys = d.getGeneratedKeys();

            if (keys != null && result) {
                try {
                    if (keys.next()) {
                        setId(keys.getInt(1));
                        dataMap.put(getId(),this);
                        itemDataMap.put(item,this);
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

            query.append("item = '" + d.makeSafe(item) + "', ");
            query.append("quantity = " + quantity + " ");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder builder = new StringBuilder(table);

        builder.append(" int id, string item, int quantity");

        return builder.toString();
    }

    public static void refreshCache(Database d, Logger l) {
        if (d == null) {
            return;
        }
        List<ServerStock> ServerStockList = (List<ServerStock>)d.select(ServerStock.class,"");

        dataMap = new HashMap<Integer, ServerStock>();
        itemDataMap = new HashMap<String, ServerStock>();

        if (ServerStockList == null) {
            return;
        }

        for (ServerStock pd : ServerStockList) {
            dataMap.put(pd.getId(),pd);
            itemDataMap.put(pd.getItem(),pd);
        }

        if (l != null) {
            l.info("Cache refresh complete, have " + dataMap.keySet().size() + " entries.");
        }
    }

    public static boolean createTables(Database d, Logger l) {
        if (d == null) return false;
        StringBuilder definition = new StringBuilder("CREATE TABLE " + table + "(");
        definition.append("`id` int(11) NOT NULL AUTO_INCREMENT,");

        definition.append("`item` varchar(50) NOT NULL,");
        definition.append("`quantity` int(11) NOT NULL,");

        definition.append("PRIMARY KEY (`id`)");
        definition.append(");");
        boolean result = d.createTableIfNotExists(table,definition.toString());

        if (!result && l != null) {
            l.warning("Unable to create table " + table);
        }

        return result;
    }

    // table specific get instructions

    public static ServerStock getStockForItem(String item) {
        return itemDataMap.get(item);
    }
}
