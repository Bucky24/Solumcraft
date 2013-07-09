package com.thepastimers.Money;

import com.thepastimers.Database.Database;
import com.thepastimers.Database.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 4/24/13
 * Time: 6:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class Price extends Table {
    public static String table = "prices";

    int id;

    public Price() {
        id = -1;
    }

    String name;
    int price;

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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public static List<Price> parseResult(ResultSet result) throws SQLException {
        List<Price> ret = new ArrayList<Price>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            Price p = new Price();

            p.setId(result.getInt("id"));
            p.setName(result.getString("name"));
            p.setPrice(result.getInt("price"));

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
            String columns = "(name,price)";
            String values = "('" + d.makeSafe(name) + "'," + price + ")";
            return d.query("INSERT INTO " + table + columns + " VALUES" + values);
        } else {
            StringBuilder query = new StringBuilder();
            query.append("UPDATE " + table + " SET ");

            query.append("name = '" + d.makeSafe(name) + "'" + ", ");
            query.append("price = " + price + " ");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder ret = new StringBuilder();
        ret.append(table);
        ret.append(": int id, string name, int price");

        return ret.toString();
    }
}
