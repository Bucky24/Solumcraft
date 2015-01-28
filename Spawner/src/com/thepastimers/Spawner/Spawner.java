package com.thepastimers.Spawner;

import com.thepastimers.Database.Database;
import com.thepastimers.Permission.Permission;
import com.thepastimers.Worlds.Worlds;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 5/2/13
 * Time: 8:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class Spawner extends JavaPlugin implements Listener {
    Database database;
    Permission permission;
    Worlds worlds;

    @Override
    public void onEnable() {
        getLogger().info("Spawner init");

        getServer().getPluginManager().registerEvents(this,this);
        database = (Database)getServer().getPluginManager().getPlugin("Database");
        if (database == null) {
            getLogger().warning("Cannot load Database plugin. Some functionality may not be available");
        }

        permission = (Permission)getServer().getPluginManager().getPlugin("Permission");
        if (permission == null) {
            getLogger().warning("Cannot load Permission plugin. Some functionality may not be available");
        }

        worlds = (Worlds)getServer().getPluginManager().getPlugin("Worlds");
        if (worlds == null) {
            getLogger().warning("Unable to load Worlds plugin. Some functionality may not be available.");
        }

        getLogger().info("Table info: ");
        getLogger().info(SpawnData.getTableInfo());

        getLogger().info("Spawner init complete");
    }

    @Override
    public void onDisable() {
        getLogger().info("Spawner disabled");
    }

    public List<SpawnData> getGroupSpawns(String group) {
        List<SpawnData> ret = new ArrayList<SpawnData>();
        if (database == null) return ret;

        ret = (List<SpawnData>)database.select(SpawnData.class,"`group` = '" + database.makeSafe(group) + "'");

        return ret;
    }

    public void activateSpawn(SpawnData spawn) {
        if (spawn == null) return;

        if ("zombie".equalsIgnoreCase(spawn.getWhat())) {
            World w = getServer().getWorld(spawn.getWorld());
            Location l = new Location(w,spawn.getX(),spawn.getY(),spawn.getZ());
            w.spawnEntity(l,EntityType.ZOMBIE);
        } else if ("armorzombie".equalsIgnoreCase(spawn.getWhat())) {
            World w = getServer().getWorld(spawn.getWorld());
            Location l = new Location(w,spawn.getX(),spawn.getY(),spawn.getZ());
            Entity e = w.spawnEntity(l,EntityType.ZOMBIE);
            LivingEntity le = (LivingEntity)e;
            EntityEquipment ee = le.getEquipment();
            ItemStack head = new ItemStack(Material.LEATHER_HELMET);
            ee.setHelmet(head);
        } else {
            getLogger().info("Unknown spawn thing: " + spawn.getWhat());
        }
    }

    public boolean createSpawn(float x, float y, float z, String what, String group, String world) {
        if (database == null) return false;
        SpawnData s = new SpawnData();
        s.setX(x);
        s.setY(y);
        s.setZ(z);
        s.setWhat(what);
        s.setGroup(group);
        s.setWorld(world);

        return s.save(database);
    }

    public List<SpawnData> getNearestSpawns(int x, int y, int z, String world, int count) {
        List<SpawnData> ret = new ArrayList<SpawnData>();
        if (world == null) return ret;

        List<SpawnData> spawnDatas = (List<SpawnData>)database.select(SpawnData.class,"1");

        List<SpawnData> nearest = new ArrayList<SpawnData>();

        for (int i=0;i<spawnDatas.size();i++) {
            SpawnData d = spawnDatas.get(i);
        }

        return ret;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        String playerName = "";

        if (sender instanceof Player) {
            playerName = ((Player)sender).getName();
        } else {
            playerName = "CONSOLE";
        }

        if (worlds != null && worlds.getPlayerWorldType(playerName) == Worlds.VANILLA) {
            return false;
        }

        String command = cmd.getName();

        if (command.equalsIgnoreCase("createspawn")) {
            if (permission == null || !permission.hasPermission(playerName,"spawn_create") || playerName.equalsIgnoreCase("CONSOLE")) {
                sender.sendMessage("You do not have permissions to use this command (spawn_create)");
                return true;
            }

            if (args.length > 1) {
                String what = args[0];
                String group = args[1];

                Player p = (Player)sender;
                Location l = p.getLocation();

                if (createSpawn((float)l.getX(),(float)l.getY(),(float)l.getZ(),what,group,l.getWorld().getName())) {
                    sender.sendMessage("Spawner created at " + l.getX() + "," + l.getY() + "," + l.getZ());
                } else {
                    sender.sendMessage("Unable to create spawner");
                }
            } else {
                sender.sendMessage("/createspawn <what> <group>");
            }
        } else if (command.equalsIgnoreCase("runspawn")) {
            if (permission == null || !permission.hasPermission(playerName,"spawn_spawn")) {
                sender.sendMessage("You do not have permissions to use this command (spawn_spawn)");
                return true;
            }

            if (args.length > 0) {
                String group = args[0];

                List<SpawnData> spawns = getGroupSpawns(group);

                sender.sendMessage("Activating " + spawns.size() + " spawns");
                for (SpawnData s : spawns) {
                    activateSpawn(s);
                }
            } else {
                sender.sendMessage("/runspawn <group>");
            }
        } else if (command.equalsIgnoreCase("listspawns")) {
            if (permission == null || !permission.hasPermission(playerName,"spawn_spawn")) {
                sender.sendMessage("You do not have permissions to use this command (spawn_spawn)");
                return true;
            }

            List<SpawnData> spawns = (List<SpawnData>)database.select(SpawnData.class,"1 GROUP BY `group`");

            sender.sendMessage("Spawn List:");
            for (SpawnData s : spawns) {
                sender.sendMessage(s.getGroup());
            }
        } else {
            return false;
        }

        return true;
    }
}
