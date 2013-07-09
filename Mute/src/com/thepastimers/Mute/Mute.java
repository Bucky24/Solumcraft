package com.thepastimers.Mute;

import com.thepastimers.Database.Database;
import com.thepastimers.Permission.Permission;
import com.thepastimers.Rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: derp
 * Date: 10/2/12
 * Time: 8:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class Mute extends JavaPlugin {
    Database database;
    Rank rank;
    Permission permission;

    @Override
    public void onEnable() {
        getLogger().info("Mute init");

        database = (Database)getServer().getPluginManager().getPlugin("Database");

        if (database == null) {
            getLogger().warning("Unable to load Database plugin. Some functionality may be unavailable.");
        }

        rank = (Rank)getServer().getPluginManager().getPlugin("Rank");

        if (rank == null) {
            getLogger().warning("Unable to load Rank plugin. Some functionality may be unavailable.");
        }

        permission = (Permission)getServer().getPluginManager().getPlugin("Permission");

        if (permission == null) {
            getLogger().warning("Unable to load Permission plugin. Some functionality may be unavailable.");
        }

        getLogger().info("Mute init complete");
    }

    @Override
    public void onDisable() {
        getLogger().info("Mute disable");
    }

    public boolean isMuted(String player) {
        if (database == null) {
            return false;
        }

        List<Muted> list = (List<Muted>)database.select(Muted.class,"muted = '"
                + database.makeSafe(player) + "' and mutee = 'all'");

        return (list.size() != 0);
    }

    public boolean isMutedBy(String muted, String mutee) {
        if (database == null) {
            return false;
        }

        List<Muted> list = (List<Muted>)database.select(Muted.class,"muted = '"
                + database.makeSafe(muted) + "' and mutee = '" + database.makeSafe(mutee) + "'");

        return (list.size() != 0);
    }

    public boolean mute(String muted, String mutee) {
        if (database == null) {
            return false;
        }

        if (isMutedBy(muted,mutee)) {
            return true;
        }

        Muted m = new Muted();
        m.setMuted(muted);
        m.setMutee(mutee);

        return m.save(database);
    }

    public boolean unmute(String muted, String mutee) {
        if (database == null) {
            return false;
        }

        List<Muted> list = (List<Muted>)database.select(Muted.class,"muted = '"
                + database.makeSafe(muted) + "' and mutee = '" + database.makeSafe(mutee) + "'");

        if (list.size() == 0) {
            return true;
        }

        return list.get(0).delete(database);
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

        if (command.equals("mute")) {
            if (permission == null || !permission.hasPermission(playerName,"mute_mute")) {
                sender.sendMessage(ChatColor.RED + "You do not have permissions to use this command (mute_mute)");
                return true;
            }

            if (args.length > 0) {
                String player = args[0];

                if (!mute(player, playerName)) {
                    sender.sendMessage("Unable to mute player");
                } else {
                    sender.sendMessage("Player muted");
                }

                return true;
            } else {
                sender.sendMessage("/mute <player>");
            }
        } else if (command.equals("unmute")) {
            if (permission == null || !permission.hasPermission(playerName,"mute_mute")) {
                sender.sendMessage(ChatColor.RED + "You do not have permissions to use this command (mute_mute)");
                return true;
            }

            if (args.length > 0) {
                String player = args[0];

                if (!unmute(player,playerName)) {
                    sender.sendMessage("Unable to unmute player");
                } else {
                    sender.sendMessage("Player unmuted");
                }

                return true;
            } else {
                sender.sendMessage("/unmute <player>");
            }
        } else if (command.equals("globalmute")) {
            if (permission == null || !permission.hasPermission(playerName,"mute_globalmute")) {
                sender.sendMessage(ChatColor.RED + "You do not have permissions to use this command (mute_globalmute)");
                return true;
            }

            if (args.length > 0) {
                String player = args[0];

                Player p = getServer().getPlayer(player);

                if (!mute("all",playerName)) {
                    sender.sendMessage("Unable to mute player");
                } else {
                    sender.sendMessage("Player muted");
                    if (p != null) {
                        p.sendMessage(ChatColor.RED + "You have been muted.");
                    }
                }

                return true;
            } else {
                sender.sendMessage("/globalmute <player>");
            }
        } else if (command.equals("globalunmute")) {
            if (permission == null || !permission.hasPermission(playerName,"mute_globalmute")) {
                sender.sendMessage(ChatColor.RED + "You do not have permissions to use this command (mute_globalmute)");
                return true;
            }

            if (args.length > 0) {
                String player = args[0];

                Player p = getServer().getPlayer(player);

                if (!unmute("all",playerName)) {
                    sender.sendMessage("Unable to unmute player");
                } else {
                    sender.sendMessage("Player unmuted");
                    if (p != null) {
                        p.sendMessage(ChatColor.RED + "You have been ummuted.");
                    }
                }

                return true;
            } else {
                sender.sendMessage("/unmute <player>");
            }
        } else {
            return false;
        }

        return true;
    }
}
