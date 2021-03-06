package com.thepastimers.Permission;

import com.thepastimers.Database.Database;
import com.thepastimers.Database.Table;
import com.thepastimers.Rank.Rank;
import com.thepastimers.UserMap.UserMap;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: derp
 * Date: 10/1/12
 * Time: 7:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class Permission extends JavaPlugin {
    Database database;
    Rank rank;
    UserMap userMap;
    Map<String,Integer> permMap = null;

    @Override
    public void onEnable() {
        getLogger().info("Permission init");

        database = (Database)getServer().getPluginManager().getPlugin("Database");
        if (database == null) {
            getLogger().warning("Unable to load Database plugin. Some functionality w ill not be available.");
        } else {
            PlayerPerm.createTables(database,getLogger());
            GroupPerm.createTables(database,getLogger());
        }

        rank = (Rank)getServer().getPluginManager().getPlugin("Rank");
        if (rank == null) {
            getLogger().warning("Unable to load Rank plugin. Some functionality will not be available.");
        }

        userMap = (UserMap)getServer().getPluginManager().getPlugin("UserMap");
        if (userMap == null) {
            getLogger().warning("Unable to load UserMap plugin. Some functionality will not be available.");
        }

        if (permMap == null) permMap = new HashMap<String, Integer>();

        getLogger().info("Registering permissions");
        this.registerPermission("perms_set",2);
        this.registerPermission("perms_remove",2);
        this.registerPermission("groupperms_set",2);
        this.registerPermission("groupperms_set",2);
        this.registerPermission("perms_level1",1);
        this.registerPermission("perms_level2",1);
        this.registerPermission("perms_level3",1);
        this.registerPermission("perms_level4",1);
        this.registerPermission("perms_level5",1);
        this.registerPermission("perms_level6",1);
        this.registerPermission("perms_level7",1);
        this.registerPermission("perms_level8",1);
        this.registerPermission("perms_level9",1);
        this.registerPermission("perms_level10",1);
        this.registerPermission("perms_level11",1);
        this.registerPermission("perms_level12",1);
        this.registerPermission("perms_level13",1);
        this.registerPermission("perms_level14",1);
        this.registerPermission("perms_level15",1);
        this.registerPermission("perms_level16",1);

        PlayerPerm.refreshCache(database, getLogger());
        //getLogger().info(PlayerPerm.getTableInfo());
        GroupPerm.refreshCache(database, getLogger());
        //getLogger().info(GroupPerm.getTableInfo());

        getLogger().info("Permission init complete");
    }

    @Override
    public void onDisable() {
        getLogger().info("Permission disabled");
    }

    public void registerPermission(String perm, int level) {
        if (permMap == null) permMap = new HashMap<String, Integer>();
        getLogger().info("Adding perm " + perm  + " at level " + level);
        permMap.put(perm,level);
    }

    public boolean hasPermission(String player, String permission) {
        if (player == null || permission == null) {
            return false;
        }

        if (player.equalsIgnoreCase("CONSOLE")) {
            return true;
        }

        player = userMap.getUUID(player);
        if (UserMap.NO_USER.equalsIgnoreCase(player)) {
            return false;
        }

        if (PlayerPerm.hasPermission(player,permission)) {
            return true;
        }

        List<String> playerRanks = new ArrayList<String>();

        if (rank != null) {
            playerRanks = rank.getRanks(player);
        }
        playerRanks.add("all");

        for (String playerRank : playerRanks) {
            //getLogger().info(playerRank);
            if (GroupPerm.hasPermission(playerRank, permission)) {
                //getLogger().info(playerRank + " has perm " + permission);
                return true;
            }
        }

        return false;
    }

    public boolean setPermission(String player, String permission) {
        if (hasPermission(player,permission)) {
            return true;
        }

        player = userMap.getUUID(player);
        if (UserMap.NO_USER.equalsIgnoreCase(player)) {
            return false;
        }

        PlayerPerm p = new PlayerPerm();
        p.setPlayer(player);
        p.setPermission(permission);

        return p.save(database);
    }

    public boolean groupSetPermission(String group, String permission) {
        if (GroupPerm.hasPermission(group,permission)) {
            return true;
        }

        GroupPerm p = new GroupPerm();
        p.setGroup(group);
        p.setPermission(permission);

        return p.save(database);
    }

    public boolean removePermission(String player, String permission) {
        if (database == null) {
            return false;
        }
        if (!hasPermission(player,permission)) {
            return true;
        }

        player = userMap.getUUID(player);
        if (UserMap.NO_USER.equalsIgnoreCase(player)) {
            return false;
        }

        List<PlayerPerm> perms = (List<PlayerPerm>)database.select(PlayerPerm.class,"player = '"
                + database.makeSafe(player) + "' AND permission = '" + database.makeSafe(permission) + "'");

        if (perms.size() == 0) {
            return true;
        }

        return perms.get(0).delete(database);
    }

    public boolean groupRemovePermission(String group, String permission) {
        if (database == null) {
            return false;
        }
        if (!GroupPerm.hasPermission(group, permission)) {
            return true;
        }

        List<GroupPerm> perms = (List<GroupPerm>)database.select(GroupPerm.class,"`group` = '"
                + database.makeSafe(group) + "' AND permission = '" + database.makeSafe(permission) + "'");

        if (perms.size() == 0) {
            return true;
        }

        return perms.get(0).delete(database);
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

        if (command.equalsIgnoreCase("perms")) {
            if (args.length > 0) {
                String secondCommand = args[0];

                if (secondCommand.equalsIgnoreCase("set")) {
                    if (!hasPermission(uuid,"perms_set")) {
                        sender.sendMessage(ChatColor.RED + "You do not have permission to do this (perms_set)");
                        getLogger().info(playerName + " attempted unauthorized access of /perms set (perms_set)");
                        return true;
                    }

                    if (args.length > 2) {
                        String player = args[1];
                        if (userMap == null) {
                            sender.sendMessage(ChatColor.RED + "This functionality is currently unavailable");
                            return true;
                        }
                        String playerUuid = userMap.getUUID(player);
                        if (UserMap.NO_USER.equalsIgnoreCase(playerUuid)) {
                            sender.sendMessage(ChatColor.RED + "That user cannot be found");
                            return true;
                        }
                        String perm = args[2];

                        if (permMap.containsKey(perm)) {
                            int level = permMap.get(perm);
                            boolean found = false;
                            for (int i=1;i<=level;i++) {
                                if (hasPermission(uuid,"perms_level" + i)) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                sender.sendMessage(ChatColor.RED + "You do not have permission to grant this level of perms (perms_level" + level + ")");
                                return true;
                            }
                        }

                        if (!setPermission(playerUuid,perm)) {
                            sender.sendMessage("Unable to set permission");
                        } else {
                            sender.sendMessage(player + " now has permission " + perm);
                        }
                    } else {
                        sender.sendMessage("/perms set <player> <perm>");
                    }
                } else if (secondCommand.equalsIgnoreCase("remove")) {
                    if (!hasPermission(uuid,"perms_set")) {
                        getLogger().info(playerName + " attempted unauthorized access of /perms remove");
                        return true;
                    }

                    if (args.length > 2) {
                        String player = args[1];
                        if (userMap == null) {
                            sender.sendMessage(ChatColor.RED + "This functionality is currently unavailable");
                            return true;
                        }
                        String playerUuid = userMap.getUUID(player);
                        if (UserMap.NO_USER.equalsIgnoreCase(playerUuid)) {
                            sender.sendMessage(ChatColor.RED + "That user cannot be found");
                            return true;
                        }
                        String perm = args[2];

                        if (permMap.containsKey(perm)) {
                            int level = permMap.get(perm);
                            boolean found = false;
                            for (int i=1;i<=level;i++) {
                                if (hasPermission(uuid,"perms_level" + i)) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                sender.sendMessage(ChatColor.RED + "You do not have permission to revoke this level of perms (perms_level" + level + ")");
                                return true;
                            }
                        }

                        if (!removePermission(playerUuid,perm)) {
                            sender.sendMessage("Unable to remove permission");
                        } else {
                            sender.sendMessage(player + " no longer has permission " + perm);
                        }
                    } else {
                        sender.sendMessage("/perms remove <player> <perm>");
                    }
                } else if (secondCommand.equalsIgnoreCase("check")) {
                    if (!hasPermission(uuid,"perms_list")) {
                        getLogger().info(playerName + " attempted unauthorized access of /perms check");
                        return true;
                    }

                    if (args.length > 2) {
                        String player = args[1];
                        if (userMap == null) {
                            sender.sendMessage(ChatColor.RED + "This functionality is currently unavailable");
                            return true;
                        }
                        String playerUuid = userMap.getUUID(player);
                        if (UserMap.NO_USER.equalsIgnoreCase(playerUuid)) {
                            sender.sendMessage(ChatColor.RED + "That user cannot be found");
                            return true;
                        }
                        String perm = args[2];

                        if (!hasPermission(playerUuid,perm)) {
                            sender.sendMessage(player + " does not  have permission " + perm);
                        } else {
                            sender.sendMessage(player + " has permission " + perm);
                        }

                    } else {
                        sender.sendMessage("/perms check <player> <perm>");
                    }
                } else if ("list".equalsIgnoreCase(secondCommand)) {
                    if (!hasPermission(uuid,"perms_list")) {
                        getLogger().info(playerName + " attempted unauthorized access of /perms list");
                        return true;
                    }

                    if (args.length > 1) {
                        String player = args[1];
                        if (userMap == null) {
                            sender.sendMessage(ChatColor.RED + "This functionality is currently unavailable");
                            return true;
                        }
                        String playerUuid = userMap.getUUID(player);
                        if (UserMap.NO_USER.equalsIgnoreCase(playerUuid)) {
                            sender.sendMessage(ChatColor.RED + "That user cannot be found");
                            return true;
                        }

                        List<PlayerPerm> perms = (List<PlayerPerm>)database.select(PlayerPerm.class,"player = '" + database.makeSafe(playerUuid) + "'");
                        sender.sendMessage("Perms for " + player + ":");
                        for (PlayerPerm perm : perms) {
                            sender.sendMessage(perm.getPermission());
                        }
                    } else {
                        sender.sendMessage("/perms list <player>");
                    }
                }
            } else {
                sender.sendMessage("/perms <list|set|remove|check>");
            }
        } else if (command.equalsIgnoreCase("groupperms")) {
            if (args.length > 0) {
                String secondCommand = args[0];

                if (secondCommand.equalsIgnoreCase("set")) {
                    if (!hasPermission(uuid,"groupperms_set")) {
                        sender.sendMessage(ChatColor.RED + "You do not have permission to do this (groupperms_set)");
                        getLogger().info(playerName + " attempted unauthorized access of /groupperms set (groupperms_set)");
                        return true;
                    }

                    if (args.length > 2) {
                        String group = args[1];
                        String perm = args[2];

                        if (permMap.containsKey(perm)) {
                            int level = permMap.get(perm);
                            boolean found = false;
                            for (int i=1;i<=level;i++) {
                                if (hasPermission(playerName,"perms_level" + i)) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                sender.sendMessage(ChatColor.RED + "You do not have permission to grant this level of perms (min perms_level" + level + ")");
                                return true;
                            }
                        }

                        if (!groupSetPermission(group,perm)) {
                            sender.sendMessage("Unable to set permission");
                        } else {
                            sender.sendMessage(group + " now has permission " + perm);
                        }
                    } else {
                        sender.sendMessage("/groupperms set <group> <perm>");
                    }
                } else if (secondCommand.equalsIgnoreCase("remove")) {
                    if (!hasPermission(uuid,"groupperms_set")) {
                        getLogger().info(playerName + " attempted unauthorized access of /groupperms remove (groupperms_set)");
                        return true;
                    }

                    if (args.length > 2) {
                        String group = args[1];
                        String perm = args[2];

                        if (permMap.containsKey(perm)) {
                            int level = permMap.get(perm);
                            boolean found = false;
                            for (int i=1;i<=level;i++) {
                                if (hasPermission(playerName,"perms_level" + i)) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                sender.sendMessage(ChatColor.RED + "You do not have permission to revoke this level of perms (min perms_level" + level + ")");
                                return true;
                            }
                        }

                        if (!groupRemovePermission(group,perm)) {
                            sender.sendMessage("Unable to remove permission");
                        } else {
                            sender.sendMessage(group + " no longer has permission " + perm);
                        }
                    } else {
                        sender.sendMessage("/groupperms remove <group> <perm>");
                    }
                } else if (secondCommand.equalsIgnoreCase("check")) {
                    if (!hasPermission(uuid,"groupperms_list")) {
                        getLogger().info(playerName + " attempted unauthorized access of /groupperms check");
                        return true;
                    }

                    if (args.length > 2) {
                        String group = args[1];
                        String perm = args[2];

                        if (!GroupPerm.hasPermission(group,perm)) {
                            sender.sendMessage(group + " does not have permission " + perm);
                        } else {
                            sender.sendMessage(group + " has permission " + perm);
                        }

                    } else {
                        sender.sendMessage("/groupperms check <group> <perm>");
                    }
                } else if ("list".equalsIgnoreCase(secondCommand)) {
                    if (!hasPermission(uuid,"groupperms_list")) {
                        getLogger().info(playerName + " attempted unauthorized access of /groupperms list");
                        return true;
                    }

                    if (args.length > 1) {
                        String group = args[1];

                        List<GroupPerm> perms = (List<GroupPerm>)database.select(GroupPerm.class,"`group` = '" + database.makeSafe(group) + "'");
                        sender.sendMessage("Perms for " + group + ":");
                        for (GroupPerm perm : perms) {
                            sender.sendMessage(perm.getPermission());
                        }
                    } else {
                        sender.sendMessage("/groupperms list <group>");
                    }
                }
            } else {
                sender.sendMessage("/groupperms <list|set|remove|check>");
            }
        } else {
            return false;
        }

        return true;
    }
}
