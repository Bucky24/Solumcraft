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
public class LoadPattern extends BukkitRunnable {
    private final Database database;
    private final JavaPlugin plugin;
    Player player;
    String world, pattern;
    int startx, starty, startz, curx, cury, curz;
    int done;
    static int blocksEach = 500;
    static int delay = 10;

    public LoadPattern(JavaPlugin plugin, Database d, Player p, String world, String pattern, int startx, int starty, int startz) {
        this.plugin = plugin;
        this.database = d;
        this.player = p;
        this.pattern = pattern;

        this.world = world;

        this.startx = startx;
        this.starty = starty;
        this.startz = startz;

        this.curx = startx;
        this.cury = starty;
        this.curz = startz;
        this.done = 0;
    }

    // this constructor assumes that the coords are already in proper order
    private LoadPattern(JavaPlugin plugin, Database d, Player p, String world, String pattern, int startx, int starty, int startz, int curx, int cury, int curz, int done) {
        this.plugin = plugin;
        this.database = d;
        this.player = p;
        this.pattern = pattern;
        this.world = world;
        this.startx = startx;
        this.starty = starty;
        this.startz = startz;
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
        List<PatternBlock> patternBlockList = (List<PatternBlock>)database.select(PatternBlock.class,"pattern = '" + database.makeSafe(pattern) + "' order by priority asc, y asc, x asc, z asc LIMIT " + done + ", " + blocksEach);
        int i = 0;

        if (patternBlockList.size() == 0) {
            areWeDone = true;
        }

        for (i=0;i<patternBlockList.size();i++) {
            PatternBlock pb = patternBlockList.get(i);
            Block b = w.getBlockAt(pb.getX()+startx,pb.getY()+starty,pb.getZ()+startz);
            b.setType(Material.getMaterial(pb.getBlock()));
            b.setData(pb.getData());
        }

        curx = x;
        cury = y;
        curz = z;
        done += i;

        //player.sendMessage(ChatColor.BLUE + "Done: " + done + ". This round handled " + i + " blocks.");
        //player.sendMessage(ChatColor.BLUE + "stats. We are now at: (" + curx + "," + cury + "," + curz + ") we started at (" + startx + "," + starty + "," + startz + "). Ending at (" + endx + "," + endy + "," + endz + ")");

        //plugin.getLogger().info(ChatColor.BLUE + "stats. We are now at: (" + curx + "," + cury + "," + curz + ") we started at (" + startx + "," + starty + "," + startz + "). Ending at (" + endx + "," + endy + "," + endz + ")");

        if (!areWeDone) {
            BukkitTask task = new LoadPattern(plugin,database,player,world,pattern,startx,starty,startz,curx,cury,curz,done).runTaskLater(plugin,delay);
        } else {
            if (player != null) {
                player.sendMessage(ChatColor.GREEN + "Done loading patterh " + pattern + "!");
            }
        }
    }
}