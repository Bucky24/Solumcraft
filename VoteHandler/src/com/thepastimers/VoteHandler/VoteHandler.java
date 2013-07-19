package com.thepastimers.VoteHandler;

import com.thepastimers.Database.Database;
import com.thepastimers.ItemName.ItemName;
import com.thepastimers.Permission.Permission;
import com.thepastimers.Rank.Rank;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 3/25/13
 * Time: 8:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class VoteHandler extends JavaPlugin implements Listener {
    Database database;
    Rank rank;
    Permission permission;
    ItemName itemName;

    @Override
    public void onEnable() {
        getLogger().info("VoteHandler init");

        getServer().getPluginManager().registerEvents(this,this);
        database = (Database)getServer().getPluginManager().getPlugin("Database");
        if (database == null) {
            getLogger().warning("Cannot load Database plugin. Some functionality may not be available");
        }

        permission = (Permission)getServer().getPluginManager().getPlugin("Permission");
        if (permission == null) {
            getLogger().warning("Cannot load Permission plugin. Some functionality may not be available");
        }

        rank = (Rank)getServer().getPluginManager().getPlugin("Rank");

        if (rank == null) {
            getLogger().warning("Cannot load Rank plugin. Some functionality may not be available");
        }

        itemName = (ItemName)getServer().getPluginManager().getPlugin("ItemName");
        if (itemName == null) {
            getLogger().warning("Cannot load ItemName plugin. Some functionality may not be available");
        }

        getLogger().info("Table info: ");
        getLogger().info(VoteSettings.getTableInfo());
        getLogger().info(VoteCredits.getTableInfo());

        getLogger().info("VoteHandler init complete");
    }

    @Override
    public void onDisable() {
        getLogger().info("VoteHandler disabled.");
    }

    public String getReward(String player) {
        if (player == "null") {
            return "diamonds";
        }

        List<VoteSettings> settingsList = (List<VoteSettings>)database.select(VoteSettings.class,"player = '" + database.makeSafe(player) + "'");

        if (settingsList.size() == 0) {
            return "diamonds";
        }

        return settingsList.get(0).getReward();
    }

    public boolean setReward(String player, String reward) {
        if (player == null || reward == null) {
            return false;
        }

        if (!"diamonds".equalsIgnoreCase(reward) && !"credits".equalsIgnoreCase(reward)) {
            return false;
        }

        VoteSettings vs = null;
        List<VoteSettings> settingsList = (List<VoteSettings>)database.select(VoteSettings.class,"player = '" + database.makeSafe(player) + "'");

        if (settingsList.size() == 0) {
            vs = new VoteSettings();
            vs.setPlayer(player);
        } else {
            vs = settingsList.get(0);
        }
        vs.setReward(reward);
        return (vs.save(database));
    }

    @EventHandler
    public void onVotifierEvent(VotifierEvent event) {
        Vote v = event.getVote();

        creditItems(v.getUsername(),v.getServiceName());
    }

    private void creditItems(String user, String address) {
        if ("".equalsIgnoreCase(user)) {
            return;
        }
        getLogger().info("Got vote from " + user + " at " + address);

        Player p = getServer().getPlayer(user);

        if (p == null) {
            getLogger().warning("User " + user + " does not exist.");
        } else {
            String reward = getReward(user);
            if ("credits".equalsIgnoreCase(reward)) {
               creditCredits(p,address);
            } else {
                creditDiamonds(p,address);
            }
        }
    }

    private boolean creditCredits(Player p, String address) {
        if (p == null) {
            return false;
        }

        int creditsToGive = 1;

        VoteCredits vc = null;
        List<VoteCredits> voteCreditsList = (List<VoteCredits>)database.select(VoteCredits.class,"player = '" + database.makeSafe(p.getName()) + "'");
        if (voteCreditsList.size() > 0) {
            vc = voteCreditsList.get(0);
        } else {
            vc = new VoteCredits();
            vc.setPlayer(p.getName());
            vc.setCredits(0);
        }

        vc.setCredits(vc.getCredits() + creditsToGive);
        boolean result = vc.save(database);

        if (!result) {
            p.sendMessage(ChatColor.RED + "Unable to give you " + creditsToGive + " vote credit/s.");
            getLogger().info(p.getName() + " has not been credited " + creditsToGive + " vote credit/s");
        } else {
            getLogger().info(p.getName() + " has been given " + creditsToGive + " vote credit/s.");
            p.sendMessage(ChatColor.GREEN + "You have been awarded " + creditsToGive + " vote credit/s!");
            getServer().broadcastMessage(ChatColor.GREEN + p.getName() + " has been given " + creditsToGive + " vote credit/s for voting at " + address + "!");
        }

        return result;
    }

    private boolean creditDiamonds(Player p, String address) {
        PlayerInventory pi = p.getInventory();

        ItemStack is = null;

        int toCredit = 2;
        int toCreditBak = 2;

        ItemStack[] itemStacks = pi.getContents();

        for (int i=0;i<itemStacks.length;i++) {
            ItemStack stack = itemStacks[i];
            if (stack != null && stack.getType() == Material.DIAMOND) {
                if (stack.getAmount() < stack.getMaxStackSize()) {
                    if (stack.getAmount()+toCredit > stack.getMaxStackSize()) {
                        int credit = stack.getMaxStackSize()-stack.getAmount();
                        toCredit -= credit;
                        stack.setAmount(stack.getMaxStackSize());
                    } else {
                        stack.setAmount(stack.getAmount()+toCredit);
                        toCredit = 0;
                    }
                    itemStacks[i] = stack;
                    break;
                }
            }
        }

        if (toCredit > 0) {
            for (int i=0;i<itemStacks.length;i++) {
                ItemStack stack = itemStacks[i];
                if (stack == null) {
                    stack = new ItemStack(Material.DIAMOND,toCredit);
                    toCredit = 0;
                    itemStacks[i] = stack;
                    break;
                }
            }
        }

        pi.setContents(itemStacks);

        if (toCredit > 0) {
            p.sendMessage(ChatColor.RED + "Unable to give you " + toCredit + " diamond/s-your inventory is full!");
            getLogger().info(p.getName() + " has not been credited " + toCredit + " diamonds, inventory was full");
        } else {
            getLogger().info(p.getName() + " has been given " + toCreditBak + " diamonds.");
            p.sendMessage(ChatColor.GREEN + "You have been awarded " + toCreditBak +" diamonds!");
            getServer().broadcastMessage(ChatColor.GREEN + p.getName() + " has been given " + toCreditBak + " diamonds for voting at " + address + "!");
        }

        return (toCredit == 0);
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

        if (command.equalsIgnoreCase("testvote")) {
            if (permission == null || !permission.hasPermission(playerName,"vote_test") || playerName.equalsIgnoreCase("CONSOLE")) {
                sender.sendMessage("You don't have permissions for this command (vote_test)");
                return true;
            }

            if (args.length > 1) {
                String player = args[0];
                String address = args[1];

                sender.sendMessage("Running test credit for player=" + player + ", address=" + address);

                creditItems(player,address);
            } else {
                sender.sendMessage("/testvote <player> <address>");
            }
        } else if ("vote".equalsIgnoreCase(command)) {
            if (permission == null || !permission.hasPermission(playerName,"vote_vote") || playerName.equalsIgnoreCase("CONSOLE")) {
                sender.sendMessage(ChatColor.RED + "You don't have permissions for this command (vote_vote)");
                return true;
            }

            if (args.length > 0) {
                String subCommand = args[0];

                if ("setReward".equalsIgnoreCase(subCommand)) {
                    if (args.length > 1) {
                        String reward = args[1];

                        if (!setReward(playerName,reward)) {
                            sender.sendMessage(ChatColor.RED + "Unable to save reward preferences (" + reward + ")");
                        } else {
                            sender.sendMessage(ChatColor.GREEN + "Reward preferences set to " + reward);
                        }
                    } else {
                        sender.sendMessage("/vote setReward <diamonds|credits>");
                    }
                } else if ("redeem".equalsIgnoreCase(subCommand)) {
                    if (args.length > 1) {
                        String reward = args[1];

                        List<VoteCredits> voteCreditsList = (List<VoteCredits>)database.select(VoteCredits.class,"player = '" + database.makeSafe(playerName) + "'");
                        VoteCredits credits = null;
                        if (voteCreditsList.size() == 0) {
                            credits = new VoteCredits();
                            credits.setCredits(0);
                        } else {
                            credits = voteCreditsList.get(0);
                        }

                        if ("horse".equalsIgnoreCase(reward)) {
                            int need = 10;
                            if (credits.getCredits() < need) {
                                sender.sendMessage(ChatColor.RED + "You need " + need + " credits to get a horse egg. You have " + credits.getCredits());
                            } else {
                                credits.setCredits(credits.getCredits()-need);
                                if (!credits.save(database)) {
                                    sender.sendMessage(ChatColor.RED + "Unable to update your credits balance");
                                } else {
                                    if (itemName != null && itemName.giveItem((Player)sender,"HORSE_EGG",1)) {
                                        sender.sendMessage(ChatColor.GREEN + "You have been given a horse egg");
                                    } else {
                                        credits.setCredits(credits.getCredits()+need);
                                        credits.save(database);
                                        sender.sendMessage(ChatColor.RED + "Unable to give you a horse egg. Your credits have been refunded.");
                                    }
                                }
                            }
                        } else if ("cow".equalsIgnoreCase(reward)) {
                            int need = 10;
                            if (credits.getCredits() < need) {
                                sender.sendMessage(ChatColor.RED + "You need " + need + " credits to get a cow egg. You have " + credits.getCredits());
                            } else {
                                credits.setCredits(credits.getCredits()-need);
                                if (!credits.save(database)) {
                                    sender.sendMessage(ChatColor.RED + "Unable to update your credits balance");
                                } else {
                                    if (itemName != null && itemName.giveItem((Player)sender,"COW_EGG",1)) {
                                        sender.sendMessage(ChatColor.GREEN + "You have been given a cow egg");
                                    } else {
                                        credits.setCredits(credits.getCredits()+need);
                                        credits.save(database);
                                        sender.sendMessage(ChatColor.RED + "Unable to give you a cow egg. Your credits have been refunded.");
                                    }
                                }
                            }
                        } else if ("chiseled_block".equalsIgnoreCase(reward)) {
                            int need = 2;
                            if (credits.getCredits() < need) {
                                sender.sendMessage(ChatColor.RED + "You need " + need + " credits to get 5 chiseled stone blocks. You have " + credits.getCredits());
                            } else {
                                credits.setCredits(credits.getCredits()-need);
                                if (!credits.save(database)) {
                                    sender.sendMessage(ChatColor.RED + "Unable to update your credits balance");
                                } else {
                                    if (itemName != null && itemName.giveItem((Player)sender,"CHISELED_STONE",5)) {
                                        sender.sendMessage(ChatColor.GREEN + "You have been given 5 chiseled stone blocks");
                                    } else {
                                        credits.setCredits(credits.getCredits()+need);
                                        credits.save(database);
                                        sender.sendMessage(ChatColor.RED + "Unable to give you 5 chiseled stone blocks. Your credits have been refunded.");
                                    }
                                }
                            }
                        } else if ("villager".equalsIgnoreCase(reward)) {
                            int need = 20;
                            if (credits.getCredits() < need) {
                                sender.sendMessage(ChatColor.RED + "You need " + need + " credits to get a villager egg. You have " + credits.getCredits());
                            } else {
                                credits.setCredits(credits.getCredits()-need);
                                if (!credits.save(database)) {
                                    sender.sendMessage(ChatColor.RED + "Unable to update your credits balance");
                                } else {
                                    if (itemName != null && itemName.giveItem((Player)sender,"VILLAGER_EGG",1)) {
                                        sender.sendMessage(ChatColor.GREEN + "You have been given a villager egg");
                                    } else {
                                        credits.setCredits(credits.getCredits()+need);
                                        credits.save(database);
                                        sender.sendMessage(ChatColor.RED + "Unable to give you a villager egg. Your credits have been refunded.");
                                    }
                                }
                            }
                        } else {
                            sender.sendMessage("You have " + credits.getCredits() + " vote credits. Earn more by voting for the server!");
                            sender.sendMessage("Vote reward list:");
                            sender.sendMessage("horse (horse egg): 10 credits");
                            sender.sendMessage("cow (cow egg): 10 credits");
                            sender.sendMessage("villager (villager egg): 20 credits");
                            sender.sendMessage("chiseled_block (5 chiseled stone blocks): 2 credits");
                        }
                    } else {
                        sender.sendMessage("/vote redeem <list|item (use list to see items)>");
                    }
                } else if ("sites".equalsIgnoreCase(subCommand)) {
                    sender.sendMessage(ChatColor.GREEN + "http://www.minecraft-server-list.com/server/127787");
                    sender.sendMessage(ChatColor.GREEN + "http://www.mcserverlist.net/servers/516ba260041b26153700019e");
                    sender.sendMessage(ChatColor.GREEN + "http://minecraftservers.org/server/68085");
                } else if ("credits".equalsIgnoreCase(subCommand)) {
                    List<VoteCredits> voteCreditsList = (List<VoteCredits>)database.select(VoteCredits.class,"player = '" + database.makeSafe(playerName) + "'");
                    VoteCredits credits = null;
                    if (voteCreditsList.size() == 0) {
                        credits = new VoteCredits();
                        credits.setCredits(0);
                    } else {
                        credits = voteCreditsList.get(0);
                    }

                    sender.sendMessage(ChatColor.GREEN + "You have " + credits.getCredits() + " vote credits. Earn more by voting for the server!");
                } else {
                    sender.sendMessage("/vote <setReward|redeem|credits|sites>");
                }
            } else {
                sender.sendMessage("/vote <setReward|redeem|credits|sites>");
            }
        } else {
            return false;
        }

        return true;
    }
}
