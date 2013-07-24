package com.thepastimers.Creative;

import com.thepastimers.Database.Database;
import com.thepastimers.Permission.Permission;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

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

    String allPerms = "creative_all";
    String commandPerm = "creative_command";

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
        if (event.getBlockPlaced().getType() == Material.TNT) {
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
        if (event.getBlock().getType() == Material.TNT) {
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
            List<Entity> entityList = event.getEntity().getNearbyEntities(6,6,6);
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
    public void playerOpen(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        Player p = event.getPlayer();

        if (block == null) return;

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            return;
        }

        if (block.getType() == Material.CHEST || block.getType() == Material.FURNACE || block.getType() == Material.BURNING_FURNACE
                || block.getType() == Material.ENDER_CHEST || block.getType() == Material.TRAPPED_CHEST) {
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
                }
            } else {
                sender.sendMessage("/gm <0|1>");
            }
        } else {
            return false;
        }

        return true;
    }
}
