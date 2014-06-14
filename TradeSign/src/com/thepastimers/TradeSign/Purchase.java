package com.thepastimers.TradeSign;

import com.thepastimers.Database.Database;
import com.thepastimers.Database.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 7/29/13
 * Time: 5:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class Purchase extends Table {
    public static String table = "purchase";
    
    int id;
    
    public Purchase() {
        id = -1;
    }
    
    String fromPlayer;
    String purchaser;
    String item;
    int amount;
    int count;
    int sign;
    Timestamp time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFromPlayer() {
        return fromPlayer;
    }

    public void setFromPlayer(String fromPlayer) {
        this.fromPlayer = fromPlayer;
    }

    public String getPurchaser() {
        return purchaser;
    }

    public void setPurchaser(String purchaser) {
        this.purchaser = purchaser;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getSign() {
        return sign;
    }

    public void setSign(int sign) {
        this.sign = sign;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public static List<Purchase> parseResult(ResultSet result) throws SQLException {
        List<Purchase> ret = new ArrayList<Purchase>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            Purchase p = new Purchase();

            p.setId(result.getInt("id"));
            p.setFromPlayer(result.getString("from_player"));
            p.setPurchaser(result.getString("purchaser"));
            p.setItem(result.getString("item"));
            p.setAmount(result.getInt("amount"));
            p.setCount(result.getInt("count"));
            p.setSign(result.getInt("sign"));
            p.setTime(result.getTimestamp("time"));

            ret.add(p);
        }

        return ret;
    }
    
    public boolean save(Database d) {
        if (d == null) {
            return false;
        }
        if (id == -1) {
            String columns = "(from_player,purchaser,item,amount,count,sign,`time`)";
            String values = "('" + d.makeSafe(fromPlayer) + "','" + d.makeSafe(purchaser) + "','" + d.makeSafe(item) + "'," + amount
                    +  "," + count + "," + sign + ",'" + time + "')";
            boolean result = d.query("INSERT INTO " + table + columns + " VALUES" + values);

            return result;
        } else {
            StringBuilder query = new StringBuilder();
            query.append("UPDATE " + table + " SET ");

            query.append("from_player = '" + d.makeSafe(fromPlayer) + "'" + ", ");
            query.append("purchaser = '" + d.makeSafe(purchaser) + "', ");
            query.append("item = '" + d.makeSafe(item) + "', ");
            query.append("amount = " + amount + ", ");
            query.append("count = " + count + ", ");
            query.append("sign = " + sign + ", ");
            query.append("`time` = '" + time + "'");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder builder = new StringBuilder(table);

        builder.append(" int id, string from_player, string purchaser, string item, int amount, int count, int sign, timestamp time");

        return builder.toString();
    }

    public static boolean createTables(Database d, Logger l) {
        if (d == null) return false;
        String definition = "CREATE TABLE `purchase` (  `id` int(11) NOT NULL AUTO_INCREMENT,  " +
                "`from_player` varchar(100) NOT NULL,  `purchaser` varchar(100) NOT NULL,  " +
                "`item` varchar(100) NOT NULL,  `amount` int(11) NOT NULL,  `count` int(11) NOT NULL,  " +
                "`sign` int(11) NOT NULL,  " +
                "`time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,  " +
                "PRIMARY KEY (`id`)) ENGINE=InnoDB AUTO_INCREMENT=4943 DEFAULT CHARSET=latin1";
        boolean result = d.createTableIfNotExists(table,definition);
        if (!result && l != null) {
            l.warning("Unable to create table " + table);
        }

        return result;
    }
}
