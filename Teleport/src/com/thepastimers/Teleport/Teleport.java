package com.thepastimers.Teleport;

import com.thepastimers.CombatLog.CombatLog;
import com.thepastimers.Mute.Mute;
import com.thepastimers.Permission.Permission;
import com.thepastimers.Worlds.Worlds;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: derp
 * Date: 10/2/12
 * Time: 7:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class Teleport extends JavaPlugin {
    Permission permission;
    Mute mute;
    Worlds worlds;
    Map<String,String> requests;
    CombatLog combatLog;

    @Override
    public void onEnable() {
        getLogger().info("Teleport init");

        permission = (Permission)getServer().getPluginManager().getPlugin("Permission");

        if (permission == null) {
            getLogger().warning("Unable to load Permission plugin. Some functionality will not be available.");
        }

        mute = (Mute)getServer().getPluginManager().getPlugin("Mute");

        if (mute == null) {
            getLogger().warning("Unable to load Mute plugin. Some functionality will not be available.");
        }

        combatLog = (CombatLog)getServer().getPluginManager().getPlugin("CombatLog");
        if (combatLog == null) {
            getLogger().warning("Unable to load CombatLog plugin. Some functionality will not be available.");
        }

        worlds = (Worlds)getServer().getPluginManager().getPlugin("Worlds");
        if (worlds == null) {
            getLogger().warning("Unable to load Worlds plugin. Some functionality may not be available.");
        }

        requests = new HashMap<String,String>();

        getLogger().info("Teleport init complete");
    }

    @Override
    public void onDisable() {
        getLogger().info("Teleport disabled");
    }

    private boolean hasRequest(String name, String name2) {
        for (String key : requests.keySet()) {
            if (key.equalsIgnoreCase(name)) {
                if (requests.get(key).equalsIgnoreCase(name2)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void clearRequests(String name) {
        try {
            for (String key : requests.keySet()) {
                if (key.equalsIgnoreCase(name)) {
                    requests.remove(key);
                }
            }
        } catch (Exception e) {
            getLogger().warning("Can't clear requests, possibly concurrency issue.");
        }
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

        if (command.equalsIgnoreCase("tpa")) {
            //getLogger().info(playerName + " attempting teleport");
            if (args.length > 0) {
                if (permission == null || !permission.hasPermission(playerName,"teleport_tpa") || playerName.equalsIgnoreCase("CONSOLE")) {
                    sender.sendMessage(ChatColor.RED + "You do not have permissions to use this command (teleport_tpa)");
                    return true;
                }
                //getLogger().info(playerName + " has permission to teleport");
                String player = args[0];

                if (mute != null && mute.isMutedBy(playerName,player)) {
                    sender.sendMessage(ChatColor.RED + "This player has muted you");
                    return true;
                }

                Player p = getServer().getPlayer(player);

                if (p == null) {
                    sender.sendMessage(ChatColor.RED + "This player is not online");
                    return true;
                }

                if (worlds.getWorldType(p.getWorld().getName()) == Worlds.VANILLA) {
                    sender.sendMessage(ChatColor.RED + "That player is currently in the vanilla world and cannot accept teleports at this time");
                    return true;
                }
                //getLogger().info(playerName + " is teleporting to valid player");

                if (hasRequest(playerName,player)) {
                    getLogger().info(playerName + " has active request");
                    sender.sendMessage("You already have an active teleport request to " + p.getName());
                } else {
                    //getLogger().info(playerName + " adding request....");
                    p.sendMessage(playerName + " has requested to teleport to you. Use /tpaccept "
                            + playerName + " or /tpdeny " + playerName + " to accept or deny this request");
                    requests.put(playerName,p.getName());
                    sender.sendMessage("Requesting teleport to " + p.getName());
                }
            } else {
                sender.sendMessage("/tpa <player>");
            }
        } else if (command.equalsIgnoreCase("tpaccept")) {
            if (permission == null || !permission.hasPermission(playerName,"teleport_tpa") || playerName.equalsIgnoreCase("CONSOLE")) {
                sender.sendMessage(ChatColor.RED + "You do not have permissions to use this command (teleport_tpa)");
                return true;
            }
            if (args.length > 0) {
                String player = args[0];

                if (hasRequest(player,playerName)) {
                    Player p = getServer().getPlayer(player);

                    if (combatLog != null) {
                        int seconds = combatLog.secondsSinceCombat(playerName);
                        if (seconds > -1 && seconds < 10) {
                            sender.sendMessage(ChatColor.RED + "Teleport canceled due to recent combat");
                            sender.sendMessage(ChatColor.RED + "You were recently in combat. You must wait another " + (10-seconds) + " seconds before you can teleport.");
                            return true;
                        }
                    }

                    if (p == null) {
                        sender.sendMessage(player + " is not currently online");
                    } else {
                        Player p2 = (Player)sender;
                        p.teleport(p2);
                        p2.sendMessage("Teleporting " + player + " to you");
                        p.sendMessage("Teleporting you to " + playerName);
                    }
                    clearRequests(player);

                    return true;
                } else {
                    sender.sendMessage(player + " does not have an active request to teleport to you.");
                }
            } else {
                sender.sendMessage("/tpaccept <player>");
            }
        } else if (command.equalsIgnoreCase("tpdeny")) {
            if (permission == null || !permission.hasPermission(playerName,"teleport_tpa") || playerName.equalsIgnoreCase("CONSOLE")) {
                sender.sendMessage(ChatColor.RED + "You do not have permissions to use this command (teleport_tpa)");
                return true;
            }
            if (args.length > 0) {
                String player = args[0];

                if (hasRequest(player,playerName)) {
                    Player p = getServer().getPlayer(player);

                    if (p == null) {
                        sender.sendMessage(player + " is not currently online");
                    } else {
                        p.sendMessage(playerName + " has denied your teleport request");
                        sender.sendMessage("Teleport request denied");
                    }

                    clearRequests(player);

                    return true;
                } else {
                    sender.sendMessage(player + " does not have an active request to teleport to you.");
                }
            } else {
                sender.sendMessage("/tpdeny <player>");
            }
        } else if (command.equals("tp")) {
            if (permission == null || !permission.hasPermission(playerName,"teleport_tp")) {
                sender.sendMessage(ChatColor.RED + "You do not have permissions to use this command (teleport_tp)");
                return true;
            }

            if (args.length == 2) {
                if (!permission.hasPermission(playerName,"teleport_other")) {
                    sender.sendMessage(ChatColor.RED + "You do not have permissions to do this (teleport_other)");
                    return true;
                }
                String player = args[0];
                String player2 = args[1];

                Player p = getServer().getPlayer(player);
                Player p2 = getServer().getPlayer(player2);

                if (p == null) {
                    sender.sendMessage(player + " is not online");
                    return true;
                }

                if (p2 == null) {
                    sender.sendMessage(player2 + " is not online");
                    return true;
                }

                sender.sendMessage("Teleporting " + player + " to " + player2);

                p.teleport(p2);

            } else if (args.length == 1) {
                if (playerName.equalsIgnoreCase("CONSOLE")) {
                    getLogger().info("You do not have permissions to do this");
                    return true;
                }
                String player = args[0];

                Player p = getServer().getPlayer(player);

                if (p == null) {
                    sender.sendMessage(player + " is not online");
                } else {
                    sender.sendMessage("Teleporting you to " + player);

                    Player p2 = (Player)sender;

                    p2.teleport(p);
                }
            } else {
                sender.sendMessage("/tp <player 1> <player 2>");
            }
        } else {
            return false;
        }

        return true;
    }
}
