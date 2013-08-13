package com.thepastimers.Boundry;

import com.thepastimers.Permission.Permission;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 8/1/13
 * Time: 6:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class Boundry extends JavaPlugin implements Listener {
    int limit = 5000;
    String world = "economy";

    @Override
    public void onEnable() {
        getLogger().info("Boundry init");

        getServer().getPluginManager().registerEvents(this,this);

        getLogger().info("Boundry init complete");
    }

    @Override
    public void onDisable() {
        getLogger().info("Boundry disabled");
    }

    @EventHandler
    public void playerMove(PlayerMoveEvent event) {
        Location l = event.getTo();
        if (world.equalsIgnoreCase(l.getWorld().getName())) {
            if (l.getBlockX() > limit || l.getBlockX() < -limit || l.getBlockZ() > limit || l.getBlockZ() < -limit) {
                event.getPlayer().sendMessage(ChatColor.RED + "You have reached the edge of this world");
                int newx = l.getBlockX();
                int newy = l.getBlockY();
                int newz = l.getBlockZ();
                if (l.getBlockX() > limit) {
                    newx -= 5;
                }
                if (l.getBlockX() < -limit) {
                    newx += 5;
                }
                if (l.getBlockZ() > limit) {
                    newz -= 5;
                }
                if (l.getBlockZ() < -limit) {
                    newz += 5;
                }
                event.getPlayer().teleport(new Location(l.getWorld(),newx,newy,newz));
            }
        }
    }

    @EventHandler
    public void playerTeleport(PlayerTeleportEvent event) {
        Location l = event.getTo();
        if (world.equalsIgnoreCase(l.getWorld().getName())) {
            if (l.getBlockX() > limit || l.getBlockX() < -limit || l.getBlockZ() > limit || l.getBlockZ() < -limit) {
                event.getPlayer().sendMessage(ChatColor.RED + "You have reached the edge of this world");
                int newx = l.getBlockX();
                int newy = l.getBlockY();
                int newz = l.getBlockZ();
                if (l.getBlockX() > limit) {
                    newx = limit-5;
                }
                if (l.getBlockX() < -limit) {
                    newx = limit+5;
                }
                if (l.getBlockZ() > limit) {
                    newz = limit-5;
                }
                if (l.getBlockZ() < -limit) {
                    newz = limit+5;
                }
                event.getPlayer().teleport(new Location(l.getWorld(),newx,newy,newz));
            }
        }
    }
}
