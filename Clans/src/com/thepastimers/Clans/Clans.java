package com.thepastimers.Clans;

import com.thepastimers.Database.Database;
import com.thepastimers.Logger.Logger;
import com.thepastimers.Permission.Permission;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created with IntelliJ IDEA.
 * User: rwijtman
 * Date: 9/11/13
 * Time: 2:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class Clans extends JavaPlugin implements Listener {
    Database database;
    Permission permission;
    Logger logger;

    @Override
    public void onEnable() {
        getLogger().info("Clans init");

        getServer().getPluginManager().registerEvents(this,this);

        database = (Database)getServer().getPluginManager().getPlugin("Database");
        if (database == null) {
            getLogger().warning("Unable to load Database plugin. Some functionality may not be available");
        }

        permission = (Permission)getServer().getPluginManager().getPlugin("Permission");
        if (permission == null) {
            getLogger().warning("Unable to load Permission plugin. Some functionality may not be available");
        }

        logger = (Logger)getServer().getPluginManager().getPlugin("Logger");
        if (logger == null) {
            getLogger().warning("Unable to load Logger plugin. Some functionality may not be available");
        }

        getLogger().info("Clans init complete");
    }

    @Override
    public void onDisable() {
        getLogger().info("Clans disable");
    }
}
