package com.thepastimers.Mail;

import com.thepastimers.Chat.Chat;
import com.thepastimers.Chat.ChatObject;
import com.thepastimers.Database.Database;
import com.thepastimers.Permission.Permission;
import com.thepastimers.UserMap.UserMap;
import com.thepastimers.Worlds.Worlds;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: rwijtman
 * Date: 3/29/13
 * Time: 1:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class Mail extends JavaPlugin implements Listener {
    Database database;
    Permission permission;
    Worlds worlds;
    Chat chat;
    UserMap userMap;

    HashMap<String,MailData> compose;

    @Override
    public void onEnable() {
        getLogger().info("Mail init");

        getServer().getPluginManager().registerEvents(this,this);

        database = (Database)getServer().getPluginManager().getPlugin("Database");

        if (database == null) {
            getLogger().warning("Unable to load Database module. Some functionality may not be available.");
        }

        permission = (Permission)getServer().getPluginManager().getPlugin("Permission");

        if (permission == null) {
            getLogger().warning("Unable to load Permission module. Some functionality may not be available.");
        }

        worlds = (Worlds)getServer().getPluginManager().getPlugin("Worlds");
        if (worlds == null) {
            getLogger().warning("Unable to load Worlds plugin. Some functionality may not be available.");
        }

        chat = (Chat)getServer().getPluginManager().getPlugin("Chat");
        if (chat == null) {
            getLogger().warning("Unable to load Chat plugin. Some functionality may not be available.");
        }

        userMap = (UserMap)getServer().getPluginManager().getPlugin("UserMap");
        if (userMap == null) {
            getLogger().warning("Unable to lose UserMap");
        }

        compose = new HashMap<String, MailData>();

        MailData.init(database);
        getLogger().info("Printing table data:");
        getLogger().info(MailData.getTableInfo());

        getLogger().info("Mail loaded");
    }

    @Override
    public void onDisable() {
        getLogger().info("Mail disable");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (database == null) return;
        Player p = event.getPlayer();
        String uuid = p.getUniqueId().toString();
        getLogger().info("Updating UUID for " + p.getName());

        String query = "UPDATE " + MailData.table + " SET player = \"" + database.makeSafe(uuid) + "\" WHERE player = \"" + p.getName() + "\"";
        database.query(query);
        query = "UPDATE " + MailData.table + " SET sender = \"" + database.makeSafe(uuid) + "\" WHERE sender = \"" + p.getName() + "\"";
        database.query(query);
    }

    @EventHandler
    public void login(PlayerJoinEvent event) {
        if (worlds != null && worlds.getPlayerWorldType(event.getPlayer().getName()) == Worlds.VANILLA) {
            return;
        }
        if (database == null) {
            return;
        }

        Player p = event.getPlayer();

        int count = unreadMessages(p.getName());

        if (count > 0) {
            p.sendMessage(ChatColor.LIGHT_PURPLE + "You have unread mail! Use /mail to view your messages.");
        }
    }

    public int unreadMessages(String player) {
        if (database == null || player == null) {
            return 0;
        }

        player = userMap.getUUID(player);
        if (player == UserMap.NO_USER) return 0;

        List<MailData> mailDataList = (List<MailData>)database.select(MailData.class,"player = '" + database.makeSafe(player) + "' and `read` = 0;");

        return mailDataList.size();
    }

    public boolean sendMessage(MailData md) {
        Player sender = getServer().getPlayer(userMap.getId(md.getSender()));
        if (database == null || md == null) {
            sender.sendMessage(ChatColor.RED + "Unable to send mail, bad parameters.");
            return false;
        }

        md.setPlayer(userMap.getUUID(md.getPlayer()));
        if (md.getPlayer() == UserMap.NO_USER) return false;
        md.setSender(userMap.getUUID(md.getSender()));
        if (md.getSender() == UserMap.NO_USER) return false;
        md.setRead(false);

        if (!md.save(database)) {
            sender.sendMessage(ChatColor.RED + "Unable to save mail.");
            return false;
        }

        Player p = getServer().getPlayer(userMap.getId(md.getPlayer()));
        if (p != null) {
            p.sendMessage(ChatColor.LIGHT_PURPLE + "You have unread mail! Use /mail to view your messages.");
        }

        return true;
    }

    public MailData readNextUnreadMessage(String player) {
        if (database == null || player == null) {
            return null;
        }

        player = userMap.getUUID(player);
        if (player == UserMap.NO_USER) return null;

        List<MailData> mailDataList = (List<MailData>)database.select(MailData.class,"player = '" + database.makeSafe(player) + "' and `read` = 0;");

        if (mailDataList.size() == 0) {
            return null;
        }

        MailData md = mailDataList.get(0);

        md.setRead(true);

        md.save(database);

        return md;
    }

    public List<MailData> messageList(String player) {
        if (database == null || player == null) {
            return null;
        }

        player = userMap.getUUID(player);
        if (player == UserMap.NO_USER) return null;

        List<MailData> mailDataList = (List<MailData>)database.select(MailData.class,"player = '" + database.makeSafe(player) + "' ORDER BY id desc");

        if (mailDataList.size() == 0) {
            return null;
        }

        return mailDataList;
    }

    public MailData getMessage(String player,int num) {
        if (database == null || player == null) {
            return null;
        }

        player = userMap.getUUID(player);
        if (player == UserMap.NO_USER) return null;

        List<MailData> mailDataList = (List<MailData>)database.select(MailData.class,"player = '" + database.makeSafe(player) + "' ORDER BY id desc");

        if (mailDataList == null || num >= mailDataList.size() || num < 0) {
            return null;
        }

        return mailDataList.get(num);
    }

    public MailData getMessageById(String player,int id) {
        if (database == null || player == null) {
            return null;
        }

        player = userMap.getUUID(player);
        if (player == UserMap.NO_USER) return null;

        List<MailData> mailDataList = (List<MailData>)database.select(MailData.class,"player = '" + database.makeSafe(player) + "' AND id = " + id);

        if (mailDataList == null || mailDataList.size() == 0) {
            return null;
        }

        return mailDataList.get(0);
    }

    public void drawComposeMenu(Player player) {
        MailData md = compose.get(player.getName());
        if (md == null) {
            md = new MailData();
            md.setSender(userMap.getUUID(player));
            md.setPlayer("");
            md.setSubject("");
            md.setMessage("");
            compose.put(player.getName(),md);
        }
        if ("".equalsIgnoreCase(md.getPlayer())) {
            ChatObject.make().text("To: ",ChatColor.BLUE).suggest("<click to add recipient>","/mail compose to ",ChatColor.GREEN).send(chat,player);
        } else {
            ChatObject.make().text("To: ", ChatColor.BLUE).suggest(userMap.getPlayer(md.getPlayer()), "/mail compose to ", ChatColor.GREEN).send(chat, player);
        }
        if ("".equalsIgnoreCase(md.getSubject())) {
            ChatObject.make().text("Subject: ",ChatColor.BLUE).suggest("<click to add subject>","/mail compose subject ",ChatColor.GREEN).send(chat,player);
        } else {
            ChatObject.make().text("Subject: ", ChatColor.BLUE).suggest(md.getSubject(), "/mail compose subject ", ChatColor.GREEN).send(chat, player);
        }
        ChatObject.make().text("Content: ",ChatColor.BLUE).send(chat,player);
        String[] content = md.getMessage().split("NEWLINE");
        for (int i=0;i<content.length;i++) {
            String line = content[i];
            line = line.replace("NEWLINE","");
            if ("".equalsIgnoreCase(line)) continue;
            ChatObject.make().text(line).command(" [Remove]","/mail compose removeLine " + i,ChatColor.RED).send(chat,player);
        }
        ChatObject.make().suggest("<click to add line>","/mail compose body ",ChatColor.GREEN).send(chat,player);
        ChatObject.make().command("[Send] ","/mail compose send",ChatColor.BLUE).command("[Cancel]","/mail compose clear",ChatColor.RED).send(chat,player);
    }

    public void drawMessage(Player player, MailData md) {
        String from = userMap.getPlayer(md.getSender());
        ChatObject.make().text("From: ", ChatColor.BLUE).text(from,ChatColor.GREEN).send(chat, player);
        ChatObject.make().text("Subject: ", ChatColor.BLUE).text(md.getSubject(),ChatColor.GREEN).send(chat, player);
        ChatObject.make().text("Content: ",ChatColor.BLUE).send(chat,player);
        String[] content = md.getMessage().split("NEWLINE");
        for (int i=0;i<content.length;i++) {
            String line = content[i];
            line = line.replace("NEWLINE","");
            if ("".equalsIgnoreCase(line)) continue;
            ChatObject.make().text(line).send(chat,player);
        }
        ChatObject.make().command("[Reply]","/mail reply " + md.getId(),ChatColor.RED).send(chat,player);
    }

    @Override
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

        if (command.equalsIgnoreCase("mail")) {
            if (permission == null || !permission.hasPermission(playerName,"mail_mail") || playerName.equalsIgnoreCase("CONSOLE")) {
                sender.sendMessage(ChatColor.RED + "You don't have permissions for this command (mail_mail)");
                return true;
            }

            if (args.length > 0) {
                String subcommand = args[0];

                if ("read".equalsIgnoreCase(subcommand)) {
                    Player player = (Player)sender;
                    if (args.length > 1) {
                        int num;
                        try {
                            num = Integer.parseInt(args[1]);
                        } catch (Exception e) {
                            sender.sendMessage(ChatColor.RED + "Message number must be a valid integer");
                            return true;
                        }
                        MailData m = getMessage(playerName,num);
                        if (m == null) {
                            sender.sendMessage(ChatColor.RED + "Invalid message number");
                            return true;
                        }
                        m.setRead(true);
                        m.save(database);
                        drawMessage(player,m);
                    } else {
                        MailData md = readNextUnreadMessage(playerName);

                        if (md == null) {
                            sender.sendMessage("You have no more unread messages");
                        } else {
                            drawMessage(player,md);
                            sender.sendMessage(ChatColor.GREEN + "You have " + unreadMessages(playerName) + " message remaining.");
                        }
                    }
                } else if ("compose".equalsIgnoreCase(subcommand)) {
                    Player player = (Player)sender;
                    boolean drawMenu = true;
                    if (args.length > 1) {
                        String subSubCommand = args[1];
                        if ("to".equalsIgnoreCase(subSubCommand)) {
                            if (args.length > 2) {
                                MailData md = compose.get(player.getName());
                                if (md != null) {
                                    String uuid = userMap.getUUID(args[2]);
                                    if (args[2].equalsIgnoreCase(uuid)) {
                                        player.sendMessage(ChatColor.RED + "Invalid player");
                                    } else {
                                        md.setPlayer(userMap.getUUID(uuid));
                                    }
                                }
                            }
                        } else if ("subject".equalsIgnoreCase(subSubCommand)) {
                            if (args.length > 2) {
                                MailData md = compose.get(player.getName());
                                if (md != null) {
                                    StringBuilder subject = new StringBuilder();
                                    for (int i=2;i<args.length;i++) {
                                        subject.append(args[i]).append(" ");
                                    }
                                    md.setSubject(subject.toString());
                                }
                            }
                        } else if ("body".equalsIgnoreCase(subSubCommand)) {
                            if (args.length > 2) {
                                MailData md = compose.get(player.getName());
                                if (md != null) {
                                    StringBuilder body = new StringBuilder();
                                    for (int i=2;i<args.length;i++) {
                                        body.append(args[i]).append(" ");
                                    }
                                    md.setMessage(md.getMessage() + body.toString() + "NEWLINE");
                                    getLogger().info(md.getMessage());
                                }
                            }
                        } else if ("removeLine".equalsIgnoreCase(subSubCommand)) {
                            if (args.length > 2) {
                                MailData md = compose.get(player.getName());
                                if (md != null) {
                                    int line = Integer.parseInt(args[2]);
                                    String[] body = md.getMessage().split("NEWLINE");
                                    String newBody = "";
                                    for (int i=0;i<body.length;i++) {
                                        if (i != line) {
                                            newBody += body[i] + "NEWLINE";
                                        }
                                    }
                                    md.setMessage(newBody);
                                }
                            }
                        } else if ("send".equalsIgnoreCase(subSubCommand)) {
                            MailData md = compose.get(player.getName());
                            if (md != null) {
                                if (sendMessage(md)) {
                                    drawMenu = false;
                                    compose.remove(player.getName());
                                }
                            }
                        } else if ("clear".equalsIgnoreCase(subSubCommand)) {
                            drawMenu = false;
                            compose.remove(player.getName());
                            player.sendMessage(ChatColor.GREEN + "Your draft has been removed");
                        }
                    }
                    if (drawMenu) drawComposeMenu(player);
                } else if ("reply".equalsIgnoreCase(subcommand)) {
                    Player player = (Player)sender;
                    int message = Integer.parseInt(args[1]);
                    MailData md = getMessageById(player.getName(), message);
                    if (md == null) {
                        player.sendMessage(ChatColor.RED + "Cannot find that message");
                    } else {
                        MailData comp = compose.get(player.getName());
                        if (comp != null) {
                            player.sendMessage(ChatColor.RED + "You already have a message you are composing. Please discard or send that message first.");
                        } else {
                            comp = new MailData();
                            compose.put(player.getName(),comp);
                            comp.setMessage("");
                            comp.setSubject("RE: " + md.getSubject());
                            comp.setPlayer(md.getSender());
                            comp.setSender(userMap.getUUID(player));
                            drawComposeMenu(player);
                        }
                    }
                } else {
                    sender.sendMessage("/mail <compose|reply>");
                }
            } else {
                Player p = (Player)sender;
                ChatObject.make().command("[Compose]","/mail compose",ChatColor.BLUE).send(chat,p);
                ChatObject obj;
                List<MailData> mds = messageList(playerName);
                if (mds == null) {
                    sender.sendMessage("No messages");
                } else {
                    for (int i=0;i<Math.min(mds.size(),10);i++) {
                        obj = new ChatObject();
                        MailData md = mds.get(i);
                        String s = userMap.getPlayer(md.getSender());
                        String title = i + ": " + s + "-" + md.getSubject();
                        if (!md.isRead()) {
                            title += " *UNREAD*";
                        }
                        obj.command(title,"/mail read " + i,ChatColor.BLUE);
                        chat.sendRaw(obj,p);
                    }
                }
            }
        } else {
            return false;
        }

        return true;
    }
}
