package com.thepastimers.Kit;

import com.thepastimers.Database.Database;
import com.thepastimers.ItemName.ItemName;
import com.thepastimers.Permission.Permission;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 5/30/13
 * Time: 7:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class Kit extends JavaPlugin implements Listener {
    Database database;
    ItemName itemName;
    Permission permission;

    @Override
    public void onEnable() {
        getLogger().info("Kit init");

        getServer().getPluginManager().registerEvents(this,this);

        database = (Database)getServer().getPluginManager().getPlugin("Database");

        if (database == null) {
            getLogger().warning("Unable to load Database module. Some functionality may not be available.");
        }

        permission = (Permission)getServer().getPluginManager().getPlugin("Permission");

        if (permission == null) {
            getLogger().warning("Unable to load Permission module. Some functionality may not be available.");
        }

        itemName = (ItemName)getServer().getPluginManager().getPlugin("ItemName");

        if (itemName == null) {
            getLogger().warning("Unable to load ItemName module. Some functionality may not be available.");
        }

        getLogger().info("Table info: ");
        getLogger().info(KitData.getTableInfo());
        getLogger().info(UserKit.getTableInfo());

        getLogger().info("Kit loaded");
    }

    @Override
    public void onDisable() {
        getLogger().info("Kit disabled");
    }

    public KitData getKit(String name) {
        if (database == null) return null;

        List<KitData> data = (List<KitData>)database.select(KitData.class,"name = '" + database.makeSafe(name) + "'");

        if (data.size() == 0) return null;

        return data.get(0);
    }

    public KitData getKit(int id) {
        if (database == null) return null;

        List<KitData> data = (List<KitData>)database.select(KitData.class,"id = " + id);

        if (data.size() == 0) return null;

        return data.get(0);
    }

    public int timesUsed(String kitName, String player) {
        if (kitName == null || player == null || database == null) return 0;
        KitData kit = getKit(kitName);
        if (kit == null) return 0;

        return timesUsed(kit.getId(),player);
    }

    public int timesUsed(int kit, String player) {
        if (player == null || database == null) return 0;

        List<UserKit> kits = (List<UserKit>)database.select(UserKit.class,"player = '" + database.makeSafe(player) + "' and kit = " + kit);

        return kits.size();
    }

    public boolean canUse(String kitName, String player) {
        if (kitName == null || player == null || database == null) return false;
        KitData kit = getKit(kitName);
        if (kit == null) return false;

        List<UserKit> kits = (List<UserKit>)database.select(UserKit.class,"player = '" + database.makeSafe(player) + "' and kit = " + kit.getId() + " order by last_used desc");

        if (kits.size() == 0) return true;

        UserKit userKit = kits.get(0);
        Date now = new Date();
        long diff = now.getTime()-userKit.getLastUsed().getTime();
        diff /= 1000;
        if (diff < kit.getTimeout()) {
            getServer().getPlayer(player).sendMessage(ChatColor.RED + "You cannot use that kit for another " + (kit.getTimeout() - diff) + " seconds");
        }
        return (diff >= kit.getTimeout());
    }

    public boolean useKit(String kitName, String player) {
        if (!canUse(kitName,player)) return false;

        if (kitName == null || player == null || database == null || itemName == null) return false;
        KitData kit = getKit(kitName);
        if (kit == null) return false;

        Player p = getServer().getPlayer(player);
        if (p == null) return false;

        JSONArray array = kit.getItems();

        for (int i=0;i<array.size();i++) {
            String item = (String)array.get(i);

            itemName.giveItem(p,item,1);
        }

        UserKit uk = new UserKit();
        uk.setPlayer(player);
        uk.setKit(kit.getId());
        uk.setLastUsed(new Timestamp((new Date()).getTime()));
        uk.save(database);

        return true;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        String playerName = "";

        if (sender instanceof Player) {
            playerName = ((Player)sender).getName();
        } else {
            playerName = "CONSOLE";
        }

        String command = cmd.getName();

        if (command.equalsIgnoreCase("kit")) {
            if (permission == null || !permission.hasPermission(playerName,"kit_kit") || playerName.equalsIgnoreCase("CONSOLE")) {
                sender.sendMessage(ChatColor.RED + "You don't have permissions for this command (kit_kit)");
                return true;
            }
            if (args.length > 0) {
                String kit = args[0];

                if (args.length > 1) {
                    String subCommand = args[1];
                    if ("create".equalsIgnoreCase(subCommand)) {
                        if (permission == null || !permission.hasPermission(playerName,"kit_create") || playerName.equalsIgnoreCase("CONSOLE")) {
                            sender.sendMessage(ChatColor.RED + "You don't have permissions for this command (kit_create)");
                            return true;
                        }

                        if (getKit(kit) != null) {
                            sender.sendMessage(ChatColor.RED + "Kit " + kit + " already exists");
                            return true;
                        }

                        if (args.length > 3) {
                            String intervalStr = args[2];
                            Long interval;
                            try {
                                interval = Long.parseLong(intervalStr);
                            } catch (NumberFormatException e) {
                                sender.sendMessage(ChatColor.RED + "Interval must be a number");
                                return true;
                            }
                            JSONArray items = new JSONArray();
                            for (int i=3;i<args.length;i++) {
                                items.add(args[i]);
                            }
                            KitData kitData = new KitData();
                            kitData.setItems(items);
                            kitData.setTimeout(interval);
                            kitData.setName(kit);
                            if (kitData.save(database)) {
                                sender.sendMessage(ChatColor.GREEN + "Kit created");
                            } else {
                                sender.sendMessage(ChatColor.RED + "Kit could not be created");
                            }
                        } else {
                            sender.sendMessage("/kit " + kit + " create <interval> <items>");
                        }
                    } else if ("view".equalsIgnoreCase(subCommand)) {
                        if (getKit(kit) == null) {
                            sender.sendMessage(ChatColor.RED + "No kit by that name exists");
                            return true;
                        }
                        // we don't need to check for kit_kit perms because we've already done this
                        KitData kitData = getKit(kit);
                        sender.sendMessage("Items in " + kit);
                        JSONArray items = kitData.getItems();
                        for (int i=0;i<items.size();i++) {
                            sender.sendMessage((String)items.get(i));
                        }
                    } else if ("remove".equalsIgnoreCase(subCommand)) {
                        if (getKit(kit) == null) {
                            sender.sendMessage(ChatColor.RED + "No kit by that name exists");
                            return true;
                        }
                        if (permission == null || !permission.hasPermission(playerName,"kit_create") || playerName.equalsIgnoreCase("CONSOLE")) {
                            sender.sendMessage(ChatColor.RED + "You don't have permissions for this command (kit_create)");
                            return true;
                        }

                        KitData kitData = getKit(kit);
                        if (kitData.delete(database)) {
                            sender.sendMessage(ChatColor.GREEN + "Kit " + kit + " has been removed");
                        } else {
                            sender.sendMessage(ChatColor.RED + "Kit " + kit + " has not been removed");
                        }
                    } else {
                        sender.sendMessage("/kit <name> [create|view|remove]");
                    }
                } else {
                    if (getKit(kit) == null) {
                        sender.sendMessage(ChatColor.RED + "No kit by that name exists");
                        return true;
                    }

                    if (useKit(kit,playerName)) {
                        sender.sendMessage(ChatColor.GREEN + "Kit given!");
                    } else {
                        sender.sendMessage(ChatColor.RED + "Unable to give kit.");
                    }
                }
            } else {
                List<KitData> kits = (List<KitData>)database.select(KitData.class,"1");
                sender.sendMessage("/kit <name> [create|view|remove]");
                sender.sendMessage("List of kits (type /kit <name> to use): ");
                for (KitData kd : kits) {
                    sender.sendMessage(kd.getName());
                }
            }
        } else {
            return false;
        }

        return true;
    }
}
