package com.thepastimers.Announce;

import com.thepastimers.Database.Database;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 12/4/13
 * Time: 11:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class Announce extends JavaPlugin implements Listener {
    Database database;

    @Override
    public void onEnable() {
        getLogger().info("Announce init");

        getServer().getPluginManager().registerEvents(this,this);

        database = (Database)getServer().getPluginManager().getPlugin("Announce");
        if (database == null) {
            getLogger().warning("Unable to load Database plugin. Some functionality may not be available.");
        }

        AnnounceData.refreshCache(database,getLogger());
        List<AnnounceData> data = AnnounceData.getAnnouncements();

        for (AnnounceData d : data) {
            int time;
            if (d.getUnits().equalsIgnoreCase("minutes")) {
                time = d.getFrequency();
            } else if (d.getUnits().equalsIgnoreCase("minutes")) {
                time = d.getFrequency()*60;
            } else if (d.getUnits().equalsIgnoreCase("hours")) {
                time = d.getFrequency()*60*60;
            } else {
                time = 0;
            }
            if (time != 0) {
                BukkitTask task = new AnnounceTimer(this,d).runTaskTimer(this,time,time);
            }
        }

        getLogger().info("Announce init complete");
    }

    @Override
    public void onDisable() {
        getLogger().info("Announce disabled");
    }
}
