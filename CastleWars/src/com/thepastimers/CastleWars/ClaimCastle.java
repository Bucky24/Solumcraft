package com.thepastimers.CastleWars;

import com.thepastimers.CastleWars.CastleData;
import com.thepastimers.Database.Database;
import com.thepastimers.Plot.PlotData;
import com.thepastimers.Plot.PlotPerms;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 6/9/13
 * Time: 8:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClaimCastle extends BukkitRunnable {
    private final Database database;
    private final CastleWars plugin;
    private final CastleData cd;
    private int time;
    private final Player player;

    public ClaimCastle(CastleWars plugin, Database d, CastleData cd,int time, Player p) {
        this.plugin = plugin;
        this.database = d;
        this.cd = cd;
        this.time = time;
        this.player = p;
    }

    public void run() {
        time --;
        if (time % 30 == 0 && time > 0) {
            player.sendMessage(ChatColor.GREEN + "" +  time + " seconds remain before you claim this castle");
        }
        if (time <= 0) {
            if (player != null) {
                PlotData pd = PlotData.getPlotById(cd.getPlot());
                String oldOwner = cd.getOwner();
                cd.setOwner(player.getName());
                pd.setName(player.getName() + "'s castle");
                if (pd.save(database)) {
                    if (cd.save(database)) {
                        player.sendMessage(ChatColor.GREEN + "You have successfully claimed this castle!");
                        if ("Unclaimed".equalsIgnoreCase(oldOwner)) {
                            plugin.getServer().broadcastMessage(ChatColor.GREEN + player.getName() + " has just claimed a castle");
                        } else {
                            plugin.getServer().broadcastMessage(ChatColor.GREEN + player.getName() + " has just stolen a castle from " + oldOwner);
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Unable to update castle!");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Unable to update plot!");
                }
            }
            this.cancel();
        } else {
            //plugin.getLogger().info("seconds remaining: " + time);
        }
    }
}