package com.thepastimers.Rank;

import com.thepastimers.Chat.Chat;
import com.thepastimers.Chat.ChatData;
import com.thepastimers.Database.Database;
import com.thepastimers.UserMap.UserMap;
import org.bukkit.ChatColor;
import org.bukkit.ChatStyle;
import org.bukkit.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
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
    UserMap userMap;
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

        userMap = (UserMap)getServer().getPluginManager().getPlugin("UserMap");
        if (userMap == null) {
            getLogger().warning("Unable to load UserMap plugin. Some functionality will not be available.");
        }

        chat = (Chat)getServer().getPluginManager().getPlugin("Chat");
        if (chat != null) {
            chat.register(this.myClass, this);
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

    public void doChat(ChatData obj) {
        //getLogger().info("Got dochat for " + obj.getMessage());
        //getLogger().info("player is" + obj.getPlayer());

        String playerUuid = userMap.getUUID(obj.getPlayer());
        if (UserMap.NO_USER.equalsIgnoreCase(playerUuid)) {
            // user couldn't be found
            return;
        }

        PlayerRank playerRank = getRankObject(playerUuid);
        RankData rank;
        if (playerRank != null) {
            rank = RankData.getDataForRank(playerRank.getRank());
        } else {
            // Note all should be a constant somewhere or better in DB
            rank = RankData.getDataForRank("all");
        }
        if (rank == null) {
            return;
        }
        //getLogger().info(rank.getFormat());
        if (!"".equals(rank.getFormat())) {
            obj.setPlayerString(Text.make()
                    .compound(chat.replaceColor(rank.getFormat()))
                    .style(ChatStyle.RESET)
                    .text(" ")
                    .compound(obj.getPlayerString()));
        }
    }

    public String getRank(String player) {
        if (player == null || database == null) {
            return "";
        }

        player = userMap.getUUID(player);
        if (player == UserMap.NO_USER) return "";

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

        player = player.replace("'", "");

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

        String playerName = "";
        if (userMap != null) {
            playerName = userMap.getPlayer(player);
        }

        if (newRank.save(database)) {
            getServer().broadcastMessage(Text.make().color(ChatColor.BLUE).text(playerName + " is now in group " + rank));
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

        String playerName = "";
        if (userMap != null) {
            playerName = userMap.getPlayer(player);
        }

        getServer().broadcastMessage(Text.make().color(ChatColor.BLUE).text(playerName + " is no longer in group " + rank));
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

    private boolean isAuthorized(String player, String toRank) {
        if (player == null) {
            return false;
        }

        if (player.equalsIgnoreCase("CONSOLE")) {
            return true;
        }

        String rank = getRank(player);

        if (rank.equalsIgnoreCase("gameadmin") || rank.equalsIgnoreCase("admin") || rank.equalsIgnoreCase("owner")) {
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

        String playerName = "";
        if (userMap != null) {
            playerName = userMap.getPlayer(player);
        }
        getServer().broadcastMessage(Text.make().text(playerName + " no longer has title " + title));
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

            String playerName = "";
            if (userMap != null) {
                playerName = userMap.getPlayer(player);
            }
            getServer().broadcastMessage(Text.make().text(playerName + " now has title " + title));
            return true;
        }

        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        String playerName = "";

        String uuid = "";
        if (sender instanceof Player) {
            Player p = (Player)sender;
            playerName = p.getName();
            uuid = p.getUniqueId().toString();
        } else {
            playerName = "CONSOLE";
            uuid = playerName;
        }
        if ("".equalsIgnoreCase(uuid)) {
            sender.sendMessage(Text.make().color(ChatColor.RED).text("Could not get a proper UUID for you, aborting command."));
        }


        String command = cmd.getName();
        if (command.equalsIgnoreCase("rank")) {
            if (args.length > 0) {
                String secondCommand = args[0];

                if (secondCommand.equalsIgnoreCase("set")) {
                    if (args.length > 2) {
                        String newRank = args[2].toLowerCase();
                        if (!isAuthorized(uuid,newRank)) {
                            sender.sendMessage(Text.make().color(ChatColor.RED).text("You do not have permission to do this (must be console or owner/admin)"));
                            sender.sendMessage(Text.make().color(ChatColor.RED).text("Your UUID is " + uuid));
                            getLogger().info(playerName + " attempted unauthorized access of /rank set");
                            return true;
                        }
                        String player = args[1];
                        if (userMap == null) {
                            sender.sendMessage(Text.make().color(ChatColor.RED).text("This functionality is currently unavailable"));
                            return true;
                        }
                        String playerUuid = userMap.getUUID(player);
                        if (UserMap.NO_USER.equalsIgnoreCase(playerUuid)) {
                            sender.sendMessage(Text.make().color(ChatColor.RED).text("That user cannot be found"));
                            return true;
                        }

                        if (!setRank(playerUuid,newRank)) {
                            sender.sendMessage("Unable to set rank");
                        }

                        return true;
                    } else {
                        sender.sendMessage(Text.make().color(ChatColor.RED).text("/rank set <player> <rank>"));
                    }
                } else if (secondCommand.equalsIgnoreCase("remove")) {
                    if (!isAuthorized(uuid,null)) {
                        sender.sendMessage(Text.make().color(ChatColor.RED).text("You do not have permission to do this (must be console or owner/admin)"));
                        getLogger().info(playerName + " attempted unauthorized access of /rank remove");
                        return true;
                    }
                    if (args.length > 1) {
                        String player = args[1];
                        if (userMap == null) {
                            sender.sendMessage(Text.make().color(ChatColor.RED).text("This functionality is currently unavailable"));
                            return true;
                        }
                        String playerUuid = userMap.getUUID(player);
                        if (UserMap.NO_USER.equalsIgnoreCase(playerUuid)) {
                            sender.sendMessage(Text.make().color(ChatColor.RED).text("That user cannot be found"));
                        }

                        if (!removeRank(playerUuid)) {
                            sender.sendMessage("Unable to remove rank.");
                        }

                        return true;
                    } else {
                        sender.sendMessage(Text.make().color(ChatColor.RED).text("/rank remove <player>"));
                    }
                } else if (secondCommand.equalsIgnoreCase("check")) {
                    if (!isAuthorized(uuid,null)) {
                        sender.sendMessage(Text.make().color(ChatColor.RED).text("You do not have permission to do this (must be console or owner/admin)"));
                        getLogger().info(playerName + " attempted unauthorized access of /rank check");
                        return true;
                    }
                    if (args.length > 1) {
                        String player = args[1];
                        if (userMap == null) {
                            sender.sendMessage(Text.make().color(ChatColor.RED).text("This functionality is currently unavailable"));
                            return true;
                        }
                        String playerUuid = userMap.getUUID(player);
                        if (UserMap.NO_USER.equalsIgnoreCase(playerUuid)) {
                            sender.sendMessage(Text.make().color(ChatColor.RED).text("That user cannot be found"));
                        }

                        String rank = getRank(playerUuid);

                        if (!"".equals(rank)) {
                            sender.sendMessage(player + " has a rank of " + rank);
                        } else {
                            sender.sendMessage(player + " has no rank");
                        }
                    } else {
                        sender.sendMessage(Text.make().color(ChatColor.RED).text( "/rank check <player>"));
                    }
                } else if (secondCommand.equalsIgnoreCase("list")) {
                    if (!isAuthorized(uuid,null)) {
                        sender.sendMessage(Text.make().color(ChatColor.RED).text("You do not have permission to do this (must be console or owner/admin)"));
                        getLogger().info(playerName + " attempted unauthorized access of /rank list");
                        return true;
                    }
                    if (args.length > 1) {
                        String rank = args[1];

                        List<String> players = PlayerRank.getPlayersForRank(rank);

                        sender.sendMessage(Text.make().color(ChatColor.BLUE).text("List of players who have rank " + rank + ":"));
                        for (String playerUuid: players) {
                            String player = UserMap.NO_USER;
                            if (userMap != null) {
                                player = userMap.getPlayer(playerUuid);
                            }
                            if (UserMap.NO_USER.equalsIgnoreCase(player)) player = playerUuid;
                            sender.sendMessage(player);
                        }
                    } else {
                        sender.sendMessage(Text.make().color(ChatColor.RED).text("/rank list <rank>"));
                    }
                }
            } else {
                sender.sendMessage("/rank <set|remove|check|list>");
            }
        } else if(command.equalsIgnoreCase("title")) {
            if (args.length > 0) {
                String secondCommand = args[0];

                if (secondCommand.equalsIgnoreCase("set")) {
                    if (!isAuthorized(uuid,null)) {
                        sender.sendMessage(Text.make().color(ChatColor.RED).text("You do not have permission to do this (must be console or owner/admin)"));
                        getLogger().info(playerName + " attempted unauthorized access of /title set");
                        return true;
                    }
                    if (args.length > 2) {
                        String player = args[1];
                        if (userMap == null) {
                            sender.sendMessage(Text.make().color(ChatColor.RED).text("This functionality is currently unavailable"));
                            return true;
                        }
                        String playerUuid = userMap.getUUID(player);
                        if (UserMap.NO_USER.equalsIgnoreCase(playerUuid)) {
                            sender.sendMessage(Text.make().color(ChatColor.RED).text("That user cannot be found"));
                            return true;
                        }

                        String title = "";
                        for (int i=2;i<args.length;i++) {
                            title += args[i] + " ";
                        }

                        title = title.substring(0,title.length()-1);

                        if ("".equals(title)) {
                            if (!removeTitle(playerUuid)) {
                                sender.sendMessage("Unable to remove title for player " + player);
                            }
                        } else {
                            if (!setTitle(playerUuid,title)) {
                                sender.sendMessage("Unable to set title " + title + " for player " + player);
                            }
                        }
                    } else {
                        sender.sendMessage("/title set <player> <title>");

                    }
                } else if (secondCommand.equalsIgnoreCase("check")) {
                    if (args.length > 1) {
                        String player = args[1];
                        if (userMap == null) {
                            sender.sendMessage(Text.make().color(ChatColor.RED).text("This functionality is currently unavailable"));
                            return true;
                        }
                        String playerUuid = userMap.getUUID(player);
                        if (UserMap.NO_USER.equalsIgnoreCase(playerUuid)) {
                            sender.sendMessage(Text.make().color(ChatColor.RED).text("That user cannot be found"));
                            return true;
                        }

                        String title = getTitle(playerUuid);

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
