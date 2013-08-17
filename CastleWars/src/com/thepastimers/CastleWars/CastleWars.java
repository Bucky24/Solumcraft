package com.thepastimers.CastleWars;

import com.thepastimers.Coord.Coord;
import com.thepastimers.Coord.CoordData;
import com.thepastimers.Database.Database;
import com.thepastimers.Money.Money;
import com.thepastimers.Pattern.Pattern;
import com.thepastimers.Permission.Permission;
import com.thepastimers.Plot.Plot;
import com.thepastimers.Plot.PlotData;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 8/17/13
 * Time: 12:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class CastleWars extends JavaPlugin implements Listener {
    Plot plot;
    Database database;
    Permission permission;
    Money money;
    Pattern pattern;
    Map<Player,ClaimCastle> claims;

    static int MAX_LEVEL = 5;

    @Override
    public void onEnable() {
        getLogger().info("CastleWars init");

        getServer().getPluginManager().registerEvents(this,this);

        database = (Database)getServer().getPluginManager().getPlugin("Database");
        if (database == null) {
            getLogger().warning("Unable to load Database plugin. Some functionality may not be available");
        }

        permission = (Permission)getServer().getPluginManager().getPlugin("Permission");
        if (permission == null) {
            getLogger().warning("Unable to load Permission plugin. Some functionality may not be available");
        }

        plot = (Plot)getServer().getPluginManager().getPlugin("Plot");
        if (plot == null) {
            getLogger().warning("Unable to load Plot plugin. Some functionality may not be available");
        }

        plot.registerPlotEnter(CastleWars.class,this);
        plot.registerPlotLeave(CastleWars.class,this);

        claims = new HashMap<Player, ClaimCastle>();

        money = (Money)getServer().getPluginManager().getPlugin("Money");
        if (money == null) {
            getLogger().warning("Unable to load Money plugin. Some functionality may not be available");
        }

        pattern = (Pattern)getServer().getPluginManager().getPlugin("Pattern");
        if (pattern == null) {
            getLogger().warning("Unable to load Pattern plugin. Some functionality may not be available");
        }

        getLogger().info(CastleData.getTableInfo());
        CastleData.refreshCache(database,getLogger());
    }

    @Override
    public void onDisable() {
        getLogger().info("CastleWars disable");
    }

    public void handlePlotLeave(PlotData pd,Player p) {
        CastleData cd = CastleData.getCastleForPlot(pd);
        if (cd == null) return;

        if (claims.containsKey(p)) {
            ClaimCastle claimCastle = claims.get(p);
            claimCastle.cancel();
        }
    }

    public void handlePlotEnter(PlotData pd,Player p) {
        CastleData cd = CastleData.getCastleForPlot(pd);
        if (cd == null) return;
        p.sendMessage(ChatColor.GREEN + "You have entered a CastleWars castle. This castle is level " + cd.getLevel());
        if ("Unclaimed".equalsIgnoreCase(cd.getOwner())) {
            p.sendMessage(ChatColor.GREEN + "This castle is unclaimed");
        } else {
            p.sendMessage(ChatColor.GREEN + "This castle is claimed by " + cd.getOwner());
        }
        if (p.getName() != cd.getOwner()) {
            Player player = getServer().getPlayer(cd.getOwner());
            if (player != null) {
                Location l = player.getLocation();
                PlotData pd2 = PlotData.getPlotAtLocation(l.getBlockX(),l.getBlockZ(),l.getWorld().getName(),true);
                if (pd2 == null) pd2 = PlotData.getPlotAtLocation(l.getBlockX(),l.getBlockZ(),l.getWorld().getName(),false);
                if (pd2 != null) {
                    if (pd2.getId() == pd.getId()) {
                        p.sendMessage(ChatColor.RED + "The owner is currently occupying this castle");
                        return;
                    }
                }
            }
            ClaimCastle cc = new ClaimCastle(this,database,cd,10,p);
            cc.runTaskTimer(this,20,20);
            claims.put(p,cc);
        }
        //p.sendMessage(ChatColor.GREEN + "Remain in this plot for 5 minutes to capture this castle");
    }

    public void regenerateCastle(CastleData cd) {
        removeCastle(cd);
        if (plot == null || pattern == null || cd == null) return;
        PlotData pd = PlotData.getPlotById(cd.getPlot());
        if (pd == null) {
            return;
        }

        String patternStr = "castle_l" + cd.getLevel();
        int c_x = pattern.getXSize(patternStr);
        int c_z = pattern.getZSize(patternStr);
        int p_x = pd.getX2()-pd.getX1();
        int p_z = pd.getZ2()-pd.getZ1();

        if (!doesItFit(cd)) return;
        // center the pattern in the plot
        int p_center_x = pd.getX1() + p_x/2;
        int p_center_z = pd.getZ1() + p_z/2;
        int off_x = (p_x/2)-(c_x/2);
        int off_z = (p_z/2)-(c_z/2);

        int c_y = -1;

        // get height (first non-air block)
        World w = getServer().getWorld(pd.getWorld());
        //getLogger().info("Checking world " + pd.getWorld());
        for (int i=w.getMaxHeight()-1;i>0;i--) {
            Block b = w.getBlockAt(pd.getX1() + p_center_x,i,pd.getZ1() + p_center_z);
            if (b.getType() != Material.AIR) {
                c_y = i;
                //getLogger().info("Found non air block at " + b.getType().name() + " at " + c_y);
                break;
            }
        }

        if (c_y == -1) return;

        //getLogger().info(pd.getX1() + " " + off_x);

        off_x += pd.getX1();
        off_z += pd.getZ1();

        //getLogger().info("Creating castle " + patternStr + " at (" + off_x + "," + c_y + "," + off_z + ")");

        pattern.loadPattern(patternStr,off_x,c_y,off_z,pd.getWorld());
    }

    public void upgradeCastle(CastleData cd) {
        removeCastle(cd);
        if (plot == null || pattern == null || cd == null) return;
        PlotData pd = PlotData.getPlotById(cd.getPlot());
        if (pd == null) {
            return;
        }

        String patternStr = "castle_l" + cd.getLevel();
        int c_x = pattern.getXSize(patternStr);
        int c_z = pattern.getZSize(patternStr);
        int p_x = pd.getX2()-pd.getX1();
        int p_z = pd.getZ2()-pd.getZ1();

        if (!doesItFit(cd)) return;
        // center the pattern in the plot
        int p_center_x = pd.getX1() + p_x/2;
        int p_center_z = pd.getZ1() + p_z/2;
        int off_x = (p_x/2)-(c_x/2);
        int off_z = (p_z/2)-(c_z/2);

        String patternStr2 = "castle_l" + (cd.getLevel()-1);
        int c_x2 = pattern.getXSize(patternStr2);
        int c_z2 = pattern.getZSize(patternStr2);
        int p_x2 = pd.getX2()-pd.getX1();
        int p_z2 = pd.getZ2()-pd.getZ1();

        // center the pattern in the plot
        int off_x2 = (p_x2/2)-(c_x2/2);
        int off_z2 = (p_z2/2)-(c_z2/2);

        int c_y = -1;

        // get height (first non-air block)
        World w = getServer().getWorld(pd.getWorld());
        //getLogger().info("Checking world " + pd.getWorld());
        for (int i=w.getMaxHeight()-1;i>0;i--) {
            Block b = w.getBlockAt(pd.getX1() + p_center_x,i,pd.getZ1() + p_center_z);
            if (b.getType() != Material.AIR) {
                c_y = i;
                //getLogger().info("Found non air block at " + b.getType().name() + " at " + c_y);
                break;
            }
        }

        if (c_y == -1) return;

        //getLogger().info(pd.getX1() + " " + off_x);

        off_x += pd.getX1();
        off_z += pd.getZ1();
        off_x2 += pd.getX1();
        off_z2 += pd.getZ1();

        //getLogger().info("Replacing castle " + patternStr2 + " at (" + off_x2 + "," + c_y + "," + off_z2 + ") with castle " + patternStr + " at (" + off_x + "," + c_y + "," + off_z + ")");

        pattern.clearThenLoadPattern(patternStr2,patternStr,off_x2,c_y,off_z2,pd.getWorld(),off_x,c_y,off_z,pd.getWorld());
    }

    private boolean doesItFit(CastleData cd) {
        String patternStr = "castle_l" + cd.getLevel();
        PlotData pd = PlotData.getPlotById(cd.getPlot());
        if (pd == null) {
            return false;
        }

        int c_x = pattern.getXSize(patternStr);
        int c_z = pattern.getZSize(patternStr);
        int p_x = pd.getX2()-pd.getX1();
        int p_z = pd.getZ2()-pd.getZ1();

        return !(c_x > p_x || c_z > p_z);
    }

    public void removeCastle(CastleData cd) {
        if (plot == null || pattern == null || cd == null) return;
        PlotData pd = PlotData.getPlotById(cd.getPlot());
        if (pd == null) {
            return;
        }

        String patternStr = "castle_l" + cd.getLevel();
        int c_x = pattern.getXSize(patternStr);
        int c_z = pattern.getZSize(patternStr);
        int p_x = pd.getX2()-pd.getX1();
        int p_z = pd.getZ2()-pd.getZ1();

        // center the pattern in the plot
        int p_center_x = pd.getX1() + p_x/2;
        int p_center_z = pd.getZ1() + p_z/2;
        int off_x = (p_x/2)-(c_x/2);
        int off_z = (p_z/2)-(c_z/2);

        int c_y = -1;

        // get height (first non-air block)
        World w = getServer().getWorld(pd.getWorld());
        //getLogger().info("Checking world " + pd.getWorld());
        for (int i=w.getMaxHeight()-1;i>0;i--) {
            Block b = w.getBlockAt(pd.getX1() + p_center_x,i,pd.getZ1() + p_center_z);
            if (b.getType() != Material.AIR) {
                c_y = i;
                //getLogger().info("Found non air block at " + b.getType().name() + " at " + c_y);
                break;
            }
        }

        if (c_y == -1) return;

        //getLogger().info(pd.getX1() + " " + off_x);

        off_x += pd.getX1();
        off_z += pd.getZ1();

        //getLogger().info("Removing castle " + patternStr + " at (" + off_x + "," + c_y + "," + off_z + ")");

        pattern.clearPattern(patternStr, off_x, c_y, off_z, pd.getWorld());
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

        if (command.equalsIgnoreCase("cw")) {
            if (permission == null || !permission.hasPermission(playerName,"castle_command") || playerName.equalsIgnoreCase("CONSOLE")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this command (castle_command)");
                return true;
            }

            if (args.length > 0) {
                String subCommand = args[0];

                if ("create".equalsIgnoreCase(subCommand)) {
                    if (permission == null || !permission.hasPermission(playerName,"castle_create") || playerName.equalsIgnoreCase("CONSOLE")) {
                        sender.sendMessage(ChatColor.RED + "You don't have permission to use this command (castle_create)");
                        return true;
                    }

                    Player p = (Player)sender;
                    Location l = p.getLocation();

                    if (plot == null) {
                        sender.sendMessage(ChatColor.RED + "This command is not currently available");
                        return true;
                    }

                    PlotData pd = PlotData.getPlotAtLocation(l.getBlockX(),l.getBlockZ(),l.getWorld().getName(),true);
                    if (pd == null) {
                        pd = PlotData.getPlotAtLocation(l.getBlockX(),l.getBlockZ(),l.getWorld().getName(),false);
                    }
                    if (pd == null) {
                        sender.sendMessage(ChatColor.RED + "You must be inside a plot to do this");
                        return true;
                    }

                    if (CastleData.getCastleForPlot(pd) != null) {
                        sender.sendMessage(ChatColor.RED + "There is already a castle on this plot");
                        return true;
                    }

                    CastleData cd = new CastleData();
                    cd.setPlot(pd.getId());
                    cd.setLevel(1);
                    cd.setOwner("Unclaimed");

                    pd.setOwner("Server");
                    pd.setPve(true);
                    pd.setName("Unclaimed castle");
                    pd.setPve(true);

                    if (!pd.save(database)) {
                        sender.sendMessage(ChatColor.RED + "Unable to update plot information");
                    } else {
                        if (!cd.save(database)) {
                            sender.sendMessage(ChatColor.RED + "Unable to create Castle");
                        } else {
                            sender.sendMessage(ChatColor.GREEN + "Castle created!");
                            regenerateCastle(cd);
                        }
                    }
                } else if ("remove".equalsIgnoreCase(subCommand)) {
                    if (permission == null || !permission.hasPermission(playerName,"castle_create") || playerName.equalsIgnoreCase("CONSOLE")) {
                        sender.sendMessage(ChatColor.RED + "You don't have permission to use this command (castle_create)");
                        return true;
                    }

                    Player p = (Player)sender;
                    Location l = p.getLocation();

                    if (plot == null) {
                        sender.sendMessage(ChatColor.RED + "This command is not currently available");
                        return true;
                    }

                    PlotData pd = PlotData.getPlotAtLocation(l.getBlockX(),l.getBlockZ(),l.getWorld().getName(),true);
                    if (pd == null) {
                        pd = PlotData.getPlotAtLocation(l.getBlockX(),l.getBlockZ(),l.getWorld().getName(),false);
                    }
                    if (pd == null) {
                        sender.sendMessage(ChatColor.RED + "You must be inside a plot to do this");
                        return true;
                    }

                    CastleData cd = CastleData.getCastleForPlot(pd);
                    if (cd == null) {
                        sender.sendMessage(ChatColor.RED + "This is not a CastleWars plot");
                        return true;
                    }

                    pd.setOwner(p.getName());
                    pd.setPve(false);
                    pd.setPvp(true);
                    pd.setName(p.getName() + "'s plot");

                    if (!pd.save(database)) {
                        sender.sendMessage(ChatColor.RED + "Unable to update plot information");
                    } else {
                        if (!cd.delete(database,CastleData.class)) {
                            sender.sendMessage(ChatColor.RED + "Unable to remove Castle");
                        } else {
                            sender.sendMessage(ChatColor.GREEN + "Castle removed!");
                            removeCastle(cd);
                            CastleData.refreshCache(database,getLogger());
                        }
                    }
                } else if ("upgrade".equalsIgnoreCase(subCommand)) {
                    Player p = (Player)sender;
                    Location l = p.getLocation();

                    if (plot == null) {
                        sender.sendMessage(ChatColor.RED + "This command is not currently available");
                        return true;
                    }

                    PlotData pd = PlotData.getPlotAtLocation(l.getBlockX(),l.getBlockZ(),l.getWorld().getName(),true);
                    if (pd == null) {
                        pd = PlotData.getPlotAtLocation(l.getBlockX(),l.getBlockZ(),l.getWorld().getName(),false);
                    }
                    if (pd == null) {
                        sender.sendMessage(ChatColor.RED + "You must be inside a plot to do this");
                        return true;
                    }

                    CastleData cd = CastleData.getCastleForPlot(pd);

                    if (cd == null) {
                        sender.sendMessage(ChatColor.RED + "This is not a castle");
                        return true;
                    }

                    if (!cd.getOwner().equalsIgnoreCase(playerName)) {
                        sender.sendMessage(ChatColor.RED + "You do not own this castle");
                        return true;
                    }

                    cd.setLevel(cd.getLevel()+1);
                    if (!doesItFit(cd)) {
                        cd.setLevel(cd.getLevel()-1);
                        sender.sendMessage(ChatColor.RED + "This castle cannot level up that high");
                        return false;
                    }

                    if (cd.save(database)) {
                        sender.sendMessage(ChatColor.GREEN + "Castle upgraded");
                        upgradeCastle(cd);
                    } else {
                        sender.sendMessage(ChatColor.RED + "Unable to upgrade castle");
                    }
                }
            } else {
                sender.sendMessage("/cw <create|remove|upgrade>");
            }
        } else {
            return false;
        }

        return true;
    }
}
