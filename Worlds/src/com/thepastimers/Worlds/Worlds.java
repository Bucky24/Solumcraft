package com.thepastimers.Worlds;

import com.thepastimers.CombatLog.CombatLog;
import com.thepastimers.Database.Database;
import com.thepastimers.Inventory.Inventory;
import com.thepastimers.Rank.Rank;
import com.thepastimers.UserMap.UserMap;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
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
    UserMap userMap;

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

        userMap = (UserMap)getServer().getPluginManager().getPlugin("UserMap");
        if (userMap == null) {
            getLogger().warning("Cannot load Usermap plugin. Some functionality may not be available.");
        }

        deathLocs = new HashMap<Player, Location>();

        for (World w : getServer().getWorlds()) {
            if (getWorldType(w.getName()) == ECONOMY) {
                w.setSpawnFlags(false,true);
                w.setDifficulty(Difficulty.EASY);
            }
        }

        getLogger().info("Worlds init complete");
    }

    @Override
    public void onDisable() {
        getLogger().info("Worlds disable");
    }

    @EventHandler
    public void spawn(CreatureSpawnEvent event) {
        EntityType type = event.getEntityType();

        if (getWorldType(event.getLocation().getWorld().getName()) == ECONOMY) {
            if (type == EntityType.BLAZE ||
                    type == EntityType.ZOMBIE ||
                    type == EntityType.CAVE_SPIDER ||
                    type == EntityType.CREEPER ||
                    type == EntityType.ENDERMAN ||
                    type == EntityType.GHAST ||
                    type == EntityType.MAGMA_CUBE ||
                    type == EntityType.PIG_ZOMBIE ||
                    type == EntityType.SILVERFISH ||
                    type == EntityType.SKELETON ||
                    type == EntityType.SPIDER ||
                    type == EntityType.WITCH) {
                event.setCancelled(true);
            }
        }
    }

    public int getPlayerWorldType(Player player, boolean useRank) {
        return getPlayerWorldType(player.getName(),useRank);
    }

    public int getPlayerWorldType(Player player) {
        return getPlayerWorldType(player.getName(),true);
    }

    public int getPlayerWorldType(String player) {
        return getPlayerWorldType(player,true);
    }

    public int getPlayerWorldType(String player, boolean useRank) {
        Player p = getServer().getPlayer(player);
        if (rank == null) {
            rank = (Rank)getServer().getPluginManager().getPlugin("Rank");
        }
        boolean rankNormal = false;
        if (rank != null && useRank) {
            String type = rank.getRank(player);
            if (type.equalsIgnoreCase("owner") || type.equalsIgnoreCase("admin")) {
                rankNormal = true;
            }
        }
        if (p != null) {
            World w = p.getLocation().getWorld();
            String name = w.getName();
            if ("vanilla".equalsIgnoreCase(name) && !rankNormal) {
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
            if (event.getBlockPlaced().getType() == Material.ENDER_CHEST || event.getBlockPlaced().getType() == Material.PISTON_BASE
                    || event.getBlockPlaced().getType() == Material.PISTON_STICKY_BASE) {
                p.sendMessage(ChatColor.RED + "You cannot place that block in this world");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void teleport(PlayerTeleportEvent event) {
        Location l1 = event.getFrom();
        Location l2 = event.getTo();
        int type1 = getWorldType(l1.getWorld().getName());
        int type2 = getWorldType(l2.getWorld().getName());
        if (type1 != type2) {
            updatePlayerLocation(event.getPlayer());
            handleMove(event.getPlayer(),false,event.getTo().getWorld());
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

    public boolean handleMove(Player p, boolean force,World to) {
        if (p == null || to == null) return false;
        World currWorld = p.getWorld();
        boolean saved = false;

        String player = p.getName();
        if (userMap != null) {
            player = userMap.getUUID(player);
            if (UserMap.NO_USER.equalsIgnoreCase(player)) return false;
        }

        String cWorld = currWorld.getName();
        if (getWorldType(cWorld) == NORMAL) {
            cWorld = "world";
        }

        if (inventory != null) {
            saved = inventory.saveInventory(p,cWorld + "_" + player);
        }

        if (!saved && !force) {
            p.sendMessage(ChatColor.RED + "Cannot save your inventory. Use /go <world> force to go anyway. Warning: If you do this you may lose your entire inventory. It is recommended that you empty it before attempting to go to another world.");
            return false;
        } else {
            inventory.clearInventory(p);
            try {
                inventory.loadInventory(p,to.getName() + "_" + player);
                p.sendMessage(ChatColor.GREEN + "Your inventory has been saved and will be reloaded when you return to your previous world");
            } catch (Exception e) {
                inventory.loadInventory(p,cWorld + "_" + player);
                p.sendMessage(ChatColor.RED + "Your inventory could not be properly saved.");
            }
        }

        return true;
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
                String origWorld = world;
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

                List<WorldCoords> worldList = (List<WorldCoords>)database.select(WorldCoords.class,"world = \"" + database.makeSafe(world) + "\" AND player = \"" + database.makeSafe(p.getName()) + "\"");

                Location l = w.getSpawnLocation();

                if (worldList.size() > 0) {
                    WorldCoords wc = worldList.get(0);
                    l = new Location(getServer().getWorld(wc.getWorld()),wc.getX(),wc.getY(),wc.getZ());
                }

                p.teleport(l);
            } else {
                sender.sendMessage("/go <world> [force]");
                sender.sendMessage("Worlds: main,vanilla,economy");
            }
        } else {
            return false;
        }

        return true;
    }
}
