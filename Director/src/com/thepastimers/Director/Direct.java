package com.thepastimers.Director;

import com.thepastimers.Database.Database;
import com.thepastimers.Spawner.Spawner;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 7/15/13
 * Time: 11:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class Direct extends BukkitRunnable {
    private final Database database;
    private final JavaPlugin plugin;
    private final Spawner spawner;

    public Direct(JavaPlugin plugin, Database d, Spawner s) {
        this.plugin = plugin;
        this.database = d;
        this.spawner = s;
    }

    public void run() {
        Player[] players = plugin.getServer().getOnlinePlayers();
        Director d = (Director)plugin;
        for (Player p : players) {
            Location l = p.getLocation();
            Arena a = d.getArena(l.getX(),l.getY(),l.getZ(),l.getWorld().getName());
            if (a != null) {
                int zombies = getEntityCount(30,30,30,EntityType.ZOMBIE,p);
                int pcount = getEntityCount(10,10,10,EntityType.PLAYER,p);
                int maxZombies = pcount*10;
                if (zombies < maxZombies) {

                }
            }
        }
    }

    private static int getEntityCount(int x, int y, int z, EntityType et, Player p) {
        List<Entity> entityList = p.getNearbyEntities(x,y,z);
        int near = 0;

        for (Entity e : entityList) {
            if (e.getType() == et) {
                near ++;
            }
        }

        return near;
    }
}
