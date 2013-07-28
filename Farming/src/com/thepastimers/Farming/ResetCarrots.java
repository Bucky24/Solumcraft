package com.thepastimers.Farming;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 6/9/13
 * Time: 8:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResetCarrots extends BukkitRunnable {

    private final JavaPlugin plugin;
    private Block block;

    public ResetCarrots(JavaPlugin plugin, Block block) {
        this.plugin = plugin;
        this.block = block;
    }

    public void run() {
        //plugin.getLogger().info("Running async thread");
        block.setType(Material.CARROT);
        block.setData((byte)0);
    }
}