package com.thepastimers.Director;

import com.thepastimers.Coord.Coord;
import com.thepastimers.Coord.CoordData;
import com.thepastimers.Database.Database;
import com.thepastimers.ItemName.ItemName;
import com.thepastimers.Permission.Permission;
import com.thepastimers.Spawner.Spawner;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 5/3/13
 * Time: 10:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class Director extends JavaPlugin implements Listener {
    Database database;
    Permission permission;
    Spawner spawner;
    ItemName itemName;
    Coord coord;

    @Override
    public void onEnable() {
        getLogger().info("Director init");

        getServer().getPluginManager().registerEvents(this,this);

        database = (Database)getServer().getPluginManager().getPlugin("Database");

        if (database == null) {
            getLogger().warning("Unable to load Database module. Some functionality may not be available.");
        }

        permission = (Permission)getServer().getPluginManager().getPlugin("Permission");

        if (permission == null) {
            getLogger().warning("Unable to load Permission module. Some functionality may not be available.");
        }

        spawner = (Spawner)getServer().getPluginManager().getPlugin("Spawner");

        if (spawner == null) {
            getLogger().warning("Unable to load Spawner module. Some functionality may not be available.");
        }

        itemName = (ItemName)getServer().getPluginManager().getPlugin("ItemName");

        if (itemName == null) {
            getLogger().warning("Unable to load ItemName module. Some functionality may not be available.");
        }

        coord = (Coord)getServer().getPluginManager().getPlugin("Coord");

        if (coord == null) {
            getLogger().warning("Unable to load Coord module. Some functionality may not be available.");
        }

        getLogger().info("Table info: ");
        getLogger().info(Arena.getTableInfo());
        getLogger().info(Competitor.getTableInfo());

        getLogger().info("Director loaded");
    }

    @Override
    public void onDisable() {
        getLogger().info("Director disable");
    }

    public Arena getArena(double x, double y, double z, String world) {
        return getArena((int)x,(int)y,(int)z,world);
    }

    public Arena getArena(int x, int y, int z, String world) {
        if (database == null || world == null) return null;

        List<Arena> arenaList = (List<Arena>)database.select(Arena.class,"x1 <= " + x + " AND y1 <= " + y + " AND z1 <= " + z +
                                " AND x2 >= " + x + " AND y2 >= " + y + " AND z2 >= " + z + " AND world = '" + database.makeSafe(world) + "'");

        if (arenaList.size() == 0) return null;

        return arenaList.get(0);
    }

    public Arena getArenaById(int id) {
        if (database == null) return null;

        List<Arena> arenaList = (List<Arena>)database.select(Arena.class,"id = " + id);

        if (arenaList.size() == 0) return null;

        return arenaList.get(0);
    }

    public boolean isPlayerCompeting(Player p) {
        if (p == null) return false;
        return isPlayerCompeting(p.getName());
    }

    public boolean isPlayerCompeting(String player) {
        Competitor c = getCompetitor(player);

        return (c != null);
    }

    public Competitor getCompetitor(String player) {
        if (database == null || player == null) return null;

        List<Competitor> competitorList = (List<Competitor>)database.select(Competitor.class,"player = '" + database.makeSafe(player) + "'");

        if (competitorList.size() == 0) return null;

        for (Competitor c : competitorList) {
            if (!c.isActive()) continue;
            Arena a = getArenaById(c.getArena());
            if (a == null) continue;
            if (a.isActive()) return c;
        }

        return null;
    }

    public List<Competitor> getActiveCompetitors(int arena) {
        List<Competitor> ret = new ArrayList<Competitor>();
        if (database == null) return ret;

        List<Competitor> competitorList = (List<Competitor>)database.select(Competitor.class,"arena = " + arena + " AND active = 1");

        return competitorList;
    }

    public Arena getInactiveArena() {
        if (database == null) return null;

        List<Arena> arenaList = (List<Arena>)database.select(Arena.class,"active = 0");

        if (arenaList.size() == 0) return null;

        return arenaList.get(0);
    }

    @EventHandler
    public void death(PlayerDeathEvent event) {
        event.setDeathMessage("");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        String playerName = "";

        if (sender instanceof Player) {
            playerName = ((Player)sender).getName();
        } else {
            playerName = "CONSOLE";
        }

        String command = cmd.getName();

        if (command.equalsIgnoreCase("arena")) {
            if (args.length > 0) {
                String subCommand = args[0];
                if ("create".equalsIgnoreCase(subCommand)) {
                    if (permission == null || !permission.hasPermission(playerName,"arena_create") || playerName.equalsIgnoreCase("CONSOLE")) {
                        sender.sendMessage(ChatColor.RED + "You don't have permissions for this command (arena_create)");
                        return true;
                    }
                    List<CoordData> coords = coord.popCoords(playerName,2);

                    if (coords.size() < 2) {
                        sender.sendMessage(ChatColor.RED + "You need two coords set in order to create an arena");
                        return true;
                    }

                    CoordData c1 = coords.get(0);
                    CoordData c2 = coords.get(1);

                    int x1 = (int)c1.getX();
                    int z1 = (int)c1.getZ();
                    int y1 = (int)c1.getY();
                    int x2 = (int)c2.getX();
                    int z2 = (int)c2.getZ();
                    int y2 = (int)c2.getY();

                    if (x1 > x2) {
                        int tmp = x1;
                        x1 = x2;
                        x2 = tmp;
                    }

                    if (z1 > z2) {
                        int tmp = z1;
                        z1 = z2;
                        z2 = tmp;
                    }

                    if (y1 > y2) {
                        int tmp = y1;
                        y1 = y2;
                        y2 = tmp;
                    }

                    Player p = (Player)sender;
                    World w = p.getWorld();

                    if (getArena(x1,y1,z1,w.getName()) != null || getArena(x2,y2,z2,w.getName()) != null) {
                        sender.sendMessage(ChatColor.RED + "There is already an Arena intersecting that area");
                    }

                    Arena a = new Arena();
                    a.setActive(false);
                    a.setX1(x1);
                    a.setY1(y1);
                    a.setZ1(z1);
                    a.setX2(x2);
                    a.setY2(y2);
                    a.setZ2(z2);
                    a.setWorld(w.getName());
                    a.setStartx(null);
                    if (a.save(database)) {
                        sender.sendMessage(ChatColor.RED + "Cannot save Arena data");
                    } else {
                        sender.sendMessage(ChatColor.GREEN + "Arena created!");
                    }
                } else if ("join".equalsIgnoreCase(subCommand)) {
                    if (permission == null || !permission.hasPermission(playerName,"arena_compete") || playerName.equalsIgnoreCase("CONSOLE")) {
                        sender.sendMessage(ChatColor.RED + "You don't have permissions for this command (arena_compete)");
                        return true;
                    }

                    if (args.length > 1) {
                        int id = 0;
                        try {
                            id = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            sender.sendMessage(ChatColor.RED + "Arena ID must be a number");
                            return true;
                        }

                        Arena a = getArenaById(id);

                        if (a == null) {
                            sender.sendMessage(ChatColor.RED + "Arena " + id + " does not exist");
                            return true;
                        }

                        if (a.getStartx() == null) {
                            sender.sendMessage(ChatColor.RED + "Arena " + id + " does not have a designated start point");
                            return true;
                        }

                        if (!a.isActive()) {
                            sender.sendMessage(ChatColor.RED + "Arena " + id + " is not currently active");
                            return true;
                        }

                        Player p = (Player)sender;
                        Location pl = p.getLocation();

                        Competitor c = new Competitor();
                        c.setArena(a.getId());
                        c.setPlayer(p.getName());
                        c.setPrevX(pl.getX());
                        c.setPrevY(pl.getY());
                        c.setPrevZ(pl.getZ());
                        c.setPrevWorld(pl.getWorld().getName());
                        c.setDeaths(0);
                        c.setKills(0);
                        c.setPoints(0);
                        c.setActive(true);

                        if (!c.save(database)) {
                            sender.sendMessage(ChatColor.RED + "Unable to log you as a competitor");
                            return true;
                        }

                        sender.sendMessage(ChatColor.GREEN + "Teleporting you to arena!");
                        Location l = new Location(getServer().getWorld(a.getWorld()),a.getStartx(),a.getStarty(),a.getStartz());
                        p.teleport(l);
                    } else {
                        sender.sendMessage(ChatColor.BLUE + "/arena compete <arena id>");
                    }
                } else if ("start".equalsIgnoreCase(subCommand)) {
                    if (permission == null || !permission.hasPermission(playerName,"arena_start") || playerName.equalsIgnoreCase("CONSOLE")) {
                        sender.sendMessage(ChatColor.RED + "You don't have permissions for this command (arena_start)");
                        return true;
                    }

                    if (args.length > 1) {
                        int id = 0;
                        try {
                            id = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            sender.sendMessage(ChatColor.RED + "Arena ID must be a number");
                            return true;
                        }

                        Arena a = getArenaById(id);

                        if (a == null) {
                            sender.sendMessage(ChatColor.RED + "Arena " + id + " does not exist");
                            return true;
                        }

                        if (a.getStartx() == null) {
                            sender.sendMessage(ChatColor.RED + "Arena " + id + " does not have a designated start point");
                            return true;
                        }

                        if (a.isActive()) {
                            sender.sendMessage(ChatColor.RED + "Arena " + id + " is already active");
                            return true;
                        }

                        a.setActive(true);
                        if (!a.save(database)) {
                            sender.sendMessage(ChatColor.RED + "Unable to activate arena");
                            return true;
                        }

                        Player p = (Player)sender;
                        Location pl = p.getLocation();

                        Competitor c = new Competitor();
                        c.setArena(a.getId());
                        c.setPlayer(p.getName());
                        c.setPrevX(pl.getX());
                        c.setPrevY(pl.getY());
                        c.setPrevZ(pl.getZ());
                        c.setPrevWorld(pl.getWorld().getName());
                        c.setDeaths(0);
                        c.setKills(0);
                        c.setPoints(0);
                        c.setActive(true);

                        if (!c.save(database)) {
                            sender.sendMessage(ChatColor.RED + "Unable to log you as a competitor");
                            return true;
                        }

                        sender.sendMessage(ChatColor.GREEN + "Teleporting you to arena!");
                        Location l = new Location(getServer().getWorld(a.getWorld()),a.getStartx(),a.getStarty(),a.getStartz());
                        p.teleport(l);
                    } else {
                        sender.sendMessage(ChatColor.BLUE + "/arena start <arena id>");
                    }
                } else if ("leave".equalsIgnoreCase(subCommand)) {
                    if (permission == null || !permission.hasPermission(playerName,"arena_compete") || playerName.equalsIgnoreCase("CONSOLE")) {
                        sender.sendMessage(ChatColor.RED + "You don't have permissions for this command (arena_compete)");
                        return true;
                    }

                    Competitor c = getCompetitor(sender.getName());

                    if (c == null) {
                        sender.sendMessage(ChatColor.RED + "You are not currently competing in any arena");
                        return true;
                    }

                    c.setActive(false);
                    if (!c.save(database)) {
                        sender.sendMessage(ChatColor.RED + "Unable to update your competition status");
                    }

                    sender.sendMessage(ChatColor.GREEN + "Now leaving arena");
                    Location l = new Location(getServer().getWorld(c.getPrevWorld()),c.getPrevX(),c.getPrevY(),c.getPrevZ());
                    Player p = (Player)sender;
                    p.teleport(l);
                } else if ("end".equalsIgnoreCase(subCommand)) {
                    if (permission == null || !permission.hasPermission(playerName,"arena_start") || playerName.equalsIgnoreCase("CONSOLE")) {
                        sender.sendMessage(ChatColor.RED + "You don't have permissions for this command (arena_start)");
                        return true;
                    }

                    if (args.length > 1) {
                        int id = 0;
                        try {
                            id = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            sender.sendMessage(ChatColor.RED + "Arena ID must be a number");
                            return true;
                        }

                        Arena a = getArenaById(id);

                        if (a == null) {
                            sender.sendMessage(ChatColor.RED + "Arena " + id + " does not exist");
                            return true;
                        }

                        if (!a.isActive()) {
                            sender.sendMessage(ChatColor.RED + "Arena " + id + " is not active");
                            return true;
                        }

                        List<Competitor> competitorList = getActiveCompetitors(id);

                        if (competitorList.size() > 0) {
                            sender.sendMessage(ChatColor.RED + "There are still active competitors in the arena");
                            return true;
                        }

                        a.setActive(false);
                        if (!a.save(database)) {
                            sender.sendMessage(ChatColor.RED + "Unable to deactivate arena");
                            return true;
                        }

                        sender.sendMessage(ChatColor.GREEN + "Arena ended");
                    } else {
                        sender.sendMessage(ChatColor.BLUE + "/arena end <arena id>");
                    }
                } else if ("setSpawn".equalsIgnoreCase(subCommand)) {
                    if (permission == null || !permission.hasPermission(playerName,"arena_create") || playerName.equalsIgnoreCase("CONSOLE")) {
                        sender.sendMessage(ChatColor.RED + "You don't have permissions for this command (arena_create)");
                        return true;
                    }

                    if (args.length > 1) {
                        int id = 0;
                        try {
                            id = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            sender.sendMessage(ChatColor.RED + "Arena ID must be a number");
                            return true;
                        }

                        Arena a = getArenaById(id);

                        if (a == null) {
                            sender.sendMessage(ChatColor.RED + "Arena " + id + " does not exist");
                            return true;
                        }

                        List<CoordData> coords = coord.popCoords(playerName,2);

                        if (coords.size() < 1) {
                            sender.sendMessage(ChatColor.RED + "You need 1 coord set in order to set an arena spawn");
                            return true;
                        }

                        CoordData c1 = coords.get(0);

                        int x1 = (int)c1.getX();
                        int z1 = (int)c1.getZ();
                        int y1 = (int)c1.getY();

                        a.setStartx(x1);
                        a.setStarty(y1);
                        a.setStartz(z1);

                        if (!a.save(database)) {
                            sender.sendMessage(ChatColor.RED + "Unable to set arena spawn");
                            return true;
                        }

                        sender.sendMessage(ChatColor.GREEN + "Arena spawn set to (" + x1 + "," + y1 + "," + z1 + ")");
                    } else {
                        sender.sendMessage(ChatColor.BLUE + "/arena start <arena id>");
                    }
                }
            } else {
                sender.sendMessage(ChatColor.BLUE + "/arena <create|setSpawn|start|join|leave|end>");
            }
        } else {
            return false;
        }

        return true;
    }
}
