package com.thepastimers.Director;

import com.thepastimers.Coord.Coord;
import com.thepastimers.Coord.CoordData;
import com.thepastimers.Database.Database;
import com.thepastimers.ItemName.ItemName;
import com.thepastimers.Permission.Permission;
import com.thepastimers.Spawner.Spawner;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 5/3/13
 * Time: 10:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class Director extends JavaPlugin implements Listener {
    Database database;
    Permission permission;
    Spawner spawner;
    ItemName itemName;
    Coord coord;

    @Override
    public void onEnable() {
        getLogger().info("Director init");

        getServer().getPluginManager().registerEvents(this,this);

        database = (Database)getServer().getPluginManager().getPlugin("Database");

        if (database == null) {
            getLogger().warning("Unable to load Database module. Some functionality may not be available.");
        }

        permission = (Permission)getServer().getPluginManager().getPlugin("Permission");

        if (permission == null) {
            getLogger().warning("Unable to load Permission module. Some functionality may not be available.");
        }

        spawner = (Spawner)getServer().getPluginManager().getPlugin("Spawner");

        if (spawner == null) {
            getLogger().warning("Unable to load Spawner module. Some functionality may not be available.");
        }

        itemName = (ItemName)getServer().getPluginManager().getPlugin("ItemName");

        if (itemName == null) {
            getLogger().warning("Unable to load ItemName module. Some functionality may not be available.");
        }

        coord = (Coord)getServer().getPluginManager().getPlugin("Coord");

        if (coord == null) {
            getLogger().warning("Unable to load Coord module. Some functionality may not be available.");
        }

        BukkitTask task = new Direct(this,database,spawner).runTaskTimer(this,0,20);

        getLogger().info("Table info: ");
        getLogger().info(Arena.getTableInfo());
        Arena.refreshCache(database,getLogger());

        getLogger().info("Director loaded");
    }

    @Override
    public void onDisable() {
        getLogger().info("Director disable");
    }

    public Arena getArena(double x, double y, double z, String world) {
        return getArena((int) x, (int) y, (int) z, world);
    }

    public Arena getArena(int x, int y, int z, String world) {
        return Arena.getArena(x,y,z,world);
    }

    public Arena getArenaById(int id) {
        return Arena.getArenaById(id);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        String playerName = "";

        if (sender instanceof Player) {
            playerName = ((Player)sender).getName();
        } else {
            playerName = "CONSOLE";
        }

        String command = cmd.getName();

        if (command.equalsIgnoreCase("arena")) {
            if (args.length > 0) {
                String subCommand = args[0];
                if ("create".equalsIgnoreCase(subCommand)) {
                    if (permission == null || !permission.hasPermission(playerName,"arena_create") || playerName.equalsIgnoreCase("CONSOLE")) {
                        sender.sendMessage(ChatColor.RED + "You don't have permissions for this command (arena_create)");
                        return true;
                    }

                    if (args.length > 1) {
                        String name = args[1];
                        List<CoordData> coords = coord.popCoords(playerName,2);

                        if (coords.size() < 2) {
                            sender.sendMessage(ChatColor.RED + "You need two coords set in order to create an arena");
                            return true;
                        }

                        CoordData c1 = coords.get(0);
                        CoordData c2 = coords.get(1);

                        int x1 = (int)c1.getX();
                        int z1 = (int)c1.getZ();
                        int y1 = (int)c1.getY();
                        int x2 = (int)c2.getX();
                        int z2 = (int)c2.getZ();
                        int y2 = (int)c2.getY();

                        if (x1 > x2) {
                            int tmp = x1;
                            x1 = x2;
                            x2 = tmp;
                        }

                        if (z1 > z2) {
                            int tmp = z1;
                            z1 = z2;
                            z2 = tmp;
                        }

                        if (y1 > y2) {
                            int tmp = y1;
                            y1 = y2;
                            y2 = tmp;
                        }

                        Player p = (Player)sender;
                        World w = p.getWorld();

                        if (getArena(x1,y1,z1,w.getName()) != null || getArena(x2,y2,z2,w.getName()) != null) {
                            sender.sendMessage(ChatColor.RED + "There is already an Arena intersecting that area");
                        }

                        Arena a = new Arena();
                        a.setX1(x1);
                        a.setY1(y1);
                        a.setZ1(z1);
                        a.setX2(x2);
                        a.setY2(y2);
                        a.setZ2(z2);
                        a.setWorld(w.getName());
                        a.setName(name);
                        if (a.save(database)) {
                            sender.sendMessage(ChatColor.RED + "Cannot save Arena data");
                        } else {
                            sender.sendMessage(ChatColor.GREEN + "Arena created!");
                        }
                    } else {
                        sender.sendMessage("/arena create <name>");
                    }
                }
            } else {
                sender.sendMessage(ChatColor.BLUE + "/arena <create>");
            }
        } else {
            return false;
        }

        return true;
    }
}
