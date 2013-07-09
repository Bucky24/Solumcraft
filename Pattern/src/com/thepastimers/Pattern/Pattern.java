package com.thepastimers.Pattern;

import com.thepastimers.Coord.Coord;
import com.thepastimers.Coord.CoordData;
import com.thepastimers.Database.Database;
import com.thepastimers.Permission.Permission;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 5/5/13
 * Time: 12:02 AM
 * To change this template use File | Settings | File Templates.
 */
public class Pattern extends JavaPlugin implements Listener {
    Database database;
    Permission permission;
    Coord coord;

    @Override
    public void onEnable() {
        getLogger().info("Pattern init");

        getServer().getPluginManager().registerEvents(this,this);

        database = (Database)getServer().getPluginManager().getPlugin("Database");
        if (database == null) {
            getLogger().warning("Unable to load Database plugin. Some functionality may not be available");
        }

        permission = (Permission)getServer().getPluginManager().getPlugin("Permission");
        if (permission == null) {
            getLogger().warning("Unable to load Permission plugin. Some functionality may not be available");
        }

        coord = (Coord)getServer().getPluginManager().getPlugin("Coord");
        if (coord == null) {
            getLogger().warning("Unable to load Coord plugin. Some functionality may not be available");
        }

        getLogger().info("Table info:");
        getLogger().info(PatternBlock.getTableInfo());

        getLogger().info("Pattern init complete");
    }

    @Override
    public void onDisable() {
        getLogger().info("Pattern disable");
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

        if (command.equalsIgnoreCase("pattern")) {
            if (permission == null || !permission.hasPermission(playerName,"pattern_pattern") || playerName.equalsIgnoreCase("CONSOLE")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this command (pattern_pattern)");
                return true;
            }
            if (args.length > 0) {
                String subCommand = args[0];
                if ("set".equalsIgnoreCase(subCommand)) {
                    if (permission == null || !permission.hasPermission(playerName,"pattern_set") || playerName.equalsIgnoreCase("CONSOLE")) {
                        sender.sendMessage(ChatColor.RED + "You don't have permission to use this command (pattern_set)");
                        return true;
                    }

                    if (args.length > 1) {
                        String name = args[1];

                        // TODO: make sure name is unique

                        CoordData coord1, coord2;
                        if (coord == null) {
                            sender.sendMessage(ChatColor.RED + "This action is not currently possible");
                            return true;
                        }

                        List<CoordData> coordDataList = coord.popCoords(playerName,2);
                        if (coordDataList.size() < 2) {
                            sender.sendMessage(ChatColor.RED + "You must have at least 2 coordinates set to complete this operation");
                            return true;
                        }

                        coord1 = coordDataList.get(0);
                        coord2 = coordDataList.get(1);

                        int x1,x2,y1,y2,z1,z2;
                        Player p = (Player)sender;
                        World w = p.getWorld();

                        x1 = (int)Math.min(coord1.getX(),coord2.getX());
                        x2 = (int)Math.max(coord1.getX(),coord2.getX());
                        y1 = (int)Math.min(coord1.getY(),coord2.getY());
                        y2 = (int)Math.max(coord1.getY(),coord2.getY());
                        z1 = (int)Math.min(coord1.getZ(),coord2.getZ());
                        z2 = (int)Math.max(coord1.getZ(),coord2.getZ());

                        int error = 0;
                        int count = 0;

                        for (int x=x1;x<=x2;x++) {
                            for (int y=y1;y<=y2;y++) {
                                for (int z=z1;z<=z2;z++) {
                                    Block b = w.getBlockAt(x,y,z);
                                    String type = b.getType().name();

                                    if ("AIR".equalsIgnoreCase(type)) {
                                        continue;
                                    }

                                    PatternBlock pb = new PatternBlock();
                                    pb.setBlock(type);
                                    pb.setPattern(name);
                                    pb.setX(x-x1);
                                    pb.setY(y-y1);
                                    pb.setZ(z-z1);

                                    if (!pb.save(database)) {
                                        error ++;
                                    }

                                    count ++;
                                }
                            }
                        }

                        p.sendMessage(ChatColor.GREEN + "Pattern " + name + " created: " + count + " blocks");
                        if (error > 0) {
                            p.sendMessage(ChatColor.RED.toString() + error + " blocks failed to save");
                        }
                    } else {
                        sender.sendMessage("/pattern set <name>");
                    }
                } else if ("use".equalsIgnoreCase(subCommand)) {
                    if (permission == null || !permission.hasPermission(playerName,"pattern_use") || playerName.equalsIgnoreCase("CONSOLE")) {
                        sender.sendMessage(ChatColor.RED + "You don't have permission to use this command (pattern_use)");
                        return true;
                    }

                    if (args.length > 1) {
                        String name = args[1];

                        // TODO: make sure name is unique

                        CoordData coord1;
                        if (coord == null) {
                            sender.sendMessage(ChatColor.RED + "This action is not currently possible");
                            return true;
                        }

                        List<CoordData> coordDataList = coord.popCoords(playerName,1);
                        if (coordDataList.size() < 1) {
                            sender.sendMessage(ChatColor.RED + "You must have at least 1 coordinate set to complete this operation");
                            return true;
                        }

                        coord1 = coordDataList.get(0);

                        int x1,y1,z1;
                        Player p = (Player)sender;
                        World w = p.getWorld();

                        x1 = (int)coord1.getX();
                        y1 = (int)coord1.getY();
                        z1 = (int)coord1.getZ();

                        List<PatternBlock> patternBlockList = (List<PatternBlock>)database.select(PatternBlock.class,"pattern = '" + database.makeSafe(name) + "'");

                        if (patternBlockList == null || patternBlockList.size() == 0) {
                            sender.sendMessage(ChatColor.RED + "Unable to retrieve pattern " + name);
                            return true;
                        }

                        int count = 0;
                        for (PatternBlock pb : patternBlockList) {
                            if (!"AIR".equalsIgnoreCase(pb.getBlock())) {
                                Block b = w.getBlockAt(pb.getX()+x1,pb.getY()+y1,pb.getZ()+z1);
                                b.setType(Material.getMaterial(pb.getBlock()));
                            }
                            count ++;
                        }

                        sender.sendMessage(ChatColor.GREEN + "Pattern " + name + " activated: " + count + " blocks");
                    } else {
                        sender.sendMessage("/pattern use <name>");
                    }
                }
            } else {
                sender.sendMessage("/pattern <set|use|info|remove>");
            }
        } else {
            return false;
        }

        return true;
    }
}