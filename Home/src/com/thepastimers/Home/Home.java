package com.thepastimers.Home;

import com.thepastimers.Database.Database;
import com.thepastimers.Permission.Permission;
import com.thepastimers.Rank.Rank;
import com.thepastimers.UserMap.UserMap;
//import com.thepastimers.Worlds.Worlds;
import org.bukkit.ChatColor;
import org.bukkit.Location;
//import org.bukkit.Material;
import org.bukkit.Text;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
//import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: derp
 * Date: 10/2/12
 * Time: 9:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class Home extends JavaPlugin implements Listener {
    Database database;
    Permission permission;
    Rank rank;
    //Worlds worlds;
    UserMap userMap;

    Map<HomeData,Date> lastHome;

    @Override
    public void onEnable() {
        getLogger().info("Home init");

        getServer().getPluginManager().registerEvents(this,this);

        database = (Database)getServer().getPluginManager().getPlugin("Database");
        if (database == null) {
            getLogger().warning("Cannot load Database plugin. Some functionality may not be available");
        } else {
            HomeData.createTables(database,getLogger());
            MaxHome.createTables(database,getLogger());
        }

        permission = (Permission)getServer().getPluginManager().getPlugin("Permission");
        if (permission == null) {
            getLogger().warning("Cannot load Permission plugin. Some functionality may not be available");
        }

        rank = (Rank)getServer().getPluginManager().getPlugin("Rank");
        if (rank == null) {
            getLogger().warning("Cannot load Rank plugin. Some functionality may not be available");
        }

        userMap = (UserMap)getServer().getPluginManager().getPlugin("UserMap");
        if (userMap == null) {
            getLogger().warning("Cannot load UserMap plugin. Some functionality may not be available");
        }

        /*worlds = (Worlds)getServer().getPluginManager().getPlugin("Worlds");
        if (worlds == null) {
            getLogger().warning("Unable to load Worlds plugin. Some functionality may not be available.");
        }*/

        lastHome = new HashMap<HomeData,Date>();

        getLogger().info("Table info: ");
        getLogger().info(HomeData.getTableInfo());
        getLogger().info(MaxHome.getTableInfo());

        getLogger().info("Home init complete");
    }

    @Override
    public void onDisable() {
        getLogger().info("Home disabled");
    }

    /*@EventHandler
    public void playerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player p = (Player)event.getEntity();

            if (event.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION) {
                Date now = new Date();
                // check if player has teleported recently.
                for (HomeData hd : lastHome.keySet()) {
                    if (hd.getPlayer().equalsIgnoreCase(p.getName())) {
                        Date d = lastHome.get(hd);

                        if (now.getTime() - d.getTime() <= 1000*5) {
                            // re-teleport
                            event.setCancelled(true);
                            p.sendMessage("Block damage detected within 5 seconds of a teleport. Re-teleporting.");

                            Location l = new Location(getServer().getWorld(hd.getWorld()),hd.getX(),hd.getY(),hd.getZ());

                            p.teleport(l);
                        }
                    }
                }
            }
        }
    }*/

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (database == null) return;
        Player p = event.getPlayer();
        String uuid = p.getUniqueId().toString();
        getLogger().info("Updating UUID for " + p.getName());
        String query = "UPDATE " + HomeData.table + " SET player = \"" + database.makeSafe(uuid) + "\" WHERE player = \"" + p.getName() + "\"";
        database.query(query);
    }

    public int getMaxHomes(String player) {
        return getMaxHomes(player,false);
    }

    public int getMaxHomes(String player, boolean groupOnly) {
        if (database == null) {
            return 0;
        }

        if (userMap != null) {
            player = userMap.getUUID(player);
            if (UserMap.NO_USER.equalsIgnoreCase(player)) return 0;
        }

        int playerMax = 0;
        String group = "";
        List<MaxHome> list;

        if (!groupOnly) {
            list = (List<MaxHome>)database.select(MaxHome.class,"name = '"
                    + database.makeSafe(player) + "' AND `group` = false");

            if (list.size() > 0) {
                playerMax = list.get(0).getMax();
                list.clear();
                list = null;
            }

            if (rank != null) {
                group = rank.getRank(player);
            }

            if (group.equalsIgnoreCase("")) {
                group = "all";
            }

        } else {
            group = player;
        }

        list = (List<MaxHome>)database.select(MaxHome.class,"name = '"
                + database.makeSafe(group) + "' AND `group` = true");

        int groupMax = 0;

        if (list.size() > 0) {
            groupMax = list.get(0).getMax();
        }

        list.clear();
        list = null;

        return Math.max(playerMax, groupMax);
    }

    public HomeData getHome(String player, String name) {
        if (database == null) {
            return null;
        }

        if (userMap != null) {
            player = userMap.getUUID(player);
            if (UserMap.NO_USER.equalsIgnoreCase(player)) return null;
        }

        List<HomeData> data = (List<HomeData>)database.select(HomeData.class,"player = '"
                + database.makeSafe(player) + "' AND name = '" + database.makeSafe(name) + "'");

        if (data.size() == 0) {
            return null;
        }

        HomeData hd = data.get(0);

        data.clear();
        data = null;

        return hd;
    }

    public List<HomeData> getHomes(String player) {
        List<HomeData> ret = new ArrayList<HomeData>();
        if (database == null) {
            return ret;
        }

        if (userMap != null) {
            player = userMap.getUUID(player);
            if (UserMap.NO_USER.equalsIgnoreCase(player)) return null;
        }

        List<HomeData> data = (List<HomeData>)database.select(HomeData.class,"player = '"
                + database.makeSafe(player) + "'");

        for (HomeData hd : data) {
            ret.add(hd);
        }

        data.clear();
        data = null;

        return ret;
    }

    public int getHomeCount(String player) {
        List<String> ret = new ArrayList<String>();
        if (database == null) {
            return 0;
        }

        if (userMap != null) {
            player = userMap.getUUID(player);
            if (UserMap.NO_USER.equalsIgnoreCase(player)) return 0;
        }

        List<HomeData> data = (List<HomeData>)database.select(HomeData.class,"player = '"
                + database.makeSafe(player) + "'");

        int count = data.size();

        data.clear();
        data = null;

        return count;
    }

    public boolean addHome(String player, String name, String world, double x, double y, double z) {
        if (database == null) {
            return false;
        }

        if (getHomeCount(player) >= getMaxHomes(player)) {
            return false;
        }

        if (userMap != null) {
            player = userMap.getUUID(player);
            if (UserMap.NO_USER.equalsIgnoreCase(player)) return false;
        }

        HomeData hd = new HomeData();
        hd.setPlayer(player);
        hd.setName(name);
        hd.setWorld(world);
        hd.setX(x);
        hd.setY(y);
        hd.setZ(z);

        return hd.save(database);
    }

    public HomeData createHome(String player, String name, String world, double x, double y, double z) {
        if (getHomeCount(player) >= getMaxHomes(player)) {
            return null;
        }

        if (userMap != null) {
            player = userMap.getUUID(player);
            if (UserMap.NO_USER.equalsIgnoreCase(player)) return null;
        }

        HomeData hd = new HomeData();
        hd.setPlayer(player);
        hd.setName(name);
        hd.setWorld(world);
        hd.setX(x);
        hd.setY(y);
        hd.setZ(z);

        return hd;
    }

    public boolean deleteHome(String player, String name) {
        if (database == null) {
            return false;
        }

        if (userMap != null) {
            player = userMap.getUUID(player);
            if (UserMap.NO_USER.equalsIgnoreCase(player)) return false;
        }

        HomeData hd = getHome(player,name);

        if (hd == null) {
            return false;
        }

        return hd.delete(database);
    }

    public boolean setMax(String name, int max, boolean group) {
        if (database == null) {
            return false;
        }

        if (userMap != null) {
            name = userMap.getUUID(name);
            if (UserMap.NO_USER.equalsIgnoreCase(name)) return false;
        }

        List<MaxHome> data = (List<MaxHome>)database.select(MaxHome.class,"name = '"
                + database.makeSafe(name) + "' AND `group` = " + group);

        MaxHome mh;

        if (data.size() > 0) {
            mh = data.get(0);
            mh.setMax(max);
        } else {
            mh = new MaxHome();
            mh.setName(name);
            mh.setMax(max);
            mh.setGroup(group);
        }

        return mh.save(database);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        String playerName = "";

        String uuid = "";
        if (sender instanceof Player) {
            Player p = (Player)sender;
            playerName = p.getName();
            uuid = p.getUniqueId().toString();
        } else {
            playerName = "CONSOLE";
            uuid = playerName;
        }
        if ("".equalsIgnoreCase(uuid)) {
            sender.sendMessage(ChatColor.RED + "Could not get a proper UUID for you, aborting command.");
        }

        String command = cmd.getName();

        if (command.equalsIgnoreCase("sethome")) {
            if (permission == null || !permission.hasPermission(playerName,"home_home") || playerName.equalsIgnoreCase("CONSOLE")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this command (home_home)");
                return true;
            }
            Player p = getServer().getPlayer(playerName);
            double x = p.getLocation().getX();
            double y = p.getLocation().getY();
            double z = p.getLocation().getZ();
            String world = p.getLocation().getWorld().getName();
            String name = "";

            if (args.length >= 1) {
                name = args[0];
            }

            HomeData hd = getHome(playerName,name);

            if (hd == null) {
                if (getHomeCount(playerName) >= getMaxHomes(playerName)) {
                    sender.sendMessage(ChatColor.RED + "You are at the maximum number of homes you can set");
                    return true;
                }

                hd = createHome(playerName,name,world,x,y,z);
            }

            hd.setX(x);
            hd.setY(y);
            hd.setZ(z);
            hd.setWorld(world);

            if (hd != null && hd.save(database)) {
                sender.sendMessage(Text.make().color(ChatColor.GREEN).text("Home set. You can set " + (getMaxHomes(uuid)-getHomeCount(uuid))
                        + " more home/s"));
            } else {
                sender.sendMessage(ChatColor.RED + "Unable to set home");
            }
        } else if (command.equalsIgnoreCase("home")) {
            if (permission == null || !permission.hasPermission(playerName,"home_home") || playerName.equalsIgnoreCase("CONSOLE")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this command (home_home)");
                return true;
            }

            String name = "";

            if (args.length >= 1) {
                name = args[0];
            }

            HomeData hd = getHome(playerName,name);

            if (hd == null) {
                if (!"".equalsIgnoreCase(name)) {
                    sender.sendMessage(ChatColor.RED + "You have no home named " + name);
                } else {
                    sender.sendMessage(ChatColor.RED + "You have no default home set");
                }
                return true;
            }

            Player p = getServer().getPlayer(playerName);
            try {
                World w = getServer().getWorld(hd.getWorld());
                Location l = new Location(getServer().getWorld(hd.getWorld()),hd.getX(),hd.getY(),hd.getZ());
                Location l2 = new Location(getServer().getWorld(hd.getWorld()),hd.getX(),hd.getY()+1,hd.getZ());

                // check if location is safe.
                /*Block b = w.getBlockAt(l);
                Block b2 = w.getBlockAt(l2);

                if ((b.getType() != Material.AIR && b.getType() != Material.WATER && b.getType() != Material.STATIONARY_WATER && b.getType() != Material.CARPET) ||
                        (b2.getType() != Material.AIR && b2.getType() != Material.WATER && b2.getType() != Material.STATIONARY_WATER && b2.getType() != Material.CARPET)) {
                    sender.sendMessage(ChatColor.RED + "That location is unsafe to teleport to. Please use /forcehome " + name + " If you really want to teleport there.");
                    return true;
                }*/

                p.teleport(l);
                lastHome.put(hd,new Date());
                if (!"".equalsIgnoreCase(hd.getName())) {
                    sender.sendMessage(ChatColor.GREEN + "Teleporting you to " + hd.getName());
                } else {
                    sender.sendMessage(ChatColor.GREEN + "Teleporting you to default home");
                }
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Unable to teleport you: " + e.getMessage());
            }
        } else if (command.equalsIgnoreCase("forcehome")) {
            if (permission == null || !permission.hasPermission(playerName,"home_home") || playerName.equalsIgnoreCase("CONSOLE")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this command (home_home)");
                return true;
            }

            String name = "";

            if (args.length >= 1) {
                name = args[0];
            }

            HomeData hd = getHome(playerName,name);

            if (hd == null) {
                if (!"".equalsIgnoreCase(name)) {
                    sender.sendMessage("You have no home named " + name);
                } else {
                    sender.sendMessage("You have no default home set");
                }
                return true;
            }

            Player p = getServer().getPlayer(playerName);
            try {
                World w = getServer().getWorld(hd.getWorld());
                Location l = new Location(getServer().getWorld(hd.getWorld()),hd.getX(),hd.getY(),hd.getZ());

                p.teleport(l);
                lastHome.put(hd,new Date());
                if (!"".equalsIgnoreCase(hd.getName())) {
                    sender.sendMessage("Teleporting you to " + hd.getName());
                } else {
                    sender.sendMessage("Teleporting you to default home");
                }
            } catch (Exception e) {
                sender.sendMessage("Unable to teleport you: " + e.getMessage());
            }
        } else if (command.equalsIgnoreCase("delhome")) {
            if (permission == null || !permission.hasPermission(playerName,"home_home") || playerName.equalsIgnoreCase("CONSOLE")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this command (home_home)");
                return true;
            }

            String name = "";

            if (args.length >= 1) {
                name = args[0];
            }

            if (deleteHome(playerName,name)) {
                sender.sendMessage("Home removed. You can now set " + (getMaxHomes(playerName)-getHomeCount(playerName)) + " homes");
            } else {
                sender.sendMessage("Unable to remove home");
            }

        } else if (command.equalsIgnoreCase("listhome")) {
            String player;
            if (args.length >= 1) {
                if (permission == null || !permission.hasPermission(playerName,"home_listhomeother")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command (home_listhomeother)");
                    return true;
                }

                player = args[0];
            } else {
                if (permission == null || !permission.hasPermission(playerName,"home_listhome") || playerName.equalsIgnoreCase("CONSOLE")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command (home_listhome)");
                    return true;
                }

                player = playerName;
            }

            List<HomeData> homes = getHomes(player);

            sender.sendMessage("Home list (" + getHomeCount(player) + "/" + getMaxHomes(player) + "):");

            for (HomeData h : homes) {
                sender.sendMessage(h.getName() + " (" + (int)h.getX() + "," + (int)h.getY() + "," + (int)h.getZ() + "), in " + h.getWorld());
            }
        } else if (command.equalsIgnoreCase("maxhome")) {
            if (permission == null || !permission.hasPermission(playerName,"home_maxhome")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this command (home_maxhome)");
                return true;
            }

            if (args.length == 1) {
                String player = args[0];
                int max = getMaxHomes(player);

                sender.sendMessage("Max homes for " + player + ": " + max);
            } else if (args.length > 1) {
                String player = args[0];
                int max;
                try {
                    max = Integer.parseInt(args[1]);
                } catch (Exception e) {
                    sender.sendMessage("Bad parameters");
                    return true;
                }

                if (setMax(player,max,false)) {
                    sender.sendMessage("Player max homes set");
                } else {
                    sender.sendMessage("Unable to set max homes");
                }
            } else {
                sender.sendMessage("/maxhome <player> <max - optional>");
            }
        } else if (command.equalsIgnoreCase("groupmaxhome")) {
            if (permission == null || !permission.hasPermission(playerName,"home_maxhome")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this command (home_maxhome)");
                return true;
            }

            if (args.length == 1) {
                String player = args[0];
                int max = getMaxHomes(player,true);

                sender.sendMessage("Max homes for " + player + ": " + max);
            } else if (args.length > 1) {
                String player = args[0];
                int max;
                try {
                    max = Integer.parseInt(args[1]);
                } catch (Exception e) {
                    sender.sendMessage("Bad parameters");
                    return true;
                }

                if (setMax(player,max,true)) {
                    sender.sendMessage("Group max homes set");
                } else {
                    sender.sendMessage("Unable to set max homes");
                }
            } else {
                sender.sendMessage("/groupmaxhome <group> <max - optional>");
            }
        } else {
            return false;
        }

        return true;
    }
}
