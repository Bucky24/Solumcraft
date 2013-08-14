package com.thepastimers.Chat;

import com.thepastimers.Database.Database;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 6/22/13
 * Time: 9:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class Chat extends JavaPlugin implements Listener {
    Database database;
    Map<Integer,Map<Class,JavaPlugin>> listeners;
    List<ChatCode> codes;
    Map<String,Map<Class,JavaPlugin>> commandListeners;

    @Override
    public void onEnable() {
        getLogger().info("Chat init");

        getServer().getPluginManager().registerEvents(this,this);

        database = (Database)getServer().getPluginManager().getPlugin("Database");

        if (database == null) {
            getLogger().warning("Unable to load Database plugin. Some functionality may not be available");
        }

        getLogger().info(ChatData.getTableInfo());

        BukkitTask task = new GetChats(this,database).runTaskTimer(this,0,20);
        BukkitTask task2 = new GetCommands(this,database).runTaskTimer(this,0,20);
        listeners = new HashMap<Integer,Map<Class,JavaPlugin>>();
        commandListeners = new HashMap<String, Map<Class, JavaPlugin>>();

        codes = new ArrayList<ChatCode>();

        codes.add(new ChatCode("red",ChatColor.RED.toString(),"Red text"));
        codes.add(new ChatCode("blue",ChatColor.BLUE.toString(),"Blue text"));
        codes.add(new ChatCode("green",ChatColor.GREEN.toString(),"Green text"));
        codes.add(new ChatCode("dark green",ChatColor.DARK_GREEN.toString(),"Dark Green text"));
        codes.add(new ChatCode("yellow",ChatColor.YELLOW.toString(),"Yellow text"));
        codes.add(new ChatCode("purple",ChatColor.LIGHT_PURPLE.toString(),"Purple text"));
        codes.add(new ChatCode("white",ChatColor.WHITE.toString(),"White text"));
        codes.add(new ChatCode("gold",ChatColor.GOLD.toString(),"Gold text"));
        codes.add(new ChatCode("gray",ChatColor.GRAY.toString(),"Gray text"));
        codes.add(new ChatCode("dark red",ChatColor.DARK_RED.toString(),"Dark Red text"));

        codes.add(new ChatCode("obf",ChatColor.getByChar("k").toString(),"Obfuscated text"));
        codes.add(new ChatCode("strike",ChatColor.getByChar("m").toString(),"Strikethrough"));

        codes.add(new ChatCode("reset",ChatColor.getByChar("r").toString(),"Reset formatting"));

        getLogger().info("Chat init complete");
    }

    @Override
    public void onDisable() {
        getLogger().info("Chat disable");
    }

    public void register(Class c, JavaPlugin plugin) {
        register(c,plugin,1);
    }

    public void register(Class c, JavaPlugin plugin, int priority) {
        Map<Class,JavaPlugin> listenerMap = listeners.get(priority);
        if (listenerMap == null) {
            listenerMap = new HashMap<Class, JavaPlugin>();
            listeners.put(priority,listenerMap);
        }
        listenerMap.put(c,plugin);
    }

    public void registerCommand(String command, Class c, JavaPlugin plugin) {
        Map<Class,JavaPlugin> classMap = new HashMap<Class, JavaPlugin>();
        classMap.put(c,plugin);
        commandListeners.put(command, classMap);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        sendChat(event.getMessage(),event.getPlayer().getName());
        event.setCancelled(true);
    }

    public void sendChat(String message, String player) {
        sendChat(message,player,true);
    }

    public void sendChat(String message, String player, boolean save) {
        sendChat(message,player,save,false);
    }

    public void sendChat(String message, String player, boolean save, boolean web) {
        ChatData data = new ChatData();
        data.setPlayer(player);
        data.setPlayerString(player);
        data.setMessage(message);
        Date date = new Date();
        data.setTime(new Timestamp(date.getTime()));
        data.setSeen(true);

        SortedSet<Integer> keys = new TreeSet<Integer>(listeners.keySet());
        for (Integer prio : keys) {
            Map<Class,JavaPlugin> classMap = listeners.get(prio);
            for (Class c : classMap.keySet()) {
                try {
                    JavaPlugin p = classMap.get(c);
                    Class[] argTypes = new Class[] {ChatData.class};
                    Method m = c.getDeclaredMethod("doChat",argTypes);
                    m.invoke(p,data);
                } catch (Exception e) {
                    getLogger().warning("Unable to call doChat for " + c.getName());
                }
            }
        }

        // handle colors
        String bak = data.getPlayerString();
        data.setPlayerString(replaceColor(data.getPlayerString()));
        data.setPlayerString(data.getPlayerString() + ChatColor.getByChar("r").toString());

        String messageBak = data.getMessage();
        data.setMessage(replaceColor(data.getMessage()));
        data.setMessage(data.getMessage() + ChatColor.getByChar("r").toString());

        if (web) {
            getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + "[WEB]" + ChatColor.WHITE + " <" + data.getPlayerString() + "> " + data.getMessage());
        } else {
            getServer().broadcastMessage("<" + data.getPlayerString() + "> " + data.getMessage());
        }

        // we want it unspoiled (without the color codes) for when it goes into db
        data.setPlayerString(bak);
        data.setMessage(messageBak);
        if (database != null && save) {
            data.save(database);
        }
    }
    
    public String replaceColor(String text) {
        for (ChatCode code : codes) {
            //getLogger().info(text + " " + code.getKey() + " " + code.getCode());
            text = text.replace(":" + code.getKey() + ":",code.getCode());
        }
        return text;
    }

    public void saveMessage(String message, String player) {
        ChatData data = new ChatData();
        data.setPlayer(player);
        data.setPlayerString(player);
        data.setMessage(message);
        Date date = new Date();
        data.setTime(new Timestamp(date.getTime()));
        data.setSeen(true);

        SortedSet<Integer> keys = new TreeSet<Integer>(listeners.keySet());
        for (Integer prio : keys) {
            Map<Class,JavaPlugin> classMap = listeners.get(prio);
            for (Class c : classMap.keySet()) {
                try {
                    JavaPlugin p = classMap.get(c);
                    Class[] argTypes = new Class[] {ChatData.class};
                    Method m = c.getDeclaredMethod("doChat",argTypes);
                    m.invoke(p,data);
                } catch (Exception e) {
                    getLogger().warning("Unable to call doChat for " + c.getName());
                }
            }
        }

        if (database != null) {
            data.save(database);
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

        if (command.equalsIgnoreCase("codes")) {
            sender.sendMessage("Chat codes:");
            for (ChatCode code : codes) {
                sender.sendMessage(":" + code.getKey() + ": = " + code.getDescription());
            }
        } else {
            return false;
        }
        return true;
    }
}
