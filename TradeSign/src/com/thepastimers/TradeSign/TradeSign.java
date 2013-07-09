package com.thepastimers.TradeSign;

import com.thepastimers.Database.Database;
import com.thepastimers.ItemName.ItemName;
import com.thepastimers.Money.Money;
import com.thepastimers.Rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 3/4/13
 * Time: 6:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class TradeSign extends JavaPlugin implements Listener {
    Database database;
    Rank rank;
    Money money;
    ItemName itemName;

    @Override
    public void onEnable() {
        getLogger().info("TradeSign init");

        getServer().getPluginManager().registerEvents(this,this);

        rank = (Rank)getServer().getPluginManager().getPlugin("Rank");

        if (rank == null) {
            getLogger().warning("Unable to load Rank plugin. Some functionality will not be available.");
        }

        database = (Database)getServer().getPluginManager().getPlugin("Database");

        if (database == null) {
            getLogger().warning("Unable to load Database plugin. Some functionality will not be available.");
        }

        money = (Money)getServer().getPluginManager().getPlugin("Money");

        if (money == null) {
            getLogger().warning("Unable to load Money plugin. Some functionality will not be available.");
        }

        itemName = (ItemName)getServer().getPluginManager().getPlugin("ItemName");

        if (itemName == null) {
            getLogger().warning("Unable to load ItemName plugin. Some functionality will not be available.");
        }

        getLogger().info("Table info: ");
        getLogger().info(SignData.getTableInfo());
        SignData.refreshCache(database,getLogger());

        getLogger().info("TradeSign init complete");
    }

    @Override
    public void onDisable() {
        getLogger().info("TradeSign disabled");
    }

    public SignData getSignAt(Location l) {
        return SignData.getSignAt(l.getBlockX(), l.getBlockY(), l.getBlockZ());
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }
        SignData sd = getSignAt(event.getBlock().getLocation());

        if (sd != null) {
            Block b = event.getBlock();

            if (!(b.getState() instanceof Sign)) {
                getLogger().warning("Warning, database thinks there is a trade sign at (" + b.getLocation().getBlockX()
                + "," + + b.getLocation().getBlockY() + "," + + b.getLocation().getBlockZ() + ") but it is in fact type "
                + b.getType().name());
                return;
            }

            Player p = event.getPlayer();
            if (p.getName().equalsIgnoreCase(sd.getPlayer())) {
                if (sd.getContains().equalsIgnoreCase("XP")) {
                    setExperience(p,getExperience(p) + sd.getAmount());
                    sd.delete(database);
                } else {
                    if (itemName != null && itemName.giveItem(p,sd.getContains(),sd.getAmount())) {
                        sd.delete(database);
                    } else {
                        p.sendMessage(ChatColor.RED + "Unable to remove trade sign");
                        event.setCancelled(true);
                    }
                }
            } else {
                event.getPlayer().sendMessage("This sign is only breakable by owner.");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Block b = event.getClickedBlock();
            if (b.getState() instanceof Sign) {
                Sign s = (Sign)b.getState();
                Location l = b.getLocation();
                SignData sd = SignData.getSignAt(l.getBlockX(),l.getBlockY(),l.getBlockZ());
                Player p = event.getPlayer();
                if (p != null && sd != null) {
                    if (p.getName().equalsIgnoreCase(sd.getPlayer())) {
                        if (sd.getContains() == null) {

                        } else {
                            if (sd.getContains().equalsIgnoreCase("XP")) {
                                int xp = getExperience(p);

                                if (xp < sd.getDispense()) {
                                    p.sendMessage("You don't have enough xp for this sign.");
                                } else {
                                    sd.setAmount(sd.getAmount() + sd.getDispense());
                                    sd.save(database);
                                    s.setLine(2,"Has: " + sd.getAmount());
                                    s.update(true);
                                    setExperience(p,xp-sd.getDispense());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public int getExperience(Player p) {
        if (p == null) {
            return 0;
        }

        int xp = 0;
        int forLevel = 17;

        //getLogger().info("get experience called with level " + p.getLevel() + " exp " + p.getExp());

        for (int i=1;i<=p.getLevel();i++) {
            xp += forLevel;
            if (i >= 16) {
                forLevel += 3;
            }
            if (i >= 31) {
                forLevel += 4;
            }
        }

        float percent = p.getExp();

        xp += ((float)forLevel)*percent;

        //getLogger().info("returning " + xp);

        return xp;
    }

    public void setExperience(Player p, int xp) {
        if (p == null) {
            return;
        }

        int level = 0;
        float lastXp = 0;
        int forLevel = 17;

        //getLogger().info("set experience called with xp " + xp);

        while (true) {
            if (xp < forLevel) {
                break;
            }
            level ++;
            xp -= forLevel;
            if (level >= 16) {
                forLevel += 3;
            }
            if (level >= 31) {
                forLevel += 4;
            }
        }

        float percent = ((float)xp)/((float)forLevel);

        //getLogger().info("level " + level + ", xp = " + percent);

        p.setLevel(level);
        p.setExp(percent);
    }

    @EventHandler
    public void signPlaced(SignChangeEvent event) {
        if (database == null) {
            return;
        }
        if (event.isCancelled()) {
            return;
        }

        if ("[SELL]".equalsIgnoreCase(event.getLine(0))) {
            Location l = event.getBlock().getLocation();
            SignData sd = new SignData();
            sd.setX(l.getBlockX());
            sd.setY(l.getBlockY());
            sd.setZ(l.getBlockZ());
            sd.setCost(0.0d);
            sd.setAmount(0);
            sd.setDispense(0);
            sd.setPlayer(event.getPlayer().getName());

            event.setLine(0,ChatColor.RED + "Sell Sign");

            if ("xp".equalsIgnoreCase(event.getLine(1))) {
                Double price = null;
                Integer dispense = null;
                if (!"".equalsIgnoreCase(event.getLine(2))) {
                    String line = event.getLine(2);

                    String[] lines = line.split("\\:");
                    if (lines.length >= 2) {
                        try {
                            price = Double.parseDouble(lines[0]);
                            dispense = Integer.parseInt(lines[1]);
                        } catch (NumberFormatException e) {
                            event.getPlayer().sendMessage("Something you entered wasn't a valid number...");
                            return;
                        }
                    }
                }

                if (!(price == null || price < 0 || dispense == null || dispense < 0)) {
                    sd.setContains("XP");
                    sd.setCost(price);
                    sd.setDispense(dispense);
                    event.setLine(1,"EXPERIENCE");
                    event.setLine(2,"Has: 0");
                    event.setLine(3,ChatColor.GREEN + "$" + price + ChatColor.BLACK + ":" + dispense);
                } else {
                    event.getPlayer().sendMessage("Sign requires price:dispense on the third line. Both must be positive.");
                    return;
                }

            }

            if (sd.save(database)) {
                event.getPlayer().sendMessage("Trade sign created");
            } else {
                event.getPlayer().sendMessage("Could not create trade sign.");
            }
        }
    }

    private boolean giveItem(Player p, String item, int amount) {
        if (p == null || item == null || amount < 0) {
            return false;
        }

        if (itemName == null) {
            return false;
        }

        PlayerInventory inv = p.getInventory();

        int empty = 0;

        Iterator itor = inv.iterator();

        while (itor.hasNext()) {
            ItemStack is = (ItemStack)itor.next();

            if (is == null) {
                empty ++;
                continue;
            }
        }

        ItemStack is = itemName.getItemFromName(item);
        int max = is.getMaxStackSize();

        int stacks = (int)Math.ceil(((double)amount)/((double)max));

        if (stacks > empty) {
            p.sendMessage("You don't have inventory room for that much");
            return false;
        }

        ItemStack[] items = inv.getContents();

        int amountDone = 0;
        while (amountDone < amount) {
            int toDoTotal = is.getMaxStackSize();
            if (amountDone + toDoTotal > amount) {
                toDoTotal = amount-amountDone;
            }

            int toDo = toDoTotal;

            for (int i=0;i<items.length;i++) {
                ItemStack is3 = items[i];
                if (is3 != null && is3.getType() == is.getType() && is3.getDurability() == is.getDurability()) {
                    if (is3.getAmount() + toDo <= is.getMaxStackSize()) {
                        is3.setAmount(is3.getAmount() + toDo);
                        break;
                    } else {
                        toDo -= (is.getMaxStackSize()-is3.getAmount());
                        is3.setAmount(is.getMaxStackSize());
                    }
                }
                if (items[i] == null) {
                    ItemStack is2 = new ItemStack(is.getType(),toDo);
                    is2.setDurability(is.getDurability());
                    items[i] = is2;
                    break;
                }
            }

            amountDone += toDoTotal;
        }

        inv.setContents(items);

        return true;
    }


}
