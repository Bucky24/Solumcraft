package com.thepastimers.Inventory;

import com.thepastimers.Database.Database;
import com.thepastimers.Database.Table;
import com.thepastimers.ItemName.ItemName;
import com.thepastimers.Permission.Permission;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: rwijtman
 * Date: 9/12/13
 * Time: 2:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class Inventory extends JavaPlugin implements Listener {
    Database database;
    Permission permission;
    ItemName itemName;

    @Override
    public void onEnable() {
        getLogger().info("Inventory init");

        getServer().getPluginManager().registerEvents(this,this);

        database = (Database)getServer().getPluginManager().getPlugin("Database");
        if (database == null) {
            getLogger().warning("Cannot load Database plugin. Some functionality may not be available");
        }

        permission = (Permission)getServer().getPluginManager().getPlugin("Permission");
        if (permission == null) {
            getLogger().warning("Cannot load Permission plugin. Some functionality may not be available");
        }

        itemName = (ItemName)getServer().getPluginManager().getPlugin("ItemName");
        if (itemName == null) {
            getLogger().warning("Cannot load ItemName plugin. Some functionality may not be available");
        }

        InventoryItem.autoPopulate = true;
        if (database != null) {
            database.select(InventoryItem.class,"1 LIMIT 1");
        }
        getLogger().info("Table data:");
        getLogger().info(InventoryItem.getTableInfo(true));

        getLogger().info("Inventory init complete");
    }

    @Override
    public void onDisable() {
        getLogger().info("Inventory disabled");
    }

    public boolean saveInventory(Player p, String name) {
        if (database == null || itemName == null || p == null || name == null) {
            return false;
        }

        List<InventoryItem> iiList = new ArrayList<InventoryItem>();

        PlayerInventory pi = p.getInventory();
        ItemStack[] iss = pi.getContents();
        for (int i=0;i<iss.length;i++) {
            ItemStack is = iss[i];
            if (is == null) continue;

            String in = itemName.getItemName(is);

            InventoryItem ii = new InventoryItem();
            ii.setSlot("standard_" + i);
            ii.setInvName(name);
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

            iiList.add(ii);
        }

        return true;
    }
}
