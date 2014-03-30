package com.thepastimers.Rank;

import com.thepastimers.Chat.Chat;
import com.thepastimers.Chat.ChatData;
import com.thepastimers.Database.Database;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: derp
 * Date: 10/1/12
 * Time: 7:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class Rank extends JavaPlugin implements Listener {
    Database database;
    Chat chat;

    @Override
    public void onEnable() {
        getLogger().info("Rank init");

        getServer().getPluginManager().registerEvents(this,this);
        database = (Database)getServer().getPluginManager().getPlugin("Database");

        if (database == null) {
            getLogger().warning("Unable to load Database plugin. Some functionality will not be available.");
        } else {
            PlayerTitle.createTables(database,getLogger());
            PlayerRank.createTables(database,getLogger());
            RankData.createTables(database,getLogger());
        }

        chat = (Chat)getServer().getPluginManager().getPlugin("Chat");

        if (chat == null) {
            getLogger().warning("Unable to load Chat plugin. Some functionality will not be available.");
        } else {
            getLogger().info("Registering chat handler");
            chat.register(Rank.class,this,1);
        }

        PlayerRank.refreshCache(database,getLogger());
        PlayerTitle.refreshCache(database,getLogger());
        RankData.refreshCache(database,getLogger());
        getLogger().info("Table info: ");
        getLogger().info(PlayerRank.getTableInfo());
        getLogger().info(PlayerTitle.getTableInfo());
        getLogger().info(RankData.getTableInfo());

        getLogger().info("Rank init complete");
    }

    @Override
    public void onDisable() {
        getLogger().info("Rank disabled");
    }

    public String getRank(String player) {
        if (player == null || database == null) {
            return "";
        }

        player = player.replace("'","");

        PlayerRank rank = PlayerRank.getRankForPlayer(player);

        if (rank == null) {
            return "";
        }

        return rank.getRank();
    }

    public List<String> getRanks(String player) {
        List<String> ranks = new ArrayList<String>();
        if (player == null || database == null) {
            return ranks;
        }

        player = player.replace("'","");

        String rank = getRank(player);
        if ("".equalsIgnoreCase(rank)) {
            return ranks;
        }

        ranks.add(rank);

        String newRank = rank;
        while (true) {
            List<RankData> rankDataList = (List<RankData>)database.select(RankData.class,"rank = '" + database.makeSafe(newRank) + "'");
            if (rankDataList.size() == 0) {
                break;
            }

            RankData data = rankDataList.get(0);
            ranks.add(data.getParentRank());
            newRank = data.getParentRank();
        }

        return ranks;
    }

    // returns -1 if rank2 is a parent of rank1
    // returns 1 if rank1 is a parent of rank2
    // returns 0 if they are equivalent or cannot be compared (not in the same permissions chain)
    public int compareRanks(String rank1, String rank2) {
        if (rank1 == null || rank2 == null) return 0;

        List<RankData> r1 = RankData.getRankChain(rank1);
        List<RankData> r2 = RankData.getRankChain(rank2);

        if (rank1.equalsIgnoreCase(rank2))return 0;

        for (RankData rd : r1) {
            if (rd.getRank().equalsIgnoreCase(rank2)) {
                return -1;
            }
        }

        for (RankData rd : r2) {
            if (rd.getRank().equalsIgnoreCase(rank1)) {
                return 1;
            }
        }

        return 0;
    }

    public PlayerRank getRankObject(String player) {
        if (player == null || database == null) {
            return null;
        }

        player = player.replace("'","");

        PlayerRank rank = PlayerRank.getRankForPlayer(player);

        return rank;
    }

    public boolean setRank(String player, String rank) {
        if (player == null || rank == null || database == null) {
            return false;
        }

        player = database.makeSafe(player);
        rank = database.makeSafe(rank);

        if (!removeRank(player)) {
            return false;
        }

        PlayerRank newRank = new PlayerRank();
        newRank.setRank(rank);
        newRank.setPlayer(player);

        if (newRank.save(database)) {
            getServer().broadcastMessage(player + " is now in group " + rank);
            return true;
        }

        return false;
    }

    public boolean removeRank(String player) {
        if (player == null || database == null) {
            return false;
        }

        PlayerRank rankObj = getRankObject(player);

        if (rankObj == null) {
            return true;
        }

        String rank = rankObj.getRank();

        rankObj.delete(database);

        rankObj = getRankObject(player);

        if (rankObj != null) {
            getLogger().warning("removeRank: Cannot remove rank");
            return false;
        }

        getServer().broadcastMessage(player + " is no longer in group " + rank);
        return true;
    }

    public boolean hasRank(String player, String rank) {
        if (player == null || rank == null || database == null) {
            return false;
        }

        PlayerRank rankObj = PlayerRank.getRankForPlayer(player);

        if (rankObj == null) {
            return false;
        }

        return rank.equalsIgnoreCase(rankObj.getRank());
    }

    private boolean isAuthorized(String player) {
        if (player == null) {
            return false;
        }

        if (player.equalsIgnoreCase("CONSOLE")) {
            return true;
        }

        String rank = getRank(player);

        if (rank.equalsIgnoreCase("admin") || rank.equalsIgnoreCase("owner")) {
            return true;
        }

        return false;
    }

    public String getTitle(String player) {
        if (player == null || database == null) {
            return "";
        }

        player = database.makeSafe(player);

        List<PlayerTitle> titles = (List<PlayerTitle>)database.select(PlayerTitle.class,"player = '" + player + "'");

        if (titles.size() == 0) {
            return "";
        }

        String title = titles.get(0).getTitle();

        return title;
    }

    public PlayerTitle getTitleObject(String player) {
        if (player == null || database == null) {
            return null;
        }

        player = database.makeSafe(player);

        List<PlayerTitle> titles = (List<PlayerTitle>)database.select(PlayerTitle.class,"player = '" + player + "'");

        if (titles.size() == 0) {
            return null;
        }

        return titles.get(0);
    }

    public boolean removeTitle(String player) {
        if (player == null || database == null) {
            return false;
        }

        player = database.makeSafe(player);

        PlayerTitle titleObj = getTitleObject(player);

        if (titleObj == null) {
            return true;
        }

        String title = getTitle(player);

        titleObj.delete(database);

        titleObj = getTitleObject(player);

        if (titleObj != null) {
            getLogger().warning("removeTitle: Cannot remove title");
            return false;
        }

        if (chat != null) {
            title = chat.replaceColor(title);
        }
        getServer().broadcastMessage(player + " no longer has title " + title);
        return true;
    }

    public boolean setTitle(String player, String title) {
        if (player == null || database == null || title == null) {
            return false;
        }

        player = database.makeSafe(player);
        title = database.makeSafe(title);

        if (!removeTitle(player)) {
            return false;
        }

        PlayerTitle pt = new PlayerTitle();
        pt.setPlayer(player);
        pt.setTitle(title);

        if (pt.save(database)) {
            title = getTitle(player);
            if (chat != null) {
                title = chat.replaceColor(title);
            }
            getServer().broadcastMessage(player + " now has title " + title);
            return true;
        }

        return false;
    }

    public void doChat(ChatData cd) {
        PlayerTitle pt = PlayerTitle.getTitle(cd.getPlayer());
        PlayerRank pr = PlayerRank.getRankForPlayer(cd.getPlayer());
        RankData rd = pr == null ? null : RankData.getDataForRank(pr.getRank());
        //getLogger().info(pt + "");
        String title = "";
        String rank = "";
        if (pt != null) title = pt.getTitle();
        if (rd != null && !rd.getCode().equalsIgnoreCase("")) rank = rd.getFormat() + rd.getCode() + ":reset:";

        String added = "";
        if (!rank.equalsIgnoreCase("")) {
            added = "[" + rank + "] ";
        }
        if (!"".equalsIgnoreCase(title)) {
            added += title + ":reset: ";
        }
        if (!"".equalsIgnoreCase(added)) {
            cd.setPlayerString(added + cd.getPlayerString());
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

        if (command.equalsIgnoreCase("rank")) {
            if (args.length > 0) {
                String secondCommand = args[0];

                if (secondCommand.equalsIgnoreCase("set")) {
                    if (!isAuthorized(playerName)) {
                        sender.sendMessage(ChatColor.RED + "You do not have permission to do this (must be console or owner/admin)");
                        getLogger().info(playerName + " attempted unauthorized access of /rank set");
                        return true;
                    }
                    if (args.length > 2) {
                        String player = args[1];
                        String newRank = args[2].toLowerCase();

                        if (!setRank(player,newRank)) {
                            sender.sendMessage("Unable to set rank");
                        }

                        return true;
                    } else {
                        sender.sendMessage("/rank set <player> <rank>");
                    }
                } else if (secondCommand.equalsIgnoreCase("remove")) {
                    if (!isAuthorized(playerName)) {
                        sender.sendMessage(ChatColor.RED + "You do not have permission to do this (must be console or owner/admin)");
                        getLogger().info(playerName + " attempted unauthorized access of /rank remove");
                        return true;
                    }
                    if (args.length > 1) {
                        String player = args[1];

                        if (!removeRank(player)) {
                            sender.sendMessage("Unable to remove rank.");
                        }

                        return true;
                    }
                } else if (secondCommand.equalsIgnoreCase("check")) {
                    if (!isAuthorized(playerName)) {
                        sender.sendMessage(ChatColor.RED + "You do not have permission to do this (must be console or owner/admin)");
                        getLogger().info(playerName + " attempted unauthorized access of /rank check");
                        return true;
                    }
                    if (args.length > 1) {
                        String player = args[1];

                        String rank = getRank(player);

                        if (!"".equals(rank)) {
                            sender.sendMessage(player + " has a rank of " + rank);
                        } else {
                            sender.sendMessage(player + " has no rank");
                        }
                    }
                }
            } else {
                sender.sendMessage("/rank <set|remove|check>");
            }
        } else if(command.equalsIgnoreCase("title")) {
            if (args.length > 0) {
                String secondCommand = args[0];

                if (secondCommand.equalsIgnoreCase("set")) {
                    if (!isAuthorized(playerName)) {
                        sender.sendMessage(ChatColor.RED + "You do not have permission to do this (must be console or owner/admin)");
                        getLogger().info(playerName + " attempted unauthorized access of /title set");
                        return true;
                    }
                    if (args.length > 2) {
                        String player = args[1];
                        String title = "";
                        for (int i=2;i<args.length;i++) {
                            title += args[i] + " ";
                        }

                        title = title.substring(0,title.length()-1);

                        if (title == "") {
                            if (!removeTitle(player)) {
                                sender.sendMessage("Unable to remove title for player " + player);
                            }
                        } else {
                            if (!setTitle(player,title)) {
                                sender.sendMessage("Unable to set title " + title + " for player " + player);
                            }
                        }
                    } else {
                        sender.sendMessage("/title set <player> <title>");

                    }
                } else if (secondCommand.equalsIgnoreCase("check")) {
                    if (args.length > 1) {
                        String player = args[1];

                        String title = getTitle(player);

                        sender.sendMessage("Title for " + player + ": " + title);
                    } else {
                        sender.sendMessage("/title check <player>");

                    }
                }
            } else {
                sender.sendMessage("/title <set|check>");
            }
        } else {
            return false;
        }
        return true;
    }
}
