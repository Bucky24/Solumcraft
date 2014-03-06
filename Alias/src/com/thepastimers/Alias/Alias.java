package com.thepastimers.Alias;

import com.thepastimers.Permission.Permission;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by rwijtman on 3/5/14.
 */
public class Alias extends JavaPlugin implements Listener {
    Permission permission;

    Map<String,String> aliasMap;

    @Override
    public void onEnable() {
        getLogger().info("Alias init");

        getServer().getPluginManager().registerEvents(this,this);

        permission = (Permission)getServer().getPluginManager().getPlugin("Permission");
        if (permission == null) {
            getLogger().warning("Unable to load Permission plugin. Some functionality may not be available");
        }

        aliasMap = new HashMap<String,String>();

        getLogger().info("Alias init complete");
    }

    @Override
    public void onDisable() {
        getLogger().info("Chat disable");
    }

    public String getAlias(String player) {
        if (aliasMap.containsKey(player)) {
            return aliasMap.get(player);
        }
        return player;
    }

    public void setAlias(String player, String alias) {
        aliasMap.put(player,alias);
    }

    public void removeAlias(String player) {
        aliasMap.remove(player);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        String playerName = "";

        if (sender instanceof Player) {
            playerName = ((Player)sender).getName();
        } else {
            playerName = "CONSOLE";
        }

        String command = cmd.getName();

        if ("alias".equalsIgnoreCase(command)) {
            if (permission == null || !permission.hasPermission(playerName,"alias_command") || playerName.equalsIgnoreCase("CONSOLE")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this command (alias_command)");
                return true;
            }

            if (args.length > 0) {
                String player = args[0];
                String alias = "";
                if (args.length > 1) {
                    alias = args[1];
                }

                if ("".equalsIgnoreCase(alias)) {
                    removeAlias(player);
                    sender.sendMessage(ChatColor.GREEN + "Player " + player + "'s alias has been removed (if there ever was one)");
                } else {
                    setAlias(player,alias);
                    sender.sendMessage(ChatColor.GREEN + "Player " + player + " will now be known as " + alias);
                }
            } else {
                sender.sendMessage("/alias <player> <alias, leave blank to remove alias>");
            }
        } else {
            return false;
        }

        return true;
    }
}
