package com.thepastimers.Pattern;

import com.thepastimers.Database.Database;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 6/9/13
 * Time: 8:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class SavePattern extends BukkitRunnable {
    private final Database database;
    private final JavaPlugin plugin;
    Player player;
    String world, pattern;
    int startx, starty, startz, endx, endy, endz, curx, cury, curz;
    int done;
    static int blocksEach = 500;
    static int delay = 10;

    public SavePattern(JavaPlugin plugin, Database d, Player p, String world, String pattern, int startx, int starty, int startz, int endx, int endy, int endz) {
        this.plugin = plugin;
        this.database = d;
        this.player = p;
        this.pattern = pattern;

        if (endx < startx) {
            int tmp = startx;
            startx = endx;
            endx = tmp;
        }
        if (endy < starty) {
            int tmp = starty;
            starty = endy;
            endy = tmp;
        }
        if (endy < starty) {
            int tmp = starty;
            starty = endy;
            endy = tmp;
        }

        this.world = world;

        this.startx = startx;
        this.starty = starty;
        this.startz = startz;
        this.endx = endx;
        this.endy = endy;
        this.endz = endz;

        this.curx = startx;
        this.cury = starty;
        this.curz = startz;
        this.done = 0;
    }

    // this constructor assumes that the coords are already in proper order
    private SavePattern(JavaPlugin plugin, Database d, Player p, String world, String pattern, int startx, int starty, int startz, int endx, int endy, int endz, int curx, int cury, int curz, int done) {
        this.plugin = plugin;
        this.database = d;
        this.player = p;
        this.pattern = pattern;
        this.world = world;
        this.startx = startx;
        this.starty = starty;
        this.startz = startz;
        this.endx = endx;
        this.endy = endy;
        this.endz = endz;
        this.curx = curx;
        this.cury = cury;
        this.curz = curz;
        this.done = done;
    }

    public void run() {
        int x = curx;
        int y = cury;
        int z = curz;
        //plugin.getLogger().info(ChatColor.BLUE + "stats. We are now at: (" + curx + "," + cury + "," + curz + ") we started at (" + startx + "," + starty + "," + startz + "). Ending at (" + endx + "," + endy + "," + endz + ")");
        World w = plugin.getServer().getWorld(world);
        boolean areWeDone = false;
        int i = 0;
        int count = 0, error = 0;
        for (i=0;i<blocksEach;i++) {
            Block b = w.getBlockAt(x,y,z);

            if(b.getType() != Material.AIR) {
                PatternBlock pb = new PatternBlock();
                pb.setBlock(b.getType().name());
                pb.setPattern(pattern);
                pb.setX(x-startx);
                pb.setY(y-starty);
                pb.setZ(z-startz);
                pb.setData(b.getData());

                if (b.getType() == Material.TORCH || b.getType().name().equalsIgnoreCase("WOODEN_DOOR") ||
                        b.getType() == Material.LADDER || b.getType() == Material.STATIONARY_WATER || b.getType() == Material.WATER) {
                    pb.setPriority(2);
                }

                count ++;
                if (!pb.save(database)) {
                    error ++;
                }
            }

            y ++;
           // plugin.getLogger().info("Incrementing x to " + x);
            if (y > endy) {
                y = starty;
                x ++;
                //plugin.getLogger().info("incrementing y to " + y);
            }
            if (x > endx) {
                x = startx;
                z ++;
                //plugin.getLogger().info("incrementing z to " + z);
            }

            if (z > endz) {
                areWeDone = true;
                break;
            }
        }

        curx = x;
        cury = y;
        curz = z;
        done += i;

        int total = (endx-startx)*(endy-starty)*(endz-startz);

        //player.sendMessage(ChatColor.BLUE + "Done: " + done + "/" + total + ". This round handled " + count + " non-air blocks. Errors: " + error);
        if (done / (total/10) < 1000) {
            //player.sendMessage(ChatColor.BLUE + "stats. We are now at: (" + curx + "," + cury + "," + curz + ") we started at (" + startx + "," + starty + "," + startz + "). Ending at (" + endx + "," + endy + "," + endz + ")");
        }
        //plugin.getLogger().info(ChatColor.BLUE + "stats. We are now at: (" + curx + "," + cury + "," + curz + ") we started at (" + startx + "," + starty + "," + startz + "). Ending at (" + endx + "," + endy + "," + endz + ")");

        if (!areWeDone) {
            BukkitTask task = new SavePattern(plugin,database,player,world,pattern,startx,starty,startz,endx,endy,endz,curx,cury,curz,done).runTaskLater(plugin,delay);
        } else {
            player.sendMessage(ChatColor.GREEN + "Done saving " + pattern);
        }
    }
}