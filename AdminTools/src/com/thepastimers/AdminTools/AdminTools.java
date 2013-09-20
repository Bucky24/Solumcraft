package com.thepastimers.AdminTools;

import com.thepastimers.Database.Database;
import com.thepastimers.Logger.Logger;
import com.thepastimers.Permission.Permission;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Timestamp;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 4/22/13
 * Time: 6:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class AdminTools extends JavaPlugin implements Listener {
    Database database;
    Permission permission;
    Logger logger;

    @Override
    public void onEnable() {
        getLogger().info("AdminTools init");

        getServer().getPluginManager().registerEvents(this,this);

        database = (Database)getServer().getPluginManager().getPlugin("Database");

        if (database == null) {
            getLogger().warning("Warning, unable to load Database plugin. Critical error.");
        }

        permission = (Permission)getServer().getPluginManager().getPlugin("Permission");
        if (permission == null) {
            getLogger().warning("Unable to connect to Permission plugin");
        }

        logger = (Logger)getServer().getPluginManager().getPlugin("Logger");
        if (logger == null) {
            getLogger().warning("Unable to connect to Logger plugin");
        }

        getLogger().info("Table info: ");
        getLogger().info(BanData.getTableInfo());

        getLogger().info("AdminTools init complete");
    }

    @Override
    public void onDisable() {
        getLogger().info("AdminTools disable");
    }

    public boolean isPlayerBanned(String player) {
        if (database == null) {
            return false;
        }

        List<BanData> banList = (List<BanData>)database.select(BanData.class,"player = '" + database.makeSafe(player) + "' and active = true");

        Date nowDate = new Date();
        Timestamp now = new Timestamp(nowDate.getTime());

        for (BanData b : banList) {
            if (b.isPerm() || b.getUntil().after(now)) {
                return true;
            }
        }

        return false;
    }

    public long getTimeLeft(String player) {
        if (!isPlayerBanned(player)) return 0;
        if (database == null) return 0;

        List<BanData> banList = (List<BanData>)database.select(BanData.class,"player = '" + database.makeSafe(player) + "' and active = true");

        Date nowDate = new Date();
        Timestamp now = new Timestamp(nowDate.getTime());

        for (BanData b : banList) {
            if (b.isPerm()) {
                return -1;
            }
            if (b.getUntil().after(now)) {
                return b.getUntil().getTime() - now.getTime();
            }
        }

        return 0;
    }

    public String banReason(String player) {
        if (!isPlayerBanned(player)) return "";
        if (database == null) return "";

        List<BanData> banList = (List<BanData>)database.select(BanData.class,"player = '" + database.makeSafe(player) + "' and active = true");

        Date nowDate = new Date();
        Timestamp now = new Timestamp(nowDate.getTime());

        for (BanData b : banList) {
            if (b.isPerm() || b.getUntil().after(now)) {
                return b.getReason();
            }
        }

        return "";
    }

    public BanData banObject(String player) {
        if (!isPlayerBanned(player)) return null;
        if (database == null) return null;

        List<BanData> banList = (List<BanData>)database.select(BanData.class,"player = '" + database.makeSafe(player) + "' and active = true");

        Date nowDate = new Date();
        Timestamp now = new Timestamp(nowDate.getTime());

        for (BanData b : banList) {
            if (b.isPerm() || b.getUntil().after(now)) {
                return b;
            }
        }

        return null;
    }

    public boolean unban(String player) {
        if (!isPlayerBanned(player)) return true;
        if (database == null) return false;

        List<BanData> banList = (List<BanData>)database.select(BanData.class,"player = '" + database.makeSafe(player) + "' and active = true");

        Date nowDate = new Date();
        Timestamp now = new Timestamp(nowDate.getTime());

        for (BanData b : banList) {
            if (b.isPerm() || b.getUntil().after(now)) {
                b.setActive(false);
                if (!b.save(database)) {
                    return false;
                }
            }
        }

        return true;
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Player p = event.getPlayer();

        if (isPlayerBanned(p.getName())) {
            long time = getTimeLeft(p.getName());
            long niceTime = getTimeLeft(p.getName())/1000;
            String reason = banReason(p.getName());

            if (time < 0) {
                event.disallow(PlayerLoginEvent.Result.KICK_BANNED,"You have been perm-banned. Reason: " + reason);
            } else {
                event.disallow(PlayerLoginEvent.Result.KICK_BANNED,"You are banned for the next " + niceTime + " seconds. Reason: " + reason);
            }

            getLogger().info("Player " + p.getName() + " failed login because of ban. Reason: " + reason);
        }
    }

    public int ping(Player p) {
        if (p == null) return -1;
        CraftPlayer cp = (CraftPlayer)p;

        int ping = cp.getHandle().ping;

        return ping;
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

        if (command.equalsIgnoreCase("ban")) {
            if (permission == null || !permission.hasPermission(playerName,"admin_ban")) {
                sender.sendMessage("You don't have permission to use this command (admin_ban)");
                return true;
            }
            if (args.length > 1) {
                String name = args[0];
                String reason = "";
                Timestamp until = null;
                int offset = -1;

                int reasonOffset = 1;

                try {
                    offset = Integer.parseInt(args[1]);
                    if (offset > 24*60*60) {
                        sender.sendMessage("You may not temp-ban someone for more then 24 hours.");
                        return true;
                    }
                    until = new Timestamp((new Date()).getTime() + (offset*1000));
                    reasonOffset = 2;
                } catch (NumberFormatException e) {
                    if (!permission.hasPermission(playerName,"admin_permban")) {
                        sender.sendMessage("You don't have permission to perm-ban (admin_permban)");
                        return true;
                    }
                    // ignore, assume it's part of the reason for perm ban
                }

                for (int i=reasonOffset;i<args.length;i++) {
                    reason += args[i] + " ";
                }

                if ("".equalsIgnoreCase(reason)) {
                    sender.sendMessage("You must specify a reason when banning a player");
                    return true;
                }

                if (isPlayerBanned(name)) {
                    long time = getTimeLeft(name);
                    if (time == -1 && until != null) {
                        sender.sendMessage("That player is currently perm-banned. Please pardon this player before setting a temp ban.");
                        return true;
                    } else if (time != -1 && until == null) {
                        sender.sendMessage("You are perm-banning a player who is currently banned for " + time + " seconds");
                    }
                }

                BanData bd = new BanData();
                bd.setEntered(new Timestamp((new Date()).getTime()));
                bd.setPlayer(name);
                bd.setReason(reason);
                bd.setUntil(until);

                if (until == null) {
                    bd.setPerm(true);
                    if (logger != null) {
                        logger.writeEvent(Logger.file,null,name,"perm_ban","By: " + playerName + ". Reason: " + reason);
                    }
                    sender.sendMessage("You have perm-banned " + name + " for reason: " + reason);
                } else {
                    bd.setPerm(false);
                    if (logger != null) {
                        logger.writeEvent(Logger.file,null,name,"ban","By: " + playerName + ". Reason: " + reason + ". Length: " + until);
                    }
                    sender.sendMessage("You have banned " + name + " for " + offset + " seconds. Reason: " + reason);
                }

                bd.save(database);
                Player p = getServer().getPlayer(name);
                if (p != null) {
                    if (until == null) {
                        p.kickPlayer("You have been perm-banned: " + reason);
                    } else {
                        p.kickPlayer("You have banned for " + offset + " seconds. Reason: " + reason);
                    }
                }
            } else {
                sender.sendMessage("/ban <player> <time in seconds> <reason>");
                sender.sendMessage("/ban <player> <reason>");
            }
        } else if ("pardon".equalsIgnoreCase(command)) {
            if (permission == null || !permission.hasPermission(playerName,"admin_ban")) {
                sender.sendMessage("You don't have permission to use this command (admin_ban)");
                return true;
            }
            if (args.length > 0) {
                String player = args[0];

                if (logger != null) {
                    logger.writeEvent(Logger.file,null,player,"pardoned","By: " + playerName);
                }

                if (unban(player)) {
                    sender.sendMessage(player + " has been pardoned of all their crimes.");
                } else {
                    sender.sendMessage("Unable to unban player.");
                }
            } else {
                sender.sendMessage("/pardon <player>");
            }
        } else if ("kick".equalsIgnoreCase(command)) {
            if (permission == null || !permission.hasPermission(playerName,"admin_kick")) {
                sender.sendMessage("You don't have permission to use this command (admin_kick)");
                return true;
            }

            if (args.length > 0) {
                String player = args[0];
                String reason = "";

                Player p = getServer().getPlayer(player);
                if (p == null) {
                    sender.sendMessage("That player is not online");
                    return true;
                }

                for (int i=1;i<args.length;i++) {
                    reason += args[i] + " ";
                }

                if (logger != null) {
                    logger.writeEvent(p,"kicked","By: " + playerName + ". Reason: " + reason);
                }

                if (reason.equalsIgnoreCase("")) {
                    reason = "You have been kicked. ";
                } else {
                    reason = "You have been kicked. Reason: " + reason;
                }

                p.kickPlayer(reason);
            } else {
                sender.sendMessage("/kick <player> <reason>");
            }
        } else if ("entities".equalsIgnoreCase(command)) {
            if (permission == null || !permission.hasPermission(playerName,"admin_entities")) {
                sender.sendMessage("You don't have permission to use this command (admin_entities)");
                return true;
            }

            if (args.length > 0) {
                String subCommand = args[0];
                if ("report".equalsIgnoreCase(subCommand)) {
                    List<World> worlds = getServer().getWorlds();
                    for (World w : worlds) {
                        sender.sendMessage("World " + w.getName());

                        List<Entity> entities = w.getEntities();

                        Map<String,Integer> entityCount = new HashMap<String,Integer>();

                        for (Entity entity : entities) {
                            int count = 0;
                            if (entityCount.containsKey(entity.getType().getName())) {
                                count = entityCount.get(entity.getType().getName());
                            }
                            count ++;
                            entityCount.put(entity.getType().getName(),count);
                        }

                        for (String key : entityCount.keySet()) {
                            sender.sendMessage(key + ": " + entityCount.get(key));
                        }
                    }
                }
            } else {
                sender.sendMessage("/entities <report>");
            }
        } else if ("ping".equalsIgnoreCase(command)) {
            if (permission == null || !permission.hasPermission(playerName,"admin_ping")) {
                sender.sendMessage("You don't have permission to use this command (admin_ping)");
                return true;
            }

            String player = "";
            if (args.length > 0) {
                player = args[0];
            }

            if (!"".equalsIgnoreCase(player)) {
                Player p = getServer().getPlayer(player);
                if (p == null) {
                    sender.sendMessage(ChatColor.RED + "That player does not exist");
                    return true;
                }

                int ping = ping(p);

                sender.sendMessage(ChatColor.GREEN + "Ping for " + player + ": " + ping);
            } else {
                Player[] players = getServer().getOnlinePlayers();
                for (Player p : players) {
                    int ping = ping(p);

                    sender.sendMessage(ChatColor.GREEN + "Ping for " + p.getName() + ": " + ping);
                }
            }
        } else {
            return false;
        }

        return true;
    }
}
