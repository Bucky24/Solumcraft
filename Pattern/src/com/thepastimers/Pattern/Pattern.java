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
import org.bukkit.scheduler.BukkitTask;

import java.sql.ResultSet;
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

    public boolean loadPattern(String patternName, int x, int y, int z, String world) {
        LoadPattern pattern = new LoadPattern(this,database,null,world,patternName,x,y,z);
        pattern.run();

        return true;
    }

    public boolean clearThenLoadPattern(String patternName, String patternName2, int x, int y, int z, String world, int x2, int y2, int z2, String world2) {
        ClearThenLoad pattern = new ClearThenLoad(this,database,null,world,patternName,x,y,z,x2,y2,z2,patternName2,world2);
        pattern.run();

        return true;
    }


    public boolean clearPattern(String patternName, int x, int y, int z, String world) {
        ClearPattern pattern = new ClearPattern(this,database,null,world,patternName,x,y,z);
        pattern.run();

        return true;
    }

    public int getXSize(String patternName) {
        int minX = 0;
        int maxX = 0;

        ResultSet blocks = database.rawSelect("SELECT MIN(x),MAX(x) FROM pattern_block WHERE pattern = \"" + database.makeSafe(patternName) + "\"");
        if (blocks != null) {
            try {
                minX = blocks.getInt(1);
                maxX = blocks.getInt(2);
            } catch (Exception e) {
                // ignore
            }
        }

        return (maxX-minX);
    }

    public int getZSize(String patternName) {
        int minZ = 0;
        int maxZ = 0;

        ResultSet blocks = database.rawSelect("SELECT MIN(z),MAX(z) FROM pattern_block WHERE pattern = \"" + database.makeSafe(patternName) + "\"");
        if (blocks != null) {
            try {
                minZ = blocks.getInt(1);
                maxZ = blocks.getInt(2);
            } catch (Exception e) {
                // ignore
            }
        }

        return (maxZ-minZ);
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

                        /*int error = 0;
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
                                    pb.setData(b.getData());

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
                        }*/
                        p.sendMessage(ChatColor.BLUE + "Saving pattern");
                        SavePattern pattern = new SavePattern(this,database,p,w.getName(),name,x1,y1,z1,x2,y2,z2);
                        pattern.run();
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

                        /*List<PatternBlock> patternBlockList = (List<PatternBlock>)database.select(PatternBlock.class,"pattern = '" + database.makeSafe(name) + "'");

                        if (patternBlockList == null || patternBlockList.size() == 0) {
                            sender.sendMessage(ChatColor.RED + "Unable to retrieve pattern " + name);
                            return true;
                        }

                        int count = 0;
                        for (PatternBlock pb : patternBlockList) {
                            if (!"AIR".equalsIgnoreCase(pb.getBlock())) {
                                Block b = w.getBlockAt(pb.getX()+x1,pb.getY()+y1,pb.getZ()+z1);
                                b.setType(Material.getMaterial(pb.getBlock()));
                                b.setData(pb.getData());
                            }
                            count ++;
                        }*/

                        p.sendMessage(ChatColor.BLUE + "Using pattern" + name);
                        LoadPattern pattern = new LoadPattern(this,database,p,w.getName(),name,x1,y1,z1);
                        pattern.run();
                    } else {
                        sender.sendMessage("/pattern use <name>");
                    }
                } else if ("list".equalsIgnoreCase(subCommand)) {
                    if (permission == null || !permission.hasPermission(playerName,"pattern_use") || playerName.equalsIgnoreCase("CONSOLE")) {
                        sender.sendMessage(ChatColor.RED + "You don't have permission to use this command (pattern_use)");
                        return true;
                    }

                    List<PatternBlock> patternBlockList = (List<PatternBlock>)database.select(PatternBlock.class,"1 group by pattern");

                    sender.sendMessage("List of patterns:");
                    StringBuilder builder = new StringBuilder();
                    for (int i=0;i<patternBlockList.size();i++) {
                        PatternBlock pb = patternBlockList.get(i);
                        builder.append(pb.getPattern());
                        if (i < patternBlockList.size()-1) {
                            builder.append(",");
                        }
                    }
                    sender.sendMessage(builder.toString());
                } else if ("remove".equalsIgnoreCase(subCommand)) {
                    if (permission == null || !permission.hasPermission(playerName,"pattern_set") || playerName.equalsIgnoreCase("CONSOLE")) {
                        sender.sendMessage(ChatColor.RED + "You don't have permission to use this command (pattern_set)");
                        return true;
                    }

                    if (args.length > 1) {
                        String name = args[1];

                        List<PatternBlock> patternBlockList = (List<PatternBlock>)database.select(PatternBlock.class,"pattern = '" + database.makeSafe(name) + "'");

                        if (patternBlockList == null || patternBlockList.size() == 0) {
                            sender.sendMessage(ChatColor.RED + "Unable to find pattern " + name);
                            return true;
                        }

                        int count = 0;
                        int removed = 0;
                        for (PatternBlock b : patternBlockList) {
                            if (b.delete(database)) {
                                removed ++;
                            }
                            count ++;
                        }

                        sender.sendMessage("Removed " + removed + "/" + count);
                    } else {
                        sender.sendMessage("/pattern remove <name>");
                    }
                }
            } else {
                sender.sendMessage("/pattern <set|use|info|remove|list>");
            }
        } else {
            return false;
        }

        return true;
    }
}