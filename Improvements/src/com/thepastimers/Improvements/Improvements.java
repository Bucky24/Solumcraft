package com.thepastimers.Improvements;

import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
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
}
