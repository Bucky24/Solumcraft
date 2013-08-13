package com.thepastimers.CombatLog;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 7/2/13
 * Time: 6:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class CombatLog extends JavaPlugin implements Listener {
    Map<String,Date> lastCombat;
    @Override
    public void onEnable() {
        getLogger().info("CombatLog init");

        getServer().getPluginManager().registerEvents(this,this);

        lastCombat = new HashMap<String,Date>();

        getLogger().info("CombatLog init complete");
    }

    @Override
    public void onDisable() {
        getLogger().info("CombatLot disable");
    }

    @EventHandler
    public void entityHit(EntityDamageByEntityEvent event) {
        Entity e1 = event.getDamager();
        Entity e2 = event.getEntity();

        if (e1 instanceof Player && e2 instanceof Player) {
            Player p = (Player)e2;
            Player p2 = (Player)e1;
            Date now = new Date();

            lastCombat.put(p.getName(),now);
            lastCombat.put(p2.getName(),now);
        }
    }

    public int secondsSinceCombat(String player) {
        if (lastCombat.containsKey(player)) {
            Date now = new Date();
            long diff = now.getTime() - ((Date)lastCombat.get(player)).getTime();
            diff /= 1000;

            return (int)diff;
        }

        return -1;
    }
}
