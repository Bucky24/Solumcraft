package com.thepastimers.Metrics;

import org.bukkit.CropState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.material.Crops;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 6/9/13
 * Time: 8:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class TeleportPlayer extends BukkitRunnable {

    private final JavaPlugin plugin;
    private Player player;
    private World world;

    public TeleportPlayer(JavaPlugin plugin, Player p, World w) {
        this.plugin = plugin;
        this.player = p;
        this.world = w;
    }

    public void run() {
        player.teleport(world.getSpawnLocation());
        Location l = player.getLocation();
        plugin.getLogger().info("Teleported " + player.getName() + " to " + world.getName() + " spawn. They are now at (" + l.getX() + "," + l.getY() + "," + l.getZ() + "," + l.getWorld().getName() + ")");
    }
}