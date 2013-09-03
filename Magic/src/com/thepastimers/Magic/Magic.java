package com.thepastimers.Magic;

import com.thepastimers.Database.Database;
import com.thepastimers.ItemName.ItemName;
import com.thepastimers.Permission.Permission;
import com.thepastimers.Worlds.Worlds;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 2/24/13
 * Time: 10:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class Magic extends JavaPlugin implements Listener {
    Database database;
    Permission permission;
    ItemName itemName;
    Worlds worlds;

    Map<String,Integer> mana;
    Map<String,Date> lastChecked;

    double defaultMaxMana = 50d;
    double increaseFactor = 100d;
    long secondsPerMana = 30;

    @Override
    public void onEnable() {
        getLogger().info("Magic init");

        getServer().getPluginManager().registerEvents(this,this);
        database = (Database)getServer().getPluginManager().getPlugin("Database");
        if (database == null) {
            getLogger().warning("Cannot load Database plugin. Some functionality may not be available");
        }

        permission = (Permission)getServer().getPluginManager().getPlugin("Permission");
        if (permission == null) {
            getLogger().warning("Cannot load Permission plugin. Some functionality may not be available");
        }

        itemName = (ItemName)getServer().getPluginManager().getPlugin("ItemName");
        if (itemName == null) {
            getLogger().warning("Cannot load ItemName plugin. Some functionality may not be available.");
        }

        worlds = (Worlds)getServer().getPluginManager().getPlugin("Worlds");
        if (worlds == null) {
            getLogger().warning("Unable to load Worlds plugin. Some functionality may not be available.");
        }

        mana = new HashMap<String, Integer>();
        lastChecked = new HashMap<String, Date>();

        getLogger().info("Table info: ");
        getLogger().info(PlayerMagic.getTableInfo());

        getLogger().info("Magic init complete");
    }

    @Override
    public void onDisable() {
        getLogger().info("Magic disabled");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (worlds != null && worlds.getPlayerWorldType(event.getPlayer().getName()) == Worlds.VANILLA) {
            return;
        }
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (event.getPlayer().getItemInHand().getType() == Material.REDSTONE_TORCH_ON) {
                updateMana(event.getPlayer().getName());
                if (removeMana(event.getPlayer().getName(),10)) {
                    Fireball fire = event.getPlayer().launchProjectile(Fireball.class);
                    fire.setVelocity(fire.getVelocity().multiply(5));
                    event.setCancelled(true);
                }
            }
        }
    }

    public int getMaxMana(String player) {
        if (database == null) {
            return 0;
        }

        if (player == null) {
            return 0;
        }

        List<PlayerMagic> magicList = (List<PlayerMagic>)database.select(PlayerMagic.class,"player = '" + database.makeSafe(player) + "'");

        PlayerMagic pm;

        if (magicList.size() == 0) {
            pm = new PlayerMagic();
            pm.setPlayer(player);
            pm.setMaxMana(defaultMaxMana);
            pm.save(database);
        } else {
            pm = magicList.get(0);
        }

        Double max = Math.floor(pm.getMaxMana());

        return max.intValue();
    }

    private void updateMaxMana(String player, int amountUsed) {
        if (database == null) {
            return;
        }

        if (player == null) {
            return;
        }

        List<PlayerMagic> magicList = (List<PlayerMagic>)database.select(PlayerMagic.class,"player = '" + database.makeSafe(player) + "'");

        PlayerMagic pm;

        if (magicList.size() == 0) {
            pm = new PlayerMagic();
            pm.setPlayer(player);
            pm.setMaxMana(defaultMaxMana);
            pm.save(database);
        } else {
            pm = magicList.get(0);
        }

        double amount = amountUsed;
        amount /= increaseFactor;

        pm.setMaxMana(pm.getMaxMana() + amount);
        pm.save(database);
    }

    private boolean removeMana(String player, int amount) {
        if (player == null) {
            return false;
        }
        if (!mana.containsKey(player)) {
            mana.put(player,getMaxMana(player));
        }

        int count = mana.get(player);

        if (count < amount) {
            getServer().getPlayer(player).sendMessage("You don't have " + amount + " mana. You have " + count + " mana.");
            return false;
        }

        count -= amount;

        updateMaxMana(player, amount);

        int max = getMaxMana(player);
        getServer().getPlayer(player).sendMessage("You have " + count + "/" + max + " mana.");

        mana.put(player, count);

        return true;
    }

    private boolean addMana(String player, int amount) {
        if (player == null) {
            return false;
        }

        int max = getMaxMana(player);

        if (!mana.containsKey(player)) {
            mana.put(player,max);
        }

        int count = mana.get(player);

        count += amount;

        if (amount+count > max) {
            count = max;
        }

        mana.put(player,count);

        return true;
    }

    private void updateMana(String player) {
        if (player == null) {
            return;
        }

        Date now = new Date();

        if (!lastChecked.containsKey(player)) {
            lastChecked.put(player,now);
        }

        Date checked = lastChecked.get(player);

        long diff = now.getTime()-checked.getTime();
        long seconds = diff/1000;

        seconds /= secondsPerMana; // once every x seconds

        addMana(player, (int) seconds);

        lastChecked.put(player,now);
    }

    public ItemStack getAlchemyItem(ItemStack from) {
        ItemStack to = null;

        int totalAmount = 0;

        if (from.getType() == Material.COBBLESTONE) {
            to = new ItemStack(Material.DIRT,4);
            totalAmount = from.getMaxStackSize();
        } else if (from.getType() == Material.DIRT) {
            to = new ItemStack(Material.COAL,4);
            totalAmount = from.getMaxStackSize();
        } else if (from.getType() == Material.COAL) {
            to = new ItemStack(Material.IRON_INGOT,4);
            totalAmount = from.getMaxStackSize();
        } else if (from.getType() == Material.IRON_INGOT) {
            to = new ItemStack(Material.GOLD_INGOT,4);
            totalAmount = from.getMaxStackSize();
        } else if (from.getType() == Material.GOLD_INGOT) {
            to = new ItemStack(Material.DIAMOND,4);
            totalAmount = from.getMaxStackSize();
        } else if (from.getType() == Material.SEEDS) {
            to = new ItemStack(Material.INK_SACK,4);
            to.setDurability((short)3);
            totalAmount = from.getMaxStackSize();
        } else if (from.getType() == Material.INK_SACK && from.getDurability() == 3) {
            to = new ItemStack(Material.REDSTONE,4);
            totalAmount = from.getMaxStackSize();
        } else if (from.getType() == Material.REDSTONE) {
            to = new ItemStack(Material.GLOWSTONE_DUST,4);
            totalAmount = from.getMaxStackSize();
        } else if (from.getType() == Material.GLOWSTONE_DUST) {
            to = new ItemStack(Material.NETHER_STALK,4);
            totalAmount = from.getMaxStackSize();
        }


        if (to != null) {
            if (from.getAmount() < totalAmount) {
                to = new ItemStack(Material.AIR);
            }
        }

        return to;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        String playerName = "";

        if (sender instanceof Player) {
            playerName = ((Player)sender).getName();
        } else {
            playerName = "CONSOLE";
        }

        if (worlds != null && worlds.getPlayerWorldType(playerName) == Worlds.VANILLA) {
            return false;
        }

        String command = cmd.getName();

        if (command.equalsIgnoreCase("cast")) {
            if (permission == null || !permission.hasPermission(playerName,"magic_cast") || playerName.equalsIgnoreCase("CONSOLE")) {
                sender.sendMessage("You don't have permissions for this command");
                return true;
            }

            if (args.length > 0) {
                String subCommand = args[0];

                if (subCommand.equalsIgnoreCase("death")) {

                } else if ("platform".equalsIgnoreCase(subCommand)) {
                    Player p = (Player)sender;
                    Location l = p.getEyeLocation();

                    int y = l.getBlockY()+2;

                    boolean clear = true;

                    for (int i=y;i<y+5;i++) {
                        Block b = p.getWorld().getBlockAt(l.getBlockX(),i,l.getBlockZ());

                        if (b.getType() != Material.AIR) {
                            clear = false;
                        }
                    }

                    if (clear) {
                        updateMana(p.getName());
                        if (!removeMana(p.getName(),20)) {
                            return true;
                        }

                        Block b = p.getWorld().getBlockAt(l.getBlockX(),l.getBlockY()+6,l.getBlockZ());
                        b.setType(Material.COBBLESTONE);
                        b = p.getWorld().getBlockAt(l.getBlockX()-1,l.getBlockY()+6,l.getBlockZ()-1);
                        b.setType(Material.COBBLESTONE);
                        b = p.getWorld().getBlockAt(l.getBlockX()-1,l.getBlockY()+6,l.getBlockZ());
                        b.setType(Material.COBBLESTONE);
                        b = p.getWorld().getBlockAt(l.getBlockX()-1,l.getBlockY()+6,l.getBlockZ()+1);
                        b.setType(Material.COBBLESTONE);
                        b = p.getWorld().getBlockAt(l.getBlockX(),l.getBlockY()+6,l.getBlockZ()+1);
                        b.setType(Material.COBBLESTONE);
                        b = p.getWorld().getBlockAt(l.getBlockX()+1,l.getBlockY()+6,l.getBlockZ()+1);
                        b.setType(Material.COBBLESTONE);
                        b = p.getWorld().getBlockAt(l.getBlockX()+1,l.getBlockY()+6,l.getBlockZ());
                        b.setType(Material.COBBLESTONE);
                        b = p.getWorld().getBlockAt(l.getBlockX()+1,l.getBlockY()+6,l.getBlockZ()-1);
                        b.setType(Material.COBBLESTONE);
                        b = p.getWorld().getBlockAt(l.getBlockX()+1,l.getBlockY()+6,l.getBlockZ());
                        b.setType(Material.COBBLESTONE);
                        b = p.getWorld().getBlockAt(l.getBlockX(),l.getBlockY()+6,l.getBlockZ()-1);
                        b.setType(Material.COBBLESTONE);

                        Location l2 = new Location(p.getWorld(),l.getX(),l.getY()+7,l.getZ());
                        p.teleport(l2);
                    } else {
                        sender.sendMessage("Area above you is not clear.");
                    }
                } else if ("alchemy".equalsIgnoreCase(subCommand)) {
                    Player p = (Player)sender;

                    ItemStack stack = p.getItemInHand();

                    ItemStack to = getAlchemyItem(stack);
                    if (to == null) {
                        sender.sendMessage("You can't alchemy that.");
                        return true;
                    } else if (to.getType() == Material.AIR) {
                        sender.sendMessage("You don't have enough to alchemy that.");
                        return true;
                    }

                    updateMana(p.getName());
                    if (!removeMana(p.getName(),20)) {
                        return true;
                    }

                    String name1 = stack.getType().name();
                    String name2 = to.getType().name();

                    if (itemName != null) {
                        name1 = itemName.getItemName(stack);
                        name2 = itemName.getItemName(to);
                    }

                    sender.sendMessage("Turning your stack of " + stack.getAmount() + " " + name1 + " into " + to.getAmount() + " " + name2);

                    p.setItemInHand(to);

                    sender.sendMessage("Alchemy complete.");
                } else {
                    sender.sendMessage("Unknown spell: " + subCommand);
                }
            } else {
                sender.sendMessage("/cast <death|platform|alchemy>");
            }
        } else if ("checkmana".equalsIgnoreCase(command)) {
            Player p = (Player)sender;

            updateMana(p.getName());
            removeMana(p.getName(),0);
        } else {
            return false;
        }

        return true;
    }
}
