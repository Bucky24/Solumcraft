package com.thepastimers.Creative;

import com.thepastimers.Database.Database;
import com.thepastimers.Permission.Permission;
import com.thepastimers.Plot.Plot;
import com.thepastimers.Plot.PlotData;
import com.thepastimers.Plot.PlotPerms;
import com.thepastimers.Worlds.Worlds;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 5/3/13
 * Time: 8:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class Creative extends JavaPlugin implements Listener {
    Database database;
    Permission permission;
    Plot plot;
    Worlds worlds;

    String allPerms = "creative_all";
    String commandPerm = "creative_command";
    String creativePlot = "creative_plot_only";
    String economyCreative = "creative_economy";

    @Override
    public void onEnable() {
        getLogger().info("Creative init");

        getServer().getPluginManager().registerEvents(this,this);

        database = (Database)getServer().getPluginManager().getPlugin("Database");
        if (database == null) {
            getLogger().warning("Unable to load Database module. Some functionality may not be available.");
        }

        permission = (Permission)getServer().getPluginManager().getPlugin("Permission");
        if (permission == null) {
            getLogger().warning("Unable to load Permission module. Some functionality may not be available.");
        } else {
            permission.registerPermission(allPerms,2);
            permission.registerPermission(commandPerm,2);
            permission.registerPermission(creativePlot,2);
            permission.registerPermission(economyCreative,2);
        }

        plot = (Plot)getServer().getPluginManager().getPlugin("Plot");
        if (plot == null) {
            getLogger().warning("Unable to load Plot module. Some functionality may not be available.");
        } else {
            getLogger().info("Registering plot handlers");
            plot.registerPlotLeave(Creative.class,this);
        }

        worlds = (Worlds)getServer().getPluginManager().getPlugin("Worlds");
        if (worlds == null) {
            getLogger().warning("Unable to load Worlds plugin. Some functionality may not be available.");
        }

        getLogger().info("Creative loaded");
    }

    @Override
    public void onDisable() {
        getLogger().info("Creative disable");
    }

    @EventHandler
    public void playerLogin(PlayerLoginEvent event) {
        Player p = event.getPlayer();
        String playerName = p.getName();
        if (permission == null || !permission.hasPermission(playerName,commandPerm)) {
            p.setGameMode(GameMode.SURVIVAL);
            getLogger().info("Setting " + p.getName() + " to survival mode");
        }
    }

    @EventHandler
    public void playerDropItem(PlayerDropItemEvent event) {
        Player p = event.getPlayer();
        if (p.getGameMode() == GameMode.CREATIVE) {
            if (permission == null || !permission.hasPermission(p.getName(),allPerms)) {
                Item i = event.getItemDrop();
                i.remove();
            }
        }
    }

    @EventHandler
    public void placeBlock(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        if (event.getBlockPlaced().getType() == Material.BEDROCK) {
            if (permission == null || !permission.hasPermission(p.getName(),allPerms)) {
                p.sendMessage(ChatColor.RED + "You do not have permission to do this (" + allPerms + ")");
                event.setCancelled(true);
            }
        }
        if (event.getBlockPlaced().getType() == Material.TNT && p.getGameMode() == GameMode.CREATIVE) {
            if (permission == null || !permission.hasPermission(p.getName(),allPerms)) {
                p.sendMessage(ChatColor.RED + "You do not have permission to do this (" + allPerms + ")");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void breakBlock(BlockBreakEvent event) {
        Player p = event.getPlayer();
        if (event.getBlock().getType() == Material.BEDROCK) {
            if (permission == null || !permission.hasPermission(p.getName(),allPerms)) {
                p.sendMessage(ChatColor.RED + "You do not have permission to do this (" + allPerms + ")");
                event.setCancelled(true);
            }
        }
        if (event.getBlock().getType() == Material.TNT && p.getGameMode() == GameMode.CREATIVE) {
            if (permission == null || !permission.hasPermission(p.getName(),allPerms)) {
                p.sendMessage(ChatColor.RED + "You do not have permission to do this (" + allPerms + ")");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void creatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG
                || event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.BUILD_IRONGOLEM
                || event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.BUILD_SNOWMAN
                || event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.BUILD_WITHER) {
            List<Entity> entityList = event.getEntity().getNearbyEntities(20,20,20);
            for (Entity e : entityList) {
                if (e.getType() == EntityType.PLAYER) {
                    Player p = (Player)e;
                    if (p.getGameMode() == GameMode.CREATIVE) {
                        if (permission == null || !permission.hasPermission(p.getName(),allPerms)) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void playerThrow(PlayerEggThrowEvent event) {
        LivingEntity le = (LivingEntity)event.getEgg().getShooter();
        if (le instanceof Player) {
            Player p = (Player)le;
            if (event.getHatchingType() != EntityType.CHICKEN) {
                if (p.getGameMode() == GameMode.CREATIVE) {
                    if (permission == null || !permission.hasPermission(p.getName(),allPerms)) {
                        event.setHatching(false);
                    }
                }
            }
        }
    }

    @EventHandler
    public void playerOpen(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        Player p = event.getPlayer();

        if (block == null) return;

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            return;
        }

        if (block.getType() == Material.CHEST || block.getType() == Material.FURNACE || block.getType() == Material.BURNING_FURNACE
                || block.getType() == Material.ENDER_CHEST || block.getType() == Material.TRAPPED_CHEST
                || block.getType() == Material.DISPENSER || block.getType() == Material.HOPPER
                || block.getType() == Material.HOPPER_MINECART || block.getType() == Material.DROPPER) {
            if (p.getGameMode() == GameMode.CREATIVE) {
                if (permission == null || !permission.hasPermission(p.getName(),allPerms)) {
                    p.sendMessage(ChatColor.RED + "You cannot interact with that while in creative mode (" + allPerms + ")");
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void playerInteract(PlayerInteractEntityEvent event) {
        Entity e = event.getRightClicked();
        Player p = event.getPlayer();

        if (e == null) return;
        if (e.getType() == EntityType.HORSE) {
            if (p.getGameMode() == GameMode.CREATIVE) {
                if (permission == null || !permission.hasPermission(p.getName(),allPerms)) {
                    p.sendMessage(ChatColor.RED + "You cannot interact with that while in creative mode (" + allPerms + ")");
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void playerPickup(PlayerPickupItemEvent event) {
        Player p = event.getPlayer();
        if (p.getGameMode() == GameMode.CREATIVE) {
            if (permission == null || !permission.hasPermission(p.getName(),allPerms)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void playerDamaged(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player p = (Player)event.getDamager();
            if (p.getGameMode() == GameMode.CREATIVE) {
                if (permission == null || !permission.hasPermission(p.getName(),allPerms)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    public void handlePlotLeave(PlotData data, Player player) {
        String playerName = player.getName();
        if (permission == null) {
            player.setGameMode(GameMode.SURVIVAL);
            return;
        }

        if (permission.hasPermission(playerName,creativePlot)) {
            player.setGameMode(GameMode.SURVIVAL);
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

        if (command.equalsIgnoreCase("gm")) {
            if (args.length > 0) {
                String mode = args[0];
                Player p = (Player)sender;

                if ("1".equalsIgnoreCase(mode) && p.getGameMode() == GameMode.SURVIVAL) {
                    if (permission == null || !permission.hasPermission(playerName,commandPerm) || playerName.equalsIgnoreCase("CONSOLE")) {
                        sender.sendMessage("You do not have permissions to use this command (" + commandPerm + ")");
                        return true;
                    }

                    if (worlds.getWorldType(p.getWorld().getName()) == Worlds.ECONOMY) {
                        if (!permission.hasPermission(playerName,economyCreative)) {
                            sender.sendMessage(ChatColor.RED + "You cannot use creative in economy world (" + economyCreative + ")");
                            return true;
                        }
                    }
                    PlayerInventory inv = p.getInventory();

                    ItemStack[] contents = inv.getContents();

                    boolean empty = true;
                    for (ItemStack is : contents) {
                        if (is != null && is.getType() != Material.AIR) {
                            empty = false;
                            break;
                        }
                    }

                    for (ItemStack is : inv.getArmorContents()) {
                        if (is != null && is.getType() != Material.AIR) {
                            empty = false;
                            break;
                        }
                    }

                    if (permission.hasPermission(playerName,creativePlot)) {
                        if (plot == null) {
                            p.sendMessage(ChatColor.RED + "This action cannot be taken at this time");
                            return true;
                        }
                        PlotData pd = plot.plotAt(p.getLocation());
                        if (pd == null) {
                            p.sendMessage(ChatColor.RED + "You can only use /gm inside a plot");
                            return true;
                        }
                        if (plot.getPlotPerms(pd,playerName) < PlotPerms.OWNER) {
                            p.sendMessage(ChatColor.RED + "You can only use /gm inside a plot that you own");
                            return true;
                        }
                    }

                    if (!empty) {
                        if (permission == null || !permission.hasPermission(playerName,allPerms)) {
                            p.sendMessage(ChatColor.RED + "Please empty your inventory before activating creative mode.");
                            return true;
                        }
                    }

                    p.setGameMode(GameMode.CREATIVE);
                } else if ("0".equalsIgnoreCase(mode) && p.getGameMode() == GameMode.CREATIVE) {
                    if (permission == null || !permission.hasPermission(playerName,allPerms)) {
                        PlayerInventory inv = p.getInventory();
                        ItemStack[] itemList = inv.getContents();

                        for (int i=0;i<itemList.length;i++) {
                            itemList[i] = null;
                        }
                        inv.setContents(itemList);

                        itemList = inv.getArmorContents();

                        for (int i=0;i<itemList.length;i++) {
                            itemList[i] = null;
                        }
                        inv.setArmorContents(itemList);
                    }
                    p.setGameMode(GameMode.SURVIVAL);
                } else {
                    if (permission == null || !permission.hasPermission(playerName,"creative_other")) {
                        return true;
                    }
                    Player other = getServer().getPlayer(mode);
                    if (other == null) {
                        p.sendMessage(ChatColor.GREEN + "Player does not exist");
                    } else {
                        if (other.getGameMode() == GameMode.CREATIVE) {
                            p.sendMessage(ChatColor.GREEN + mode + " is in creative mode");
                        } else if (other.getGameMode() == GameMode.SURVIVAL) {
                            p.sendMessage(ChatColor.GREEN + mode + " is in survival mode");
                        } else {
                            p.sendMessage(ChatColor.GREEN + mode + " is in an unknown mode");
                        }
                    }
                }
            } else {
                sender.sendMessage("/gm <0|1>");
            }
        } else if ("gmcheck".equalsIgnoreCase(command)) {
            if (permission == null || !permission.hasPermission(playerName,"creative_check")) {
                sender.sendMessage("You do not have permissions to use this command (creative_check)");
                return true;
            }

            if (args.length > 0) {
                String player = args[0];
                Player p = getServer().getPlayer(player);
                if (p == null) {
                    p = getServer().getOfflinePlayer(player).getPlayer();
                }
                if (p == null) {
                    sender.sendMessage(ChatColor.RED + player + " has never played on this server");
                } else {
                    if (p.getGameMode() == GameMode.ADVENTURE) {
                        sender.sendMessage(ChatColor.BLUE + player + " is in adventure mode");
                    } else if (p.getGameMode() == GameMode.SURVIVAL) {
                        sender.sendMessage(ChatColor.BLUE + player + " is in survival mode");
                    } else if (p.getGameMode() == GameMode.CREATIVE) {
                        sender.sendMessage(ChatColor.BLUE + player + " is in creative mode");
                    } else {
                        sender.sendMessage(ChatColor.BLUE + player + " is in an unknown mode");
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + "/gmcheck <player>");
            }
        } else {
            return false;
        }

        return true;
    }
}
