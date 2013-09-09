package com.thepastimers.Logger;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 9/8/13
 * Time: 4:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class Logger extends JavaPlugin implements Listener {
    String file = "/minecraft/serverlog.log";
    String format = "<time>|<player>|<event>|<data>\n";

    @Override
    public void onEnable() {
        getLogger().info("Logger init");

        writeEvent("Plugin enabled");

        getServer().getPluginManager().registerEvents(this,this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Logger disable");
        writeEvent("Plugin disabled");
    }

    private String formatMessage(Date d, Player p, String event, String data) {
        String ret = format;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (d == null) {
            ret = ret.replace("<time>",formatter.format(new Date()));
        } else {
            ret = ret.replace("<time>",formatter.format(d));
        }

        if (p == null) {
            ret = ret.replace("<player>","NULL");
        } else {
            ret = ret.replace("<player>",p.getName());
        }

        if (event == null) {
            ret = ret.replace("<event>","NULL");
        } else {
            ret = ret.replace("<event>",event);
        }

        if (data == null) {
            ret = ret.replace("<data>","NULL");
        } else {
            ret = ret.replace("<data>",data);
        }

        return ret;
    }

    public void writeEvent(String event) {
        writeEvent(new Date(),null,event);
    }

    public void writeEvent(Player p, String event) {
        writeEvent(new Date(),p,event);
    }

    public void writeEvent(String event, String data) {
        writeEvent(new Date(),null,event,data);
    }

    public void writeEvent(Date d, Player p, String event) {
        writeEvent(d, p, event, null);
    }

    public void writeEvent(Player p, String event, String data) {
        writeEvent(new Date(), p, event, data);
    }

    public void writeEvent(Date d, Player p, String event, String data) {
        String message = formatMessage(d,p,event,data);

        //getLogger().info("Writing  " + message);
        try {
            FileWriter writer = new FileWriter(file,true);
            writer.write(message);
            writer.close();
        } catch (IOException e) {
            getLogger().info("Could not open file for writing: " + e.getMessage());
        }
    }

    @EventHandler
    public void login(PlayerLoginEvent event) {
        writeEvent(event.getPlayer(),"login");
    }

    @EventHandler
    public void join(PlayerJoinEvent event) {
        writeEvent(event.getPlayer(),"join");
    }

    // this event is normally handled by Chat plugin
    @EventHandler
    public void chat(AsyncPlayerChatEvent event) {
        if (getServer().getPluginManager().getPlugin("Chat") == null) {
            writeEvent(event.getPlayer(),"chat",event.getMessage());
        }
    }

    @EventHandler
    public void die(PlayerDeathEvent event) {
        writeEvent(event.getEntity(),"death",event.getDeathMessage());
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        Block b = event.getBlock();
        Location l = b.getLocation();
        writeEvent(event.getPlayer(),"block_break","(" + b.getType().name() + "," + b.getData() + ") (" + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ() + "," + l.getWorld().getName() + ")");
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent event) {
        Block b = event.getBlock();
        Location l = b.getLocation();
        writeEvent(event.getPlayer(),"block_place","(" + b.getType().name() + "," + b.getData() + ") (" + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ() + "," + l.getWorld().getName() + ")");
    }
}
