package com.thepastimers.Logger;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
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
    public static String file = "/minecraft/serverlog.log";
    public static String moveFile = "/minecraft/servermovelog.log";
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

    private String formatMessage(Date d, String p, String event, String data) {
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
            ret = ret.replace("<player>",p);
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

    public void writeEvent(OfflinePlayer p, String event, String data) {
        writeEvent(new Date(), p, event, data);
    }

    public void writeEvent(Date d, Player p, String event, String data) {
        if (p != null) {
            writeEvent(file,d,p.getName(),event,data);
        } else {
            writeEvent(file,d,null,event,data);
        }
    }

    public void writeEvent(Date d, OfflinePlayer p, String event, String data) {
        if (p != null) {
            writeEvent(file,d,p.getName(),event,data);
        } else {
            writeEvent(file,d,null,event,data);
        }
    }

    public void writeEvent(String logFile, Date d, String p, String event, String data) {
        String message = formatMessage(d,p,event,data);

        //getLogger().info("Writing  " + message);
        try {
            FileWriter writer = new FileWriter(logFile,true);
            writer.write(message);
            writer.close();
        } catch (IOException e) {
            getLogger().info("Could not open file for writing: " + e.getMessage());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void login(PlayerLoginEvent event) {
        writeEvent(event.getPlayer(),"login","From: " + event.getAddress());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void join(PlayerJoinEvent event) {
        writeEvent(event.getPlayer(),"join");
    }

    // this event is normally handled by Chat plugin
    @EventHandler(priority = EventPriority.MONITOR)
    public void chat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return;
        if (getServer().getPluginManager().getPlugin("Chat") == null) {
            writeEvent(event.getPlayer(),"chat",event.getMessage());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void die(PlayerDeathEvent event) {
        writeEvent(event.getEntity(),"death",event.getDeathMessage());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void blockBreak(BlockBreakEvent event) {
        Block b = event.getBlock();
        Location l = b.getLocation();
        String evt = "block_break";
        if (event.isCancelled()) evt = "canceled_block_break";
        writeEvent(event.getPlayer(),evt,"(" + b.getType().name() + "," + b.getData() + ") (" + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ() + "," + l.getWorld().getName() + ")");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void blockPlace(BlockPlaceEvent event) {
        Block b = event.getBlock();
        Location l = b.getLocation();
        String evt = "block_place";
        if (event.isCancelled()) evt = "canceled_block_place";
        writeEvent(event.getPlayer(),evt,"(" + b.getType().name() + "," + b.getData() + ") (" + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ() + "," + l.getWorld().getName() + ")");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void leave(PlayerQuitEvent event) {
        writeEvent(event.getPlayer(),"logout");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        //getLogger().info("Move event");

        String evt = "move";
        if (event.isCancelled()) evt = "canceled_move";

        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY()
                && from.getBlockZ() == to.getBlockZ()
                && from.getWorld().getName().equalsIgnoreCase(to.getWorld().getName())) {
            return;
        }
        writeEvent(moveFile, null, event.getPlayer().getName(), evt, "From (" + from.getBlockX() + ","
                + from.getBlockY() + "," + from.getBlockZ() + ","
                + from.getWorld().getName() + ") To (" + to.getBlockX() + ","
                + from.getBlockY() + "," + from.getBlockZ() + ","
                + from.getWorld().getName() + ")");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerTeleport(PlayerTeleportEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        String evt = "teleport";
        if (event.isCancelled()) evt = "canceled_teleport";
        writeEvent(event.getPlayer(),evt,"From (" + from.getBlockX() + ","
                + from.getBlockY() + "," + from.getBlockZ() + ","
                + from.getWorld().getName() + ") To (" + to.getBlockX() + ","
                + from.getBlockY() + "," + from.getBlockZ() + ","
                + from.getWorld().getName() + ")");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void creatureSpawn(CreatureSpawnEvent event) {
        Location l = event.getLocation();
        if (event.isCancelled()) return;
        writeEvent("spawn","Entity: " + event.getEntity().getType().getName()
                + ", reason: " + event.getSpawnReason().name()
                + ", at (" + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ() + "," + l.getWorld().getName() + ")");
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void throwDown(PlayerDropItemEvent event) {
        ItemStack is = event.getItemDrop().getItemStack();

        String evt = "dropped";
        if (event.isCancelled()) evt = "canceled_dropped";

        writeEvent(event.getPlayer(),"dropped","Item: "
                + is.getType().name()
                + ". Durability: " + is.getDurability() + "/" + is.getType().getMaxDurability() + ". Amount: " + event.getItemDrop().getItemStack().getAmount());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void pickup(PlayerPickupItemEvent event) {
        ItemStack is = event.getItem().getItemStack();

        String evt = "pickup";
        if (event.isCancelled()) evt = "canceled_pickup";

        writeEvent(event.getPlayer(), "pickup", "Item: "
                + is.getType().name()
                + ". Durability: " + is.getDurability() + "/" + is.getType().getMaxDurability() + ". Amount: " + is.getAmount());
    }
}
