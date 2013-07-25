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
 * Date: 7/24/13
 * Time: 5:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class Sale extends Table {
    public static String table = "sale";

    int id;

    public Sale() {
        id = -1;
    }

    String item;
    String player;
    int amount;
    int price;

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

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public static List<Sale> parseResult(ResultSet result) throws SQLException {
        List<Sale> ret = new ArrayList<Sale>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            Sale p = new Sale();

            p.setId(result.getInt("id"));
            p.setPlayer(result.getString("player"));
            p.setAmount(result.getInt("amount"));
            p.setPrice(result.getInt("price"));
            p.setItem(result.getString("item"));

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
            String columns = "(player,item,amount,price)";
            String values = "('" + d.makeSafe(player) + "','" + d.makeSafe(item) + "'," + amount + "," + price + ")";
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

            query.append("player = '" + d.makeSafe(player) + "', ");
            query.append("item = '" + d.makeSafe(item) + "', ");
            query.append("amount = " + amount + ", ");
            query.append("price = " + price + " ");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder builder = new StringBuilder(table);

        builder.append(" int id, string player, string item, int amount, int price");

        return builder.toString();
    }
}
