package com.thepastimers.TradeSign;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 6/14/14
 * Time: 10:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class UpdateSigns extends BukkitRunnable {
    private final JavaPlugin plugin;

    public UpdateSigns(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void run() {
        plugin.getLogger().info("Updating signs!");
        List<SignData> dataList = SignData.getPlayerSigns("SERVER");
        for (SignData sd : dataList) {
            String type = sd.getContains();
            plugin.getLogger().info("Running type " + type);
            TradeSign ts = (TradeSign)plugin;
            int stock = ts.money.getStock(type);
            World w = plugin.getServer().getWorld(sd.getWorld());
            if (w == null) {
                plugin.getLogger().warning("Trade sign " + sd.getId() + " has world " + sd.getWorld() + " that does not exist");
                continue;
            }
            sd.setAmount(stock);
            double price = ts.money.getPrice(type);
            if (price <= 0) price = 0;
            double cost = price;
            cost *= 1.5;
            int dispense = 1;
            int intcost = (int)cost;
            plugin.getLogger().info(type + " " + intcost + " " + price);
            if (intcost == price && price != 0) {
                cost = price*2;
            }
            sd.setCost((int)cost);
            sd.setDispense(dispense);
            Block b = w.getBlockAt(sd.getX(),sd.getY(),sd.getZ());
            if (b.getType() !=  Material.SIGN_POST && b.getType() != Material.SIGN && b.getType() != Material.WALL_SIGN) {
                plugin.getLogger().warning("Trade sign " + sd.getId() + " has coords that point to a non-sign block");
                continue;
            }
            Sign s = (Sign)b.getState();
            s.setLine(2,"Has: " + sd.getAmount());
            s.setLine(3,"$" + sd.getCost() + " for " + sd.getDispense());
            s.update(true);
            sd.save(ts.database);
        }
    }
}