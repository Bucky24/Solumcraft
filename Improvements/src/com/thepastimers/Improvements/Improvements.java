package com.thepastimers.Improvements;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 6/28/14
 * Time: 1:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class Improvements extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        getLogger().info("Improvements init");

        getServer().getPluginManager().registerEvents(this,this);

        getLogger().info("Improvements init complete");
    }

    @Override
    public void onDisable() {
        getLogger().info("Improvements disabled");
    }

    @EventHandler
    public void animalDamage(EntityDamageEvent event) {
        // don't allow baby chickens to drown or suffocate
        if (event.getEntityType() == EntityType.CHICKEN) {
            Chicken c = (Chicken)event.getEntity();
            if (!c.isAdult()) {
                if (event.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION ||
                    event.getCause() == EntityDamageEvent.DamageCause.DROWNING) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        Player p = event.getPlayer();
        ItemStack is = p.getItemInHand();
        // allow drops when glass is hit by a sword
        if (p.getGameMode() != GameMode.CREATIVE && (is.getType() == Material.WOOD_SWORD || is.getType() == Material.STONE_SWORD
            || is.getType() == Material.IRON_SWORD || is.getType() == Material.GOLD_SWORD
            || is.getType() == Material.DIAMOND_SWORD)) {
            Block b = event.getBlock();
            ItemStack drop = null;
            if (b.getType() == Material.GLASS) {
                drop = new ItemStack(Material.GLASS,1);
            } else if (b.getType() == Material.THIN_GLASS) {
                drop = new ItemStack(Material.THIN_GLASS,1);
            }
            if (drop != null) {
                event.setCancelled(true);
                b.setType(Material.AIR);
                b.getWorld().dropItemNaturally(b.getLocation(),drop);
            }
        }
    }
}
