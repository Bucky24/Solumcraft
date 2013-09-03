package com.thepastimers.Farming;

import com.thepastimers.ItemName.ItemName;
import com.thepastimers.Permission.Permission;
import com.thepastimers.Worlds.Worlds;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 6/8/13
 * Time: 5:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class Farming extends JavaPlugin implements Listener {
    Permission permission;
    ItemName itemName;
    Worlds worlds;

    @Override
    public void onEnable() {
        getLogger().info("Farming init");

        getServer().getPluginManager().registerEvents(this,this);

        permission = (Permission)getServer().getPluginManager().getPlugin("Permission");
        if (permission == null) {
            getLogger().warning("Unable to load Permission plugin. Some functionality may not be available.");
        }

        itemName = (ItemName)getServer().getPluginManager().getPlugin("ItemName");
        if (itemName == null) {
            getLogger().warning("Unable to load ItemName plugin. Some functionality may not be available.");
        }

        worlds = (Worlds)getServer().getPluginManager().getPlugin("Worlds");
        if (worlds == null) {
            getLogger().warning("Unable to load Worlds plugin. Some functionality may not be available.");
        }

        getLogger().info("Farming init complete");
    }

    @Override
    public void onDisable() {
        getLogger().info("Farming disabled");
    }

    @EventHandler
    public void playerThing(PlayerInteractEvent event) {
        if (worlds != null && worlds.getPlayerWorldType(event.getPlayer().getName()) == Worlds.VANILLA) {
            return;
        }
        if (event.getAction() == Action.PHYSICAL) {
            if (event.getClickedBlock().getType() == Material.SOIL) {
                if (permission != null && permission.hasPermission(event.getPlayer().getName(),"farming_break")) {
                    event.setCancelled(true);
                }
            }
            //getLogger().info("Player interact event to block " + event.getClickedBlock().getType().name());
        }
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        if (worlds != null && worlds.getPlayerWorldType(event.getPlayer().getName()) == Worlds.VANILLA) {
            return;
        }
        Player p = event.getPlayer();
        Block b = event.getBlock();
        if (b.getType() == Material.CROPS && b.getData() == 7) {
            if (permission != null && permission.hasPermission(event.getPlayer().getName(),"farming_replant")) {
                if (itemName.countInInventory(Material.SEEDS.name(),p.getName()) > 0) {
                    if (itemName.takeItem(p,Material.SEEDS.name(),1)) {
                        BukkitTask task = new ResetWheat(this,b).runTaskLater(this, 2);
                    }
                }
            }
        }

        if (b.getType() == Material.CARROT && b.getData() == 7) {
            if (permission != null && permission.hasPermission(event.getPlayer().getName(),"farming_replant")) {
                if (itemName.countInInventory(Material.CARROT_ITEM.name(),p.getName()) > 0) {
                    if (itemName.takeItem(p,Material.CARROT_ITEM.name(),1)) {
                        BukkitTask task = new ResetCarrots(this,b).runTaskLater(this, 2);
                    }
                }
            }
        }
    }
}
