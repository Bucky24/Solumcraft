package com.thepastimers.Worlds;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 8/6/13
 * Time: 10:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class Worlds extends JavaPlugin implements Listener {
    public static int NORMAL = 1;
    public static int VANILLA = 2;
    public static int ECONOMY = 3;

    @Override
    public void onEnable() {
        getLogger().info("Worlds init");

        getServer().getPluginManager().registerEvents(this,this);

        getLogger().info("Worlds init complete");
    }

    @Override
    public void onDisable() {
        getLogger().info("Worlds disable");
    }

    @EventHandler
    public void spawn(CreatureSpawnEvent event) {
        EntityType type = event.getEntityType();

        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL || event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CHUNK_GEN
                || event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.DEFAULT) {

            if (!"economy".equalsIgnoreCase(event.getLocation().getWorld().getName())) {
                return;
            }

            event.setCancelled(true);
            event.getEntity().setHealth(0);
        } else {
            getLogger().info("CREATURE_SPAWN: " + event.getSpawnReason().name() + " type: " + event.getEntity().getType().name());
            if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) {
                Entity e = event.getEntity();
                Location l = e.getLocation();
                getLogger().info("Spawn egg used at (" + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ() + "," + l.getWorld().getName() + ")");
                for (Player p : getServer().getOnlinePlayers()) {
                    Location l2 = p.getLocation();
                    getLogger().info(p.getName() + " at (" + l2.getBlockX() + "," + l2.getBlockY() + "," + l2.getBlockZ() + "," + l2.getWorld().getName() + ")");
                }
            }
        }
    }

    public int getPlayerWorldType(String player) {
        Player p = getServer().getPlayer(player);
        if (p != null) {
            World w = p.getLocation().getWorld();
            String name = w.getName();
            if ("vanilla".equalsIgnoreCase(name)) {
                return VANILLA;
            }
        }

        return NORMAL;
    }

    public int getWorldType(String world) {
        World w = getServer().getWorld(world);
        if (w != null) {
            String name = w.getName();
            if ("vanilla".equalsIgnoreCase(name)) {
                return VANILLA;
            }
        }

        return NORMAL;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        String playerName = "";

        if (sender instanceof Player) {
            playerName = ((Player)sender).getName();
        } else {
            playerName = "CONSOLE";
        }

        String command = cmd.getName();

        getLogger().info("Got command " + command);

        if (command.equalsIgnoreCase("go")) {
            getLogger().info("Got command from " + playerName);
            if (!playerName.equalsIgnoreCase("CONSOLE")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this command (console only)");
                return true;
            }

            if (args.length > 1) {
                String world = args[0];

                World w = getServer().getWorld(world);
                if (w == null) {
                    sender.sendMessage("World does not exist");
                    return true;
                }
                for (int i=1;i<args.length;i++) {
                    Player p = getServer().getPlayer(args[i]);
                    if (p != null) {
                        Location l = w.getSpawnLocation();
                        p.teleport(l);
                    }
                }
            } else {
                sender.sendMessage("/goto <world> <players>");
            }
        } else {
            return false;
        }

        return true;
    }
}
