package com.thepastimers.Coord;

import com.thepastimers.Permission.Permission;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: derp
 * Date: 10/4/12
 * Time: 8:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class Coord extends JavaPlugin implements Listener {
    Permission permission;

    Map<String,Queue<CoordData>> coords;

    @Override
    public void onEnable() {
        getLogger().info("Coord init");

        coords = new HashMap<String,Queue<CoordData>>();

        getServer().getPluginManager().registerEvents(this,this);

        permission = (Permission)getServer().getPluginManager().getPlugin("Permission");

        if (permission == null) {
            getLogger().warning("Unable to load Permission plugin. Some functionality may not be available");
        }

        getLogger().info("Coord init complete");
    }

    @Override
    public void onDisable() {
        getLogger().info("Corrd disable");
    }

    public void addCoord(String player, CoordData cd) {
        if (coords.containsKey(player)) {
            coords.get(player).add(cd);
        } else {
            Queue<CoordData> dat = new LinkedList<CoordData>();
            dat.add(cd);
            coords.put(player,dat);
        }
    }

    public int getCoordSize(String player) {
        if (coords.containsKey(player)) {
            return coords.get(player).size();
        }
        return 0;
    }

    public List<CoordData> popCoords(String player) {
        return popCoords(player,getCoordSize(player));
    }

    public List<CoordData> popCoords(String player,int count) {
        return popCoords(player,count,true);
    }

    public List<CoordData> popCoords(String player, int count, boolean clear) {
        List<CoordData> ret = new ArrayList<CoordData>();
        Queue<CoordData> dat = coords.get(player);

        if (dat == null) {
            return ret;
        }

        if (dat.size() < count) {
            return ret;
        }

        int i;
        for (i=0;i<count;i++) {
            ret.add(dat.remove());
        }

        if (clear) {
            clear(player);
        }

        return ret;
    }

    public void clear(String player) {
        coords.remove(player);
    }

    @EventHandler
     public void blockDamage(BlockDamageEvent event) {
        Player p = event.getPlayer();
        Block b = event.getBlock();
        if (permission != null && permission.hasPermission(p.getName(),"coord_coord")) {
            if (p.getItemInHand().getType() == Material.STICK) {
                Location l = b.getLocation();
                CoordData c = new CoordData(l.getX(),l.getY(),l.getZ());

                addCoord(p.getName(),c);

                p.sendMessage("Coordinate (" + l.getX() + "," + l.getY() + "," + l.getZ() + ") added. You have "
                        + getCoordSize(p.getName()) + " coords set");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void hitBlock(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        Block b = event.getClickedBlock();
        if (b == null) return;
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;
        if (permission != null && permission.hasPermission(p.getName(),"coord_coord")) {
            if (p.getItemInHand().getType() == Material.STICK) {
                Location l = b.getLocation();
                CoordData c = new CoordData(l.getX(),l.getY(),l.getZ());

                addCoord(p.getName(),c);

                p.sendMessage("Coordinate (" + l.getX() + "," + l.getY() + "," + l.getZ() + ") added. You have "
                        + getCoordSize(p.getName()) + " coords set");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        Block b = event.getBlock();
        if (p.getGameMode() == GameMode.CREATIVE && permission != null && permission.hasPermission(p.getName(),"coord_coord")) {
            if (p.getItemInHand().getType() == Material.STICK) {
                Location l = b.getLocation();
                CoordData c = new CoordData(l.getX(),l.getY(),l.getZ());

                addCoord(p.getName(),c);

                p.sendMessage("Coordinate (" + l.getX() + "," + l.getY() + "," + l.getZ() + ") added. You have "
                        + getCoordSize(p.getName()) + " coords set");
                event.setCancelled(true);
            }
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

        String command = cmd.getName();

        if (command.equalsIgnoreCase("coord")) {
            if (permission == null || !permission.hasPermission(playerName,"coord_admin")) {
                sender.sendMessage("You do not have permissions for this command");
                return true;
            }
            if (args.length > 0) {
                String subcommand = args[0];

                if (subcommand.equalsIgnoreCase("clear")) {
                    if (args.length > 1) {
                        String player = args[1];

                        clear(player);

                        sender.sendMessage(player + " now has " + getCoordSize(player) + " coords set");
                    } else {
                        sender.sendMessage("/coord clear <player>");
                    }
                } else {
                    sender.sendMessage("/coord <clear>");
                }
            } else {
                sender.sendMessage("/coord <clear>");
            }
        } else {
            return false;
        }

        return true;
    }
}
