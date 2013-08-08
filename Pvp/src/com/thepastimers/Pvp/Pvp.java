package com.thepastimers.Pvp;


import com.thepastimers.Database.Database;
import com.thepastimers.ItemName.ItemName;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created with IntelliJ IDEA.
 * User: rwijtman
 * Date: 8/8/13
 * Time: 11:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class Pvp extends JavaPlugin implements Listener {
    Database database;
    ItemName itemName;
    String world = "world";

    @Override
    public void onEnable() {
        getLogger().info("Pvp init");

        getServer().getPluginManager().registerEvents(this,this);

        database = (Database)getServer().getPluginManager().getPlugin("Database");

        if (database == null) {
            getLogger().warning("Unable to load Database module. Some functionality may not be available.");
        }

        itemName = (ItemName)getServer().getPluginManager().getPlugin("ItemName");

        if (itemName == null) {
            getLogger().warning("Unable to load ItemName module. Some functionality may not be available.");
        }

        getLogger().info("Printing table data:");
        getLogger().info(Heads.getTableInfo());

        getLogger().info("Pvp loaded");
    }

    @Override
    public void onDisable() {
        getLogger().info("Pvp disable");
    }

    @EventHandler
    public void onEntityDamagedByEntity(EntityDamageByEntityEvent e) {
        if (!world.equalsIgnoreCase(e.getEntity().getLocation().getWorld().getName())) {
            return;
        }
        if (e.getDamager() instanceof Player) {
            Player killer = (Player) e.getDamager();
            if (e.getEntityType() == EntityType.PLAYER) {
                Player killed = (Player) e.getEntity();
                if (killed.isDead() || killed.getHealth() <= 0) {
                    getLogger().info("A player killed another player");
                    itemName.giveItem(killer,"STEVE_HEAD",1);
                }
            }
        }
    }
}
