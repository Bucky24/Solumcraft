package com.thepastimers.Inventory;

import com.thepastimers.Database.Database;
import com.thepastimers.Database.Table;
import com.thepastimers.ItemName.ItemName;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: rwijtman
 * Date: 9/12/13
 * Time: 2:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class InventoryItem extends Table {
    public static String table = "inventory";

    String invName; // generic code
    String slot;
    String item;
    int amount;
    int durability;
    String enchants;

    public String getInvName() {
        return invName;
    }

    public void setInvName(String invName) {
        this.invName = invName;
    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
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

    public int getDurability() {
        return durability;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }

    public String getEnchants() {
        return enchants;
    }

    public void setEnchants(String enchants) {
        this.enchants = enchants;
    }

    public static List<InventoryItem> parseResult(ResultSet result) throws SQLException {
        List<InventoryItem> ret = new ArrayList<InventoryItem>();

        if (result == null) {
            return ret;
        }

        while (result.next()) {
            InventoryItem p = new InventoryItem();

            p.setId(result.getInt("id"));
            p.setInvName(result.getString("invName"));
            p.setSlot(result.getString("slot"));
            p.setItem(result.getString("item"));
            p.setAmount(result.getInt("amount"));
            p.setDurability(result.getInt("durability"));
            p.setEnchants(result.getString("enchants"));

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
            String columns = "(invName,slot,item,amount,durability,enchants)";
            String values = "('" + d.makeSafe(invName) + "','" + d.makeSafe(slot) + "','" + d.makeSafe(item) + "'," + amount + "," + durability
                    + ",'" + d.makeSafe(enchants) + "')";
            return d.query("INSERT INTO " + table + columns + " VALUES" + values);
        } else {
            StringBuilder query = new StringBuilder();
            query.append("UPDATE " + table + " SET ");

            query.append("invName = '" + d.makeSafe(invName) + "'" + ", ");
            query.append("slot = '" + d.makeSafe(slot) + "'" + ", ");
            query.append("item = '" + d.makeSafe(item) + "', ");
            query.append("amount = " + amount + ", ");
            query.append("durability = " + durability + ", ");
            query.append("enchants = '" + d.makeSafe(enchants) + "' ");

            query.append("WHERE id = " + id);
            return d.query(query.toString());
        }
    }

    public static String getTableInfo() {
        StringBuilder builder = new StringBuilder(table);

        builder.append(" int id, string invName, string slot, string item, int amount, int durability, text enchants, int data");

        return builder.toString();
    }

    public static boolean createTables(Database d, Logger l) {
        if (d == null) return false;
        StringBuilder definition = new StringBuilder("CREATE TABLE " + table + "(");
        definition.append("`id` int(11) NOT NULL AUTO_INCREMENT,");

        definition.append("`invName` varchar(100) NOT NULL,");
        definition.append("`slot` varchar(50) NOT NULL,");
        definition.append("`item` varchar(50) NOT NULL,");
        definition.append("`amount` int(11) NOT NULL,");
        definition.append("`durability` int(11) NOT NULL,");
        definition.append("`enchants` text NOT NULL,");

        definition.append("PRIMARY KEY (`id`)");
        definition.append(");");
        boolean result = d.createTableIfNotExists(table,definition.toString());

        if (!result && l != null) {
            l.warning("Unable to create table " + table);
        }

        return result;
    }

    public static InventoryItem process(ItemName itemName, ItemStack is) {
        if (is == null) return null;

        String in = itemName.getItemName(is);

        InventoryItem ii = new InventoryItem();
        ii.setAmount(is.getAmount());
        ii.setDurability(is.getDurability());
        ii.setItem(in);

        String enchants = "";
        Map<Enchantment,Integer> enchantMap = is.getEnchantments();

        for (Enchantment e : enchantMap.keySet()) {
            int level = enchantMap.get(e);
            enchants += e.getName() + "||" + level + ",";
        }
        ii.setEnchants(enchants);

        return ii;
    }

    public ItemStack toItem(ItemName itemName) {
        ItemStack is = itemName.getItemFromName(item);
        is.setAmount(amount);
        is.setDurability((short)durability);
        Map<Enchantment, Integer> enMap = new HashMap<Enchantment, Integer>();

        String[] enchantList = enchants.split(",");
        for (int i=0;i<enchantList.length;i++) {
            if (enchantList[i] != "") {
                String[] list2 = enchantList[i].split("||");
                enMap.put(Enchantment.getByName(list2[0]),Integer.parseInt(list2[1]));
            }
        }
        is.addEnchantments(enMap);

        return is;
    }
}
