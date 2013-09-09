package com.thepastimers.CastleWars;

import com.thepastimers.CastleWars.CastleData;
import com.thepastimers.Database.Database;
import com.thepastimers.Plot.PlotData;
import com.thepastimers.Plot.PlotPerms;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 6/9/13
 * Time: 8:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClaimCastle extends BukkitRunnable {
    private final Database database;
    private final CastleWars plugin;
    private final CastleData cd;
    private int time;
    private final Player player;

    public ClaimCastle(CastleWars plugin, Database d, CastleData cd,int time, Player p) {
        this.plugin = plugin;
        this.database = d;
        this.cd = cd;
        this.time = time;
        this.player = p;
    }

    public CastleData getCd() {
        return cd;
    }

    public void run() {
        time --;
        if (time % 30 == 0 && time > 0) {
            player.sendMessage(ChatColor.GREEN + "" +  time + " seconds remain before you claim this castle");
        }
        PlotData pd = PlotData.getPlotById(cd.getPlot());
        if (time <= 0) {
            if (player != null) {
                if (pd != null) {
                    String oldOwner = cd.getOwner();
                    cd.setOwner(player.getName());
                    pd.setName(player.getName() + "'s castle");
                    if (pd.save(database)) {
                        if (cd.save(database)) {
                            player.sendMessage(ChatColor.GREEN + "You have successfully claimed this castle!");
                            List<Entity> entityList = getMobsInCastle(plugin,pd);
                            for (Entity e : entityList) {
                                e.remove();
                            }

                            // cancel anyone else trying to claim this plot
                            for (Player p : plugin.claims.keySet()) {
                                if (p.getUniqueId() == player.getUniqueId()) {
                                    continue;
                                }
                                if (plugin.claims.get(p).getCd().getId() == cd.getId()) {
                                    p.sendMessage(ChatColor.RED + player.getName() + "Captured this castle before you were able to");
                                    plugin.claims.get(p).cancel();
                                }
                            }

                            if ("Unclaimed".equalsIgnoreCase(oldOwner)) {
                                if (plugin.chat != null) {
                                    plugin.chat.saveMessage(":green:" + player.getName() + " has just claimed a castle",":red:Server");
                                }
                                plugin.getServer().broadcastMessage(ChatColor.GREEN + player.getName() + " has just claimed a castle");
                            } else {
                                if (plugin.chat != null) {
                                    plugin.chat.saveMessage(":green:" + player.getName() + " has just stolen a castle from " + oldOwner,":red:Server");
                                }
                                plugin.getServer().broadcastMessage(ChatColor.GREEN + player.getName() + " has just stolen a castle from " + oldOwner);
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Unable to update castle!");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Unable to update plot!");
                    }
                }
            }
            this.cancel();
        } else {
            // determine if we need to spawn mobs
            List<Entity> entityList = getMobsInCastle(plugin,pd);
            List<CastleSpawner> spawnerList = CastleSpawner.getSpawnersForCastle(cd);
            //plugin.getLogger().info(entityList.size() + " < " + (spawnerList.size()*4));
            if (entityList.size() < spawnerList.size()*4) {
                for (CastleSpawner s : spawnerList) {
                    World w = plugin.getServer().getWorld(pd.getWorld());
                    Location l = new Location(w,s.getX(),s.getY()+1,s.getZ());
                    Entity e = w.spawnEntity(l,EntityType.ZOMBIE);
                }
            }

            for (Entity e : entityList) {
                LivingEntity le = (LivingEntity)e;
                Creature c = (Creature)le;
                c.setTarget(player);
            }
        }
    }

    public static List<Entity> getMobsInCastle(CastleWars plugin, PlotData pd) {
        List<Entity> entityList = new ArrayList<Entity>();

        List<Entity> serverEntities = plugin.getServer().getWorld(pd.getWorld()).getEntities();

        for (Entity e : serverEntities) {
            if (e.getType() == EntityType.ZOMBIE) {
                Location l = e.getLocation();
                if (l.getBlockX() >= pd.getX1()-1 && l.getBlockX() <= pd.getX2()+1 && l.getBlockZ() >= pd.getZ1()-1 && l.getBlockZ() <= pd.getZ2()+1) {
                    entityList.add(e);
                }
            }
        }

        return entityList;
    }
}