package com.thepastimers.Worlds;

import com.thepastimers.CombatLog.CombatLog;
import com.thepastimers.Database.Database;
import com.thepastimers.Inventory.Inventory;
import com.thepastimers.Rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static int MAGIC = 4;

    static Map<Player,Location> deathLocs;

    Database database;
    CombatLog combatLog;
    Rank rank;
    Inventory inventory;

    @Override
    public void onEnable() {
        getLogger().info("Worlds init");

        getServer().getPluginManager().registerEvents(this,this);

        database = (Database)getServer().getPluginManager().getPlugin("Database");
        if (database == null) {
            getLogger().warning("Unable to load database plugin. Some functionality may be unavailable.");
        } else {
            WorldCoords.createTables(database,getLogger());
        }

        combatLog = (CombatLog)getServer().getPluginManager().getPlugin("CombatLog");
        if (combatLog == null) {
            getLogger().warning("Cannot load CombatLog plugin. Some functionality may not be available");
        }

        inventory = (Inventory)getServer().getPluginManager().getPlugin("Inventory");
        if (inventory == null) {
            getLogger().warning("Cannot load Inventory plugin. Some functionality may not be available");
        }

        rank = (Rank)getServer().getPluginManager().getPlugin("Rank");
        if (rank == null) {
            // ignore, will attempt to load whenever it is required.
        }

        deathLocs = new HashMap<Player, Location>();

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
            //getLogger().info("CREATURE_SPAWN: " + event.getSpawnReason().name() + " type: " + event.getEntity().getType().name());
            if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) {
                Entity e = event.getEntity();
                if (e.getType() != EntityType.PIG_ZOMBIE) {
                    Location l = e.getLocation();
                    getLogger().info("Spawn egg used at (" + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ() + "," + l.getWorld().getName() + "). Creature: " + e.getType().name());
                    for (Player p : getServer().getOnlinePlayers()) {
                        Location l2 = p.getLocation();
                        getLogger().info(p.getName() + " at (" + l2.getBlockX() + "," + l2.getBlockY() + "," + l2.getBlockZ() + "," + l2.getWorld().getName() + ")");
                    }
                }
            }
        }
    }

    public int getPlayerWorldType(String player) {
        return getPlayerWorldType(player,true);
    }

    public int getPlayerWorldType(String player, boolean useRank) {
        Player p = getServer().getPlayer(player);
        if (rank == null) {
            rank = (Rank)getServer().getPluginManager().getPlugin("Rank");
        }
        if (rank != null && useRank) {
            String type = rank.getRank(player);
            if (type.equalsIgnoreCase("owner") || type.equalsIgnoreCase("admin")) {
                return NORMAL;
            }
        }
        if (p != null) {
            World w = p.getLocation().getWorld();
            String name = w.getName();
            if ("vanilla".equalsIgnoreCase(name)) {
                return VANILLA;
            } else if ("economy".equalsIgnoreCase(name)) {
                return ECONOMY;
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
            } else if ("economy".equalsIgnoreCase(name)) {
                return ECONOMY;
            }
        }

        return NORMAL;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player p = event.getEntity();
        deathLocs.put(p,p.getLocation());
        //getLogger().info("Player dead, adding to deathLocs: " + deathLocs.keySet().size());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player p = event.getPlayer();
        //getLogger().info("Respawn event!");
        Location dl = deathLocs.get(p);
        if (dl == null) return;
        //getLogger().info("Player died in " + dl.getWorld().getName());
        if (getWorldType(dl.getWorld().getName()) == Worlds.VANILLA) {
           // getLogger().info("Player death in vanilla world");
            World w = dl.getWorld();
            Location l = event.getRespawnLocation();
            if (!l.getWorld().getName().equalsIgnoreCase(w.getName())) {
                //getLogger().info("Player bed not set in same world");
                event.setRespawnLocation(w.getSpawnLocation());
            }
        }

        deathLocs.remove(p);
    }

    @EventHandler
    public void placeBlock(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        if (getPlayerWorldType(p.getName()) == Worlds.ECONOMY) {
            if (event.getBlockPlaced().getType() == Material.ENDER_CHEST) {
                p.sendMessage(ChatColor.RED + "You cannot place ender chests in this world");
                event.setCancelled(true);
            }
        }
    }

    public boolean updatePlayerLocation(Player p) {
        Location pl = p.getLocation();
        World w = pl.getWorld();

        List<WorldCoords> worldList = (List<WorldCoords>)database.select(WorldCoords.class,"world = \"" + database.makeSafe(w.getName()) + "\" AND player = \"" + database.makeSafe(p.getName()) + "\"");

        WorldCoords wc;
        if (worldList.size() > 0) {
            wc = worldList.get(0);
            wc.setX(pl.getX());
            wc.setY(pl.getY());
            wc.setZ(pl.getZ());
        } else {
            wc = new WorldCoords();
            wc.setPlayer(p.getName());
            wc.setWorld(p.getWorld().getName());
            wc.setX(pl.getX());
            wc.setY(pl.getY());
            wc.setZ(pl.getZ());
        }
        return wc.save(database);
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

            if (args.length > 0) {
                String world = args[0];
                if (world.equalsIgnoreCase("main")) {
                    world = "world";
                }

                World w = getServer().getWorld(world);
                if (w == null) {
                    sender.sendMessage("World does not exist");
                    return true;
                }

                boolean force = false;
                if (args.length > 1) {
                    if (args[1].equalsIgnoreCase("force")) force = true;
                }

                if (combatLog != null) {
                    int seconds = combatLog.secondsSinceCombat(playerName);
                    if (seconds > -1 && seconds < 10) {
                        sender.sendMessage(ChatColor.RED + "You were recently in combat. You must wait another " + (10-seconds) + " seconds before you can use /home");
                        return true;
                    }
                }

                Player p = (Player)sender;
                World currWorld = p.getWorld();

                if (!updatePlayerLocation(p) && !force) {
                    sender.sendMessage(ChatColor.RED + "Cannot connect to database-your position in this world cannot be saved. Use /go " + world + " force to go anyway");
                    return true;
                }

                boolean saved = false;
                if (inventory != null) {
                    saved = inventory.saveInventory(p,currWorld.getName() + "_" + p.getName());
                }

                if (!saved) {
                    sender.sendMessage(ChatColor.RED + "Cannot save your inventory. Use /go " + world + " force to go anyway. Warning: If you do this you may lose your entire inventory. It is recommended that you empty it before attempting to go to another world.");
                    return true;
                } else {
                    //inventory.clearInventory(p);
                }



                List<WorldCoords> worldList = (List<WorldCoords>)database.select(WorldCoords.class,"world = \"" + database.makeSafe(world) + "\" AND player = \"" + database.makeSafe(p.getName()) + "\"");

                Location l = w.getSpawnLocation();

                if (worldList.size() > 0) {
                    WorldCoords wc = worldList.get(0);
                    l = new Location(getServer().getWorld(wc.getWorld()),wc.getX(),wc.getY(),wc.getZ());
                }

                p.teleport(l);
            } else {
                sender.sendMessage("/go <world> [force]");
                sender.sendMessage("Worlds: main,vanilla");
            }
        } else {
            return false;
        }

        return true;
    }
}
