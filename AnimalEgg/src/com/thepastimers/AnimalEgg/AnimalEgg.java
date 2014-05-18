package com.thepastimers.AnimalEgg;

import com.thepastimers.ItemName.ItemName;
import com.thepastimers.Permission.Permission;
import com.thepastimers.Worlds.Worlds;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.security.Permissions;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 5/18/14
 * Time: 12:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class AnimalEgg extends JavaPlugin implements Listener {
    Worlds worlds;
    Permission permission;
    ItemName itemName;

    String eggPerm = "egg_creature";

    @Override
    public void onEnable() {
        getLogger().info("AnimalEgg init");

        getServer().getPluginManager().registerEvents(this,this);


        permission = (Permission)getServer().getPluginManager().getPlugin("Permission");
        if (permission == null) {
            getLogger().warning("Unable to connect to Permission plugin");
        }

        worlds = (Worlds)getServer().getPluginManager().getPlugin("Worlds");
        if (worlds == null) {
            getLogger().warning("Unable to connect to Worlds plugin");
        }

        itemName = (ItemName)getServer().getPluginManager().getPlugin("ItemName");
        if (itemName == null) {
            getLogger().warning("Unable to connect to ItemName plugin");
        }

        getLogger().info("AnimalEgg init complete");
    }

    @Override
    public void onDisable() {
        getLogger().info("AnimalEgg disable");
    }

    @EventHandler
    public void entityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player p = (Player)event.getDamager();
            int type = worlds.getPlayerWorldType(p,false);
            if (type == Worlds.VANILLA) return;
            if (!permission.hasPermission(p.getName(),eggPerm)) return;
            if (p.getItemInHand().getType() != Material.STICK) return;

            event.setCancelled(true);

            Entity ent = event.getEntity();
            String egg = "";
            if (ent.getType() == EntityType.COW) egg = "COW_EGG";
            if (ent.getType() == EntityType.HORSE) egg  = "HORSE_EGG";
            if (ent.getType() == EntityType.PIG) egg  = "PIG_EGG";
            if (ent.getType() == EntityType.WOLF) egg  = "WOLF_EGG";
            if (ent.getType() == EntityType.MUSHROOM_COW) egg  = "MOOSHROOM_EGG";
            if (ent.getType() == EntityType.SHEEP) egg  = "SHEEP_EGG";
            if (ent.getType() == EntityType.CHICKEN) egg  = "CHICKEN_EGG";

            if (!"".equalsIgnoreCase(egg)) {
                event.setCancelled(true);
                Animals a = (Animals)ent;
                if (!a.isAdult()) {
                    return;
                }
                ItemStack is = itemName.getItemFromName(egg);
                if (is == null) {
                    p.sendMessage(ChatColor.RED + "Internal server error-cannot create item stack for egg type");
                    return;
                }
                is.setAmount(1);
                Location l = ent.getLocation();
                l.getWorld().dropItem(l,is);
                ent.remove();
            }
        }
    }
}
