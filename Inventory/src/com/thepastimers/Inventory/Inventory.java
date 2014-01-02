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
import java.util.Arrays;
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
        } else {
            InventoryItem.createTables(database,getLogger());
        }

        permission = (Permission)getServer().getPluginManager().getPlugin("Permission");
        if (permission == null) {
            getLogger().warning("Cannot load Permission plugin. Some functionality may not be available");
        }

        itemName = (ItemName)getServer().getPluginManager().getPlugin("ItemName");
        if (itemName == null) {
            getLogger().warning("Cannot load ItemName plugin. Some functionality may not be available");
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

        getLogger().info("Saving inventory for " + p.getName());

        List<InventoryItem> iiList = new ArrayList<InventoryItem>();

        PlayerInventory pi = p.getInventory();
        ItemStack[] iss = pi.getContents();
        for (int i=0;i<iss.length;i++) {
            ItemStack is = iss[i];
            if (is == null) continue;

            getLogger().info("Item " + i + ": " + is.getType().name() + ", durability:" + is.getDurability() + ", amount:" + is.getAmount() + ", enchants: " + Arrays.toString(is.getEnchantments().entrySet().toArray()));

            InventoryItem ii = InventoryItem.process(itemName,is);

            getLogger().info("Resulting data: " + ii.getItem() + ", durability: " + ii.getDurability() + ", amount:" + ii.getAmount() + ", enchants: " + ii.getEnchants());
            ii.setSlot("standard_" + i);
            ii.setInvName(name);

            iiList.add(ii);
        }

        iss = pi.getArmorContents();
        for (int i=0;i<iss.length;i++) {
            ItemStack is = iss[i];
            if (is == null) continue;
            InventoryItem ii = InventoryItem.process(itemName,is);
            ii.setSlot("armor_" + i);
            ii.setInvName(name);

            iiList.add(ii);
        }

        List<InventoryItem> iis = (List<InventoryItem>)database.select(InventoryItem.class,"invName = \"" + database.makeSafe(name) + "\"");
        for (int i=0;i<iis.size();i++) {
            if (!iis.get(i).delete(database)) {
                return false;
            }
        }

        for (int i=0;i<iiList.size();i++) {
            if (!iiList.get(i).save(database)) {
                return false;
            }
        }

        return true;
    }

    public void clearInventory(Player p) {
        PlayerInventory pi = p.getInventory();
        ItemStack[] is = new ItemStack[0];
        pi.setContents(is);
        pi.setArmorContents(is);
    }

    public boolean loadInventory(Player p, String name) {
        if (database == null || itemName == null || p == null || name == null) {
            return false;
        }

        List<InventoryItem> iiList = (List<InventoryItem>)database.select(InventoryItem.class, "invName = \"" + database.makeSafe(name) + "\"");

        ItemStack[] inv = new ItemStack[p.getInventory().getContents().length];
        ItemStack[] armorInv = new ItemStack[p.getInventory().getArmorContents().length];

        for (int i=0;i<iiList.size();i++) {
            InventoryItem ii = iiList.get(i);
            if (ii.getSlot().contains("standard_")) {
                String slot = ii.getSlot();
                slot.replace("standard_","");
                Integer slotId = Integer.parseInt(slot);
                inv[slotId] = ii.toItem(itemName);
            }
            if (ii.getSlot().contains("armor_")) {
                String slot = ii.getSlot();
                slot.replace("armor_","");
                Integer slotId = Integer.parseInt(slot);
                armorInv[slotId] = ii.toItem(itemName);
            }
        }

        PlayerInventory pi = p.getInventory();
        pi.setContents(inv);
        pi.setArmorContents(armorInv);

        return true;
    }
}
