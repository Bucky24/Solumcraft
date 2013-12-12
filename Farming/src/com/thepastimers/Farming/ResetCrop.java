package com.thepastimers.Farming;

import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
public class ResetCrop extends BukkitRunnable {

    private final JavaPlugin plugin;
    private Block block;
    private Material crop;

    public ResetCrop(JavaPlugin plugin, Block block, Material crop) {
        this.plugin = plugin;
        this.block = block;
        this.crop = crop;
    }

    public void run() {
        block.setType(crop);
        if (crop == Material.SEEDS) {
            Crops c = new Crops();
            c.setState(CropState.GERMINATED);
            block.getState().setData(c);
        } else {
            block.setData((byte)1);
        }
    }
}