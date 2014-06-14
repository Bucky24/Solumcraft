package com.thepastimers.Money;

import com.thepastimers.Database.Database;
import com.thepastimers.Database.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: derp
 * Date: 10/6/12
 * Time: 12:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class MoneyData extends Table {
    public static String table = "money";
    
    int id;
    
    public MoneyData() {
        id = -1;
    }
    
    String player;
    int balance;

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

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public static List<MoneyData> parseResult(ResultSet result) throws SQLException {
        List<MoneyData> ret = new ArrayList<MoneyData>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            MoneyData p = new MoneyData();

            p.setId(result.getInt("id"));
            p.setPlayer(result.getString("player"));
            p.setBalance(result.getInt("balance"));

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
            String columns = "(player,balance)";
            String values = "('" + d.makeSafe(player) + "'," + balance + ")";
            return d.query("INSERT INTO " + table + columns + " VALUES" + values);
        } else {
            StringBuilder query = new StringBuilder();
            query.append("UPDATE " + table + " SET ");

            query.append("player = '" + d.makeSafe(player) + "'" + ", ");
            query.append("balance = " + balance + " ");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder ret = new StringBuilder();
        ret.append(table);
        ret.append(": int id, string player, int balance");

        return ret.toString();
    }

    public static boolean createTables(Database d, Logger l) {
        if (d == null) return false;
        StringBuilder definition = new StringBuilder("CREATE TABLE " + table + "(");
        definition.append("`id` int(11) NOT NULL AUTO_INCREMENT,");

        definition.append("`player` varchar(300) NOT NULL,");
        definition.append("`balance` int(11) NOT NULL,");

        definition.append("PRIMARY KEY (`id`)");
        definition.append(");");
        boolean result = d.createTableIfNotExists(table,definition.toString());

        if (!result && l != null) {
            l.warning("Unable to create table " + table);
        }

        return result;
    }
}
