package com.thepastimers.Farming;

import com.thepastimers.ItemName.ItemName;
import com.thepastimers.Permission.Permission;
import com.thepastimers.Worlds.Worlds;
import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Crops;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
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

        if (permission != null && permission.hasPermission(event.getPlayer().getName(),"farming_replant")) {
            Player p = event.getPlayer();
            Block b = event.getBlock();
            Material m = null;

            //getLogger().info(b.getType().name());
            if (b.getType() == Material.CROPS) m = Material.SEEDS;
            if (b.getType() == Material.CARROT) m = Material.CARROT_ITEM;
            if (b.getType() == Material.POTATO) m = Material.POTATO_ITEM;

            if (m != null) {
                // right now there appears to be no method for all crops.
                if (m == Material.SEEDS) {
                    Crops c = (Crops)b.getState().getData();
                    if (c.getState() == CropState.RIPE) {
                        if (itemName.countInInventory(m.name(), p.getName()) > 0) {
                            if (itemName.takeItem(p,m.name(),1)) {
                                BukkitTask task = new ResetCrop(this,b,b.getType()).runTaskLater(this, 2);
                            }
                        }
                    }
                } else {
                    //getLogger().info(b.getData() + "");
                    if (b.getData() == 7) {
                        if (itemName.countInInventory(m.name(), p.getName()) > 0) {
                            if (itemName.takeItem(p,m.name(),1)) {
                                BukkitTask task = new ResetCrop(this,b,b.getType()).runTaskLater(this, 2);
                            }
                        }
                    }
                }
            }
        }
    }
}
