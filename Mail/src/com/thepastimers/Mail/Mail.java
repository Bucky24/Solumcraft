package com.thepastimers.Mail;

import com.thepastimers.Chat.Chat;
import com.thepastimers.Chat.ChatObject;
import com.thepastimers.Chat.Menu;
import com.thepastimers.Chat.MenuItem;
import com.thepastimers.Database.Database;
import com.thepastimers.Permission.Permission;
import com.thepastimers.Worlds.Worlds;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    Menu mainMenu;

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
            mainMenu = null;
        } else {
            mainMenu = new Menu("Mail menu (clickable)");
            mainMenu.addItem(new MenuItem("Sent mail","command","check sent"));
            mainMenu.addItem(new MenuItem("Received mail","command","check received"));
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
            p.sendMessage(ChatColor.LIGHT_PURPLE + "You have unread mail! Use /mail read to read it.");
        }
    }

    public int unreadMessages(String player) {
        if (database == null || player == null) {
            return 0;
        }

        List<MailData> mailDataList = (List<MailData>)database.select(MailData.class,"player = '" + database.makeSafe(player) + "' and `read` = 0;");

        return mailDataList.size();
    }

    public boolean sendMessage(String player, String sender, String message) {
        if (database == null || player == null || sender == null || message == null) {
            return false;
        }

        MailData md = new MailData();
        md.setPlayer(player);
        md.setMessage(message);
        md.setSender(sender);
        md.setRead(false);

        if (!md.save(database)) {
            getLogger().warning("Unable to save mail.");
            return false;
        }

        return true;
    }

    public MailData readNextUnreadMessage(String player) {
        if (database == null || player == null) {
            return null;
        }

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

        List<MailData> mailDataList = (List<MailData>)database.select(MailData.class,"player = '" + database.makeSafe(player) + "' ORDER BY id desc");

        if (mailDataList == null || num >= mailDataList.size() || num < 0) {
            return null;
        }

        return mailDataList.get(num);
    }

    public void drawComposeMenu(Player player) {
        MailData md = compose.get(player.getName());
        if (md == null) {
            md = new MailData();
            md.setSender(player.getName());
            md.setPlayer("");
            md.setSubject("");
            md.setMessage("");
            compose.put(player.getName(),md);
        }
        if ("".equalsIgnoreCase(md.getPlayer())) {
            ChatObject.make().text("To: ",ChatColor.BLUE).suggest("<click to add recipient>","/mail compose to ",ChatColor.GREEN).send(chat,player);
        } else {
            ChatObject.make().text("To: ", ChatColor.BLUE).suggest(md.getPlayer(), "/mail compose to ", ChatColor.GREEN).send(chat, player);
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
        ChatObject.make().text("From: ", ChatColor.BLUE).text(md.getPlayer(),ChatColor.GREEN).send(chat, player);
        ChatObject.make().text("Subject: ", ChatColor.BLUE).text(md.getSubject(),ChatColor.GREEN).send(chat, player);
        ChatObject.make().text("Content: ",ChatColor.BLUE).send(chat,player);
        String[] content = md.getMessage().split("NEWLINE");
        for (int i=0;i<content.length;i++) {
            String line = content[i];
            line = line.replace("NEWLINE","");
            if ("".equalsIgnoreCase(line)) continue;
            ChatObject.make().text(line).send(chat,player);
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
                } else if ("check".equalsIgnoreCase(subcommand)) {
                    Player p = (Player)sender;
                    if (args.length == 0) {
                        if (chat != null) {
                            mainMenu.sendMenuTo(p,chat);
                        }
                    } else {
                        int unread = unreadMessages(playerName);

                        sender.sendMessage("You have " + unread + " unread message/s");
                        sender.sendMessage("Messages 0-10 (to read use /mail read <number>:");
                        List<MailData> md = messageList(playerName);
                        if (md == null) {
                            sender.sendMessage("No messages");
                        } else {
                            for (int i=0;i<Math.min(md.size(),10);i++) {
                                sender.sendMessage(i + ": message from " + md.get(i).getSender());
                            }
                        }
                    }
                } else if ("send".equalsIgnoreCase(subcommand)) {
                    if (args.length > 3) {
                        String player = args[1];

                        StringBuilder sb = new StringBuilder();

                        for (int i=2;i<args.length;i++) {
                            sb.append(args[i]).append(" ");
                        }

                        sender.sendMessage("Sending mail to " + player + ". Message: ");
                        sender.sendMessage(sb.toString());

                        if (!sendMessage(player,playerName,sb.toString())) {
                            sender.sendMessage(ChatColor.RED + "Unable to send mail");
                        } else {
                            sender.sendMessage(ChatColor.GREEN + "Mail sent!");
                        }
                    } else {
                        sender.sendMessage("/mail send <player> <message>");
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
                                    md.setPlayer(args[2]);
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
                                if ("".equalsIgnoreCase(md.getPlayer())) {
                                    player.sendMessage(ChatColor.RED + "You cannot send a message to nobody");
                                } else if ("".equalsIgnoreCase(md.getMessage())) {
                                    player.sendMessage(ChatColor.RED + "You cannot send a blank message");
                                } else {
                                    boolean result = md.save(database);
                                    if (result) {
                                        player.sendMessage(ChatColor.GREEN + "Mail sent!");
                                        drawMenu = false;
                                        compose.remove(player.getName());
                                    } else {
                                        player.sendMessage(ChatColor.RED + "Could not send mail, try again later.");
                                    }
                                }
                            }
                        }
                    }
                    if (drawMenu) drawComposeMenu(player);
                } else {
                    sender.sendMessage("/mail <read|check|send>");
                }
            } else {
                sender.sendMessage("/mail <read|check|send>");
                ChatObject obj;
                List<MailData> mds = messageList(playerName);
                if (mds == null) {
                    sender.sendMessage("No messages");
                } else {
                    Player p = (Player)sender;
                    for (int i=0;i<Math.min(mds.size(),10);i++) {
                        obj = new ChatObject();
                        MailData md = mds.get(i);
                        obj.command(i + ": " + md.getSender() + "-" + md.getSubject(),"/mail read " + i,ChatColor.BLUE);
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
