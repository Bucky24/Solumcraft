package com.thepastimers.Announce;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 12/4/13
 * Time: 11:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class AnnounceTimer extends BukkitRunnable {
    private final JavaPlugin plugin;
    private final AnnounceData data;

    public AnnounceTimer(JavaPlugin plugin, AnnounceData d) {
        this.plugin = plugin;
        this.data = d;
    }

    public void run() {
        if (data != null && plugin != null) {
            plugin.getServer().broadcastMessage(data.getAnnounce());
        }
    }
}
