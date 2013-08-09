package com.thepastimers.Pyro;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 8/1/13
 * Time: 6:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class Pyro extends JavaPlugin implements Listener {
    public String world = "anarchy";
    int chance = 500;

    @Override
    public void onEnable() {
        getLogger().info("Pyro init");

        getServer().getPluginManager().registerEvents(this,this);

        getLogger().info("Pyro init complete");
    }

    @Override
    public void onDisable() {
        getLogger().info("Pyro disabled");
    }

    @EventHandler
    public void fireDestroy(BlockBurnEvent event) {
        List<Block> neighbors = new ArrayList<Block>();
        Block b = event.getBlock();
        Location l = b.getLocation();

        if (b.getType() == Material.LEAVES) {
            return;
        }

        //getLogger().info("Block burned in " + l.getWorld().getName());

        if (!world.equalsIgnoreCase(l.getWorld().getName())) {
            return;
        }

        //getLogger().info("block burned");

        neighbors = getNeighbors(b);

        for (Block block : neighbors) {
            int r = (int)(Math.random()*1000);
            //getLogger().info("chance: " + r + " < " + chance);
            if (r < chance) {
                List<Block> n2 = getNeighbors(block);
                for (Block b3 : n2) {
                    if (b3.getType() == Material.AIR) {
                        //getLogger().info("Lit!");
                        b3.setType(Material.FIRE);
                        break;
                    }
                }
            }
        }
    }

    private List<Block> getNeighbors(Block b) {
        Location l = b.getLocation();
        List<Block> neighbors = new ArrayList<Block>();
        Block b2 = b.getWorld().getBlockAt(l.getBlockX()+1,l.getBlockY(),l.getBlockZ());
        neighbors.add(b2);
        b2 = b.getWorld().getBlockAt(l.getBlockX()-1,l.getBlockY(),l.getBlockZ());
        neighbors.add(b2);
        b2 = b.getWorld().getBlockAt(l.getBlockX(),l.getBlockY()-1,l.getBlockZ());
        neighbors.add(b2);
        b2 = b.getWorld().getBlockAt(l.getBlockX(),l.getBlockY()+1,l.getBlockZ());
        neighbors.add(b2);
        b2 = b.getWorld().getBlockAt(l.getBlockX(),l.getBlockY(),l.getBlockZ()-1);
        neighbors.add(b2);
        b2 = b.getWorld().getBlockAt(l.getBlockX(),l.getBlockY(),l.getBlockZ()+1);
        neighbors.add(b2);

        return neighbors;
    }
}
