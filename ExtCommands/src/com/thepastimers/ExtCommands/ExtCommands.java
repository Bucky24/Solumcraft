package com.thepastimers.ExtCommands;

//import com.thepastimers.Chat.Chat;
import com.thepastimers.Chat.CommandData;
import com.thepastimers.Coord.Coord;
import com.thepastimers.Coord.CoordData;
//import com.thepastimers.ItemName.ItemName;
//import com.thepastimers.Metrics.Metrics;
import com.thepastimers.Permission.Permission;
import com.thepastimers.Rank.Rank;
//import com.thepastimers.Worlds.Worlds;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
//import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
//import org.kitteh.vanish.VanishManager;
//import org.kitteh.vanish.VanishPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: derp
 * Date: 10/13/12
 * Time: 8:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExtCommands extends JavaPlugin implements Listener {
    Permission permission;
    //Metrics metrics;
    Rank rank;
    //Chat chat;
    //VanishPlugin vanishNoPacket;
    //VanishManager manager;
    //Worlds worlds;
    //ItemName itemName;
    Coord coord;

    @Override
    public void onEnable() {
        getLogger().info("ExtCommands init");

        getServer().getPluginManager().registerEvents(this,this);

        permission = (Permission)getServer().getPluginManager().getPlugin("Permission");

        if (permission == null) {
            getLogger().warning("Unable to load Permission plugin.");
        } else {
            permission.registerPermission("command_seed",2);
            permission.registerPermission("command_dragon",1);
            permission.registerPermission("command_fly",2);
        }

        /*metrics = (Metrics)getServer().getPluginManager().getPlugin("Metrics");

        if (metrics == null) {
            getLogger().warning("Unable to load Metrics plugin.");
        }*/

        rank = (Rank)getServer().getPluginManager().getPlugin("Rank");
        if (rank == null) {
            getLogger().warning("Unable to load Rank plugin.");
        }

        /*chat = (Chat)getServer().getPluginManager().getPlugin("Chat");
        if (chat == null) {
            getLogger().warning("Unable to load Chat plugin.");
        } else {
            chat.registerCommand("setTitle",ExtCommands.class,this);
        }*/

        /*worlds = (Worlds)getServer().getPluginManager().getPlugin("Worlds");
        if (worlds == null) {
            getLogger().warning("Unable to load Worlds plugin. Some functionality may not be available.");
        }*/

        /*itemName = (ItemName)getServer().getPluginManager().getPlugin("ItemName");
        if (itemName == null) {
            getLogger().warning("Unable to load ItemName plugin. Some functionality may not be available.");
        }*/

        /*vanishNoPacket = (VanishPlugin)getServer().getPluginManager().getPlugin("VanishNoPacket");
        if (vanishNoPacket == null) {
            getLogger().warning("Unable to load VanishNoPacket plugin.");
        } else {
            manager = vanishNoPacket.getManager();
        }*/

        coord = (Coord)getServer().getPluginManager().getPlugin("Coord");
        if (coord == null) {
            getLogger().warning("Unable to load Coord plugin.");
        }

        getLogger().info("ExtCommands init complete");
    }

    @Override
    public void onDisable() {
        getLogger().info("ExtCommands disabled");
    }

    @EventHandler
    public void explosion(EntityExplodeEvent event) {
        if (event.getEntityType() == EntityType.CREEPER) {
            event.blockList().clear();
        }
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        if (p == null || permission == null || !permission.hasPermission(p.getName(),"command_warn_break")) {
            return;
        }

        if (!event.isCancelled()) {
            ItemStack is = p.getItemInHand();
            if (is.getType() == Material.WOOD_AXE || is.getType() == Material.WOOD_HOE || is.getType() == Material.WOOD_PICKAXE || is.getType() == Material.WOOD_SPADE ||
                is.getType() == Material.STONE_AXE || is.getType() == Material.STONE_HOE || is.getType() == Material.STONE_PICKAXE || is.getType() == Material.STONE_SPADE ||
                is.getType() == Material.IRON_AXE || is.getType() == Material.IRON_HOE || is.getType() == Material.IRON_PICKAXE || is.getType() == Material.IRON_SPADE ||
                is.getType() == Material.GOLD_AXE || is.getType() == Material.GOLD_HOE || is.getType() == Material.GOLD_PICKAXE || is.getType() == Material.GOLD_SPADE ||
                is.getType() == Material.DIAMOND_AXE || is.getType() == Material.DIAMOND_HOE || is.getType() == Material.DIAMOND_PICKAXE || is.getType() == Material.DIAMOND_SPADE) {
                short durab = is.getDurability();
                durab ++;
                short max = is.getType().getMaxDurability();
                if (durab >= max-3 && durab <= max) {
                    p.sendMessage(ChatColor.RED + "Warning, your " + is.getType().name() + " will break soon! (" + durab + "/" + max + ")");
                }
            }
        }
    }

    /*@EventHandler
    public void pourLiquid(PlayerBucketEmptyEvent event) {
        Player p = event.getPlayer();
        ItemStack is = p.getItemInHand();
        //p.sendMessage("Amount in hand: " + is.getAmount());
        if (is.getAmount() > 1) {
            if (itemName == null) {
                p.sendMessage(ChatColor.RED + "This action is not currently possible");
                event.setCancelled(true);
                return;
            }
            if (itemName.giveItem(p,"BUCKET",1)) {
                is.setAmount(is.getAmount()-1);
                event.setItemStack(is);
            } else {
                p.sendMessage(ChatColor.RED + "Your inventory is full, no room for the empty bucket! (The full buckets are still in your inventory-due to a client glitch, they may appear to have vanished)");
                event.setCancelled(true);
                p.setItemInHand(is);
            }
        }
    }*/

    /*public void handleCommand(CommandData data) {
        String response = "";
        String command = data.getCommand();
        if ("setTitle".equalsIgnoreCase(command)) {
            if (permission == null || !permission.hasPermission(data.getPlayer(),"title_self")) {
                data.setResponse(":red:You do not have permission to use this command (title_self)");
                return;
            }

            if (data.getArgumentArray().length > 0) {
                StringBuilder title = new StringBuilder();
                for (int i=0;i<data.getArgumentArray().length;i++) {
                    title.append(data.getArgumentArray()[i]);
                    if (i < data.getArgumentArray().length-1) {
                        title.append(" ");
                    }
                }

                if (rank == null) {
                    data.setResponse("This command is not currently available");
                    return;
                }

                if (!rank.setTitle(data.getPlayer(),title.toString())) {
                    response = ":red:Unable to set title";
                } else {
                    response = ":green:Title changed to " + title.toString();
                }
            } else {
                response = ":red:/setTitle <title>";
            }
        } else {
            response = ":red:Unknown command";
        }
        data.setResponse(response);
    }*/

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        String playerName = "";

        if (sender instanceof Player) {
            playerName = ((Player)sender).getName();
        } else {
            playerName = "CONSOLE";
        }

        /*if (worlds != null && worlds.getPlayerWorldType(playerName) == Worlds.VANILLA) {
            return false;
        }*/

        String command = cmd.getName();

        if (command.equalsIgnoreCase("spawn")) {
            if (permission == null || !permission.hasPermission(playerName,"command_spawn") || playerName.equalsIgnoreCase("CONSOLE")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command (command_spawn)");
                return true;
            }

            Player p = getServer().getPlayer(playerName);

            World w = p.getWorld();
            //getLogger().info(w.getName());
            /*if (worlds != null) {
                if (worlds.getWorldType(w.getName()) == Worlds.NORMAL) {
                    w = getServer().getWorld("world");
                }
            }*/

            p.teleport(w.getSpawnLocation());

            sender.sendMessage("Teleporting you to spawn.");
        } else if (command.equalsIgnoreCase("tpe")) {
            if (permission == null || !permission.hasPermission(playerName,"command_tpe")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command (command_tpe)");
                return true;
            }

            if (args.length > 2) {
                int x,y,z;
                try {
                    x = Integer.parseInt(args[0]);
                    y = Integer.parseInt(args[1]);
                    z = Integer.parseInt(args[2]);
                } catch(NumberFormatException e) {
                    sender.sendMessage("Illegal arguments");
                    return true;
                }

                if (args.length > 3) {
                    playerName = args[3];
                }

                if ("CONSOLE".equals(playerName)) {
                    sender.sendMessage(ChatColor.RED + "Console cannot access this command");
                    return true;
                }

                Player p = getServer().getPlayer(playerName);
                if (p == null) {
                    sender.sendMessage("This player does not exist");
                    return true;
                }

                Location l = new Location(p.getWorld(),x,y,z);
                p.teleport(l);

                sender.sendMessage("Teleporting!");
            } else {
                sender.sendMessage("/tpe <x> <y> <z>");
            }
        } else if (command.equalsIgnoreCase("setspawn")) {
            if (permission == null || !permission.hasPermission(playerName,"command_setspawn") || playerName.equalsIgnoreCase("CONSOLE")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command (command_setspawn)");
                return true;
            }

            Player p = getServer().getPlayer(playerName);

            Location l = p.getLocation();
            p.getWorld().setSpawnLocation(l.getBlockX(),l.getBlockY(),l.getBlockZ());

            sender.sendMessage("Set spawn for this world to (" + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ() + ")");
        }/* else if (command.equalsIgnoreCase("clear")) {
            if (permission == null || !permission.hasPermission(playerName,"command_clear")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command (command_clear)");
                return true;
            }

            if (args.length > 0) {
                String subCommand = args[0];

                if ("mobs".equalsIgnoreCase(subCommand)) {
                    if (args.length > 1) {
                        String world = args[1];

                        World w = getServer().getWorld(world);
                        if (w == null) {
                            sender.sendMessage(ChatColor.RED + "World '" + world + "' does not exist.");
                            return true;
                        }

                        List<Entity> entities = getServer().getWorld(world).getEntities();

                        int count = 0;

                        for (Entity entity : entities) {
                            if (entity.getType() == EntityType.BLAZE ||
                                    entity.getType() == EntityType.ZOMBIE ||
                                    entity.getType() == EntityType.CAVE_SPIDER ||
                                    entity.getType() == EntityType.CREEPER ||
                                    entity.getType() == EntityType.ENDERMAN ||
                                    entity.getType() == EntityType.GHAST ||
                                    entity.getType() == EntityType.MAGMA_CUBE ||
                                    entity.getType() == EntityType.PIG_ZOMBIE ||
                                    entity.getType() == EntityType.SILVERFISH ||
                                    entity.getType() == EntityType.SKELETON ||
                                    entity.getType() == EntityType.SPIDER ||
                                    entity.getType() == EntityType.WITCH) {
                                entity.remove();
                                count ++;
                            }
                        }

                        sender.sendMessage("Removed " + count + " entities.");
                    } else {
                        sender.sendMessage("/clear mobs <world>");
                    }
                } else if ("bosses".equalsIgnoreCase(subCommand)) {
                    if (args.length > 1) {
                        String world = args[1];

                        World w = getServer().getWorld(world);
                        if (w == null) {
                            sender.sendMessage(ChatColor.RED + "World '" + world + "' does not exist.");
                            return true;
                        }

                        List<Entity> entities = getServer().getWorld(world).getEntities();

                        int count = 0;

                        for (Entity entity : entities) {
                            if (entity.getType() == EntityType.WITHER || entity.getType() == EntityType.ENDER_DRAGON) {
                                entity.remove();
                                count ++;
                            }
                        }

                        sender.sendMessage("Removed " + count + " entities.");
                    } else {
                        sender.sendMessage("/clear bosses <world>");
                    }
                } else if ("cats".equalsIgnoreCase(subCommand)) {
                    if (args.length > 1) {
                        String world = args[1];

                        World w = getServer().getWorld(world);
                        if (w == null) {
                            sender.sendMessage(ChatColor.RED + "World '" + world + "' does not exist.");
                            return true;
                        }

                        List<Entity> entities = getServer().getWorld(world).getEntities();

                        int count = 0;

                        for (Entity entity : entities) {
                            if (entity.getType() == EntityType.OCELOT) {
                                entity.remove();
                                count ++;
                            }
                        }

                        sender.sendMessage("Removed " + count + " entities.");
                    } else {
                        sender.sendMessage("/clear cats <world>");
                    }
                } else if ("chickens".equalsIgnoreCase(subCommand)) {
                    if (args.length > 1) {
                        String world = args[1];

                        World w = getServer().getWorld(world);
                        if (w == null) {
                            sender.sendMessage(ChatColor.RED + "World '" + world + "' does not exist.");
                            return true;
                        }

                        List<Entity> entities = getServer().getWorld(world).getEntities();

                        int count = 0;

                        for (Entity entity : entities) {
                            if (entity.getType() == EntityType.CHICKEN) {
                                entity.remove();
                                count ++;
                            }
                        }

                        sender.sendMessage("Removed " + count + " entities.");
                    } else {
                        sender.sendMessage("/clear chickens <world>");
                    }
                }
            } else {
                sender.sendMessage("/clear <mobs|bosses|cats|chickens>");
            }
        }*//* else if (command.equalsIgnoreCase("itemCheck")) {
            if (permission == null || !permission.hasPermission(playerName,"command_item_check")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command (command_item_check)");
                return true;
            }

            Player p  = (Player)sender;
            ItemStack s = p.getItemInHand();
            if (s == null) {
                sender.sendMessage(ChatColor.GREEN + "You have nothing in your hand");
            } else {
                sender.sendMessage(ChatColor.GREEN + "The item in your hand is: " + s.getType().name() + " (" + s.getType().getId() + "), durability " + s.getDurability() + "/" + s.getType().getMaxDurability());
                sender.sendMessage(ChatColor.GREEN + "ItemName plugin knows it as: " + itemName.getItemName(s));
            }
        }*//* else if ("opCheck".equalsIgnoreCase(command)) {
            if (permission == null || !permission.hasPermission(playerName,"command_op_check")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command (command_op_check)");
                return true;
            }

            if (args.length > 0) {
                String player = args[0];
                Player p = getServer().getPlayer(player);

                boolean broadcast = false;
                if (args.length > 1) {
                    if ("broadcast".equalsIgnoreCase(args[1])) {
                        broadcast = true;
                        sender.sendMessage("Broadcasting op result to server");
                    }
                }

                if (p == null) {
                    sender.sendMessage(ChatColor.RED + player + " is not a valid player.");
                } else {
                    String message = ChatColor.GREEN + "[OPCHECK]: " + ChatColor.WHITE + sender.getName() + " has run op check on " + player;
                    if (!broadcast) sender.sendMessage(message);
                    if (broadcast) getServer().broadcastMessage(message);
                    if (p.isOp()) {
                        message = ChatColor.GREEN + "[OPCHECK]: " + ChatColor.WHITE + player + ChatColor.GREEN + " is " + ChatColor.WHITE + "an op";
                        if (!broadcast) sender.sendMessage(message);
                        if (broadcast) getServer().broadcastMessage(message);
                    } else {
                        message = ChatColor.GREEN + "[OPCHECK]: " + ChatColor.WHITE + player + ChatColor.RED + " is not " + ChatColor.WHITE + "an op";
                        if (!broadcast) sender.sendMessage(message);
                        if (broadcast) getServer().broadcastMessage(message);
                    }
                }

            } else {
                sender.sendMessage(ChatColor.RED + "/opCheck <player> <broadcast>");
            }
        }*/ else if ("pot".equalsIgnoreCase(command)) {
            if (permission == null || !permission.hasPermission(playerName,"command_pot")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command (command_pot)");
                return true;
            }

            if ("Kurama_09".equalsIgnoreCase(playerName)) {
                Player p = (Player)sender;
                p.kickPlayer("POT POT POT POT POT");
            } else {
                sender.sendMessage("This command added for Kurama_09");
            }
        } else if ("setTitle".equalsIgnoreCase(command)) {
            if (permission == null || !permission.hasPermission(playerName,"title_self")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command (title_self)");
                return true;
            }

            if (args.length > 0) {
                StringBuilder title = new StringBuilder();
                for (int i=0;i<args.length;i++) {
                    title.append(args[i]);
                    if (i < args.length-1) {
                        title.append(" ");
                    }
                }

                if (rank == null) {
                    sender.sendMessage(ChatColor.RED + "This command is not currently available");
                    return true;
                }

                Player p = (Player)sender;
                playerName = p.getUniqueId().toString();

                if (!rank.setTitle(playerName,title.toString())) {
                    sender.sendMessage(ChatColor.RED + "Unable to set title");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "/setTitle <title>");
            }
        }/* else if ("hide".equalsIgnoreCase(command)) {
            if (permission == null || !permission.hasPermission(playerName,"command_vanish") || "CONSOLE".equalsIgnoreCase(playerName)) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command (command_vanish)");
                return true;
            }

            Player p = (Player)sender;
            if (manager != null) {
                manager.toggleVanish(p);
            }
        }*//* else if ("spawnDragon".equalsIgnoreCase(command)) {
            if (permission == null || !permission.hasPermission(playerName,"command_dragon") || "CONSOLE".equalsIgnoreCase(playerName)) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command (command_dragon)");
                return true;
            }

            Player p = (Player)sender;
            Location l = p.getLocation();

            l.getWorld().spawnEntity(l,EntityType.ENDER_DRAGON);
        }*//* else if ("spawnCrystal".equalsIgnoreCase(command)) {
            if (permission == null || !permission.hasPermission(playerName,"command_dragon") || "CONSOLE".equalsIgnoreCase(playerName)) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command (command_dragon)");
                return true;
            }
            if (coord == null) {
                sender.sendMessage("This functionality is not currently available.");
                return true;
            }

            List<CoordData> coords = coord.popCoords(playerName,1);

            if (coords.size() < 1) {
                sender.sendMessage("You must have 1 coord set in order to do this");
                return true;
            }

            Player p = (Player)sender;
            World w = p.getWorld();
            CoordData cd = coords.get(0);
            double x = Math.floor(cd.getX());
            x += 0.5d;
            double y = Math.floor(cd.getY());
            y += 0.5d;
            double z = Math.floor(cd.getZ());
            z += 0.5d;
            Location l = new Location(w,x,y,z);

            w.spawnEntity(l,EntityType.ENDER_CRYSTAL);
        }*//* else if ("stack".equalsIgnoreCase(command)) {
            if (permission == null || !permission.hasPermission(playerName,"command_stack") || "CONSOLE".equalsIgnoreCase(playerName)) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command (command_stack)");
                return true;
            }

            if (itemName == null) {
                sender.sendMessage(ChatColor.RED + "This functionality is not currently available");
                return true;
            }

            Player p = (Player)sender;
            ItemStack is = p.getInventory().getItemInHand();
            String name = itemName.getItemName(is);
            int count = itemName.countInInventory(name,p.getName());
            //if (name.equalsIgnoreCase("LAVA_BUCKET") || name.equalsIgnoreCase("WATER_BUCKET")) count = -1;
            if (count > 64) {
                sender.sendMessage(ChatColor.RED + "Can't stack over 64 at this time");
                return true;
            }

            if (count == -1) {
                sender.sendMessage(ChatColor.RED + "Cannot stack this item");
                return true;
            }
            itemName.takeItem(p,name,count);
            ItemStack newItem = itemName.getItemFromName(name);
            newItem.setAmount(count);
            // find an opening
            ItemStack[] items = p.getInventory().getContents();
            for (int i=0;i<items.length;i++) {
                ItemStack ii = items[i];
                if (ii == null) {
                    items[i] = newItem;
                    break;
                }
            }
            p.getInventory().setContents(items);
        }*//* else if ("fly".equalsIgnoreCase(command)) {
            if (permission == null || !permission.hasPermission(playerName,"command_fly") || "CONSOLE".equalsIgnoreCase(playerName)) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command (command_fly)");
                return true;
            }

            Player p = (Player)sender;
            if (p.getAllowFlight()) {
                p.setAllowFlight(false);
                p.sendMessage(ChatColor.BLUE + "Flying is now disabled");
            } else {
                p.setAllowFlight(true);
                p.sendMessage(ChatColor.BLUE + "Flying is now enabled");
            }
        }*/ else if ("seed".equalsIgnoreCase(command)) {
            if (permission == null || !permission.hasPermission(playerName,"command_seed") || "CONSOLE".equalsIgnoreCase(playerName)) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command (command_seed)");
                return true;
            }

            Player p = (Player)sender;
            World w = p.getWorld();
            sender.sendMessage(ChatColor.BLUE + "Seed for " + w.getName() + ": " + w.getSeed());

        }/* else if ("killzig".equalsIgnoreCase(command)) {
            if (permission == null || !permission.hasPermission(playerName,"command_killzig") || "CONSOLE".equalsIgnoreCase(playerName)) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command (command_killzig)");
                return true;
            }

            sender.sendMessage(ChatColor.LIGHT_PURPLE + "Zigx89 has died at your hand!");
        }*/ else {
            return false;
        }

        return true;
    }
}
