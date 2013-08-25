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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 6/9/13
 * Time: 8:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class CastleIncome extends BukkitRunnable {
    private final Database database;
    private final CastleWars plugin;
    private int time;

    public CastleIncome(CastleWars plugin, Database d) {
        this.plugin = plugin;
        this.database = d;
    }

    public void run() {
        DateFormat dateFormat = new SimpleDateFormat("mm");
        Date date = new Date();

        String minutes = dateFormat.format(date);

        //plugin.getLogger().info("MINutes: " + minutes);
        if ("0".equalsIgnoreCase(minutes) || "00".equalsIgnoreCase(minutes)) {
            plugin.getLogger().info("Triggering income");
            plugin.triggerIncome();
        }
    }
}