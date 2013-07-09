package com.thepastimers.Kit;

import com.thepastimers.Database.Database;
import com.thepastimers.Database.Table;
import org.json.simple.JSONArray;
import sun.org.mozilla.javascript.internal.json.JsonParser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 5/30/13
 * Time: 7:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class KitData extends Table {
    public static String table = "kit";

    int id;

    public KitData() {
        id = -1;
    }

    String name;
    String items;
    long timeout;

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

    public JSONArray getItems() {
        JSONArray ret = new JSONArray();
        String[] arr = items.split(",");
        for (int i=0;i<arr.length;i++) {
            if (!"".equalsIgnoreCase(arr[i])) {
                ret.add(arr[i]);
            }
        }

        return ret;
    }

    public void setItems(JSONArray items) {
        StringBuilder itemBuilder = new StringBuilder();
        for (int i=0;i<items.size();i++)  {
            Object o = items.get(i);
            itemBuilder.append(o).append(",");
        }
        this.items = itemBuilder.toString();
    }

    public void setItems(String items) {
        this.items = items;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public static List<KitData> parseResult(ResultSet result) throws SQLException {
        List<KitData> ret = new ArrayList<KitData>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            KitData p = new KitData();

            p.setId(result.getInt("id"));
            p.setName(result.getString("name"));
            p.setItems(result.getString("items"));
            p.setTimeout(result.getLong("timeout"));

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
        return d.query("DELETE FROM " + table + " WHERE ID = " + id);
    }

    public boolean save(Database d) {
        if (d == null) {
            return false;
        }
        if (id == -1) {
            String columns = "(name,items,timeout)";
            String values = "('" + d.makeSafe(name) + "','" + d.makeSafe(items) + "'," + timeout + ")";
            boolean result = d.query("INSERT INTO " + table + columns + " VALUES" + values);

            ResultSet keys = d.getGeneratedKeys();

            if (keys != null && result) {
                try {
                    if (keys.next()) {
                        setId(keys.getInt(1));
                    }
                } catch (SQLException e) {
                    // fallback method
                }
            }

            return result;
        } else {
            StringBuilder query = new StringBuilder();
            query.append("UPDATE " + table + " SET ");

            query.append("name = '" + d.makeSafe(name) + "', ");
            query.append("items = '" + d.makeSafe(items) + "', ");
            query.append("timeout = " + timeout + " ");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder builder = new StringBuilder(table);

        builder.append(" int id, string name, string items, long timeout");

        return builder.toString();
    }
}
