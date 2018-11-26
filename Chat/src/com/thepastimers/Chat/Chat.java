package com.thepastimers.Chat;

import BukkitBridge.Plugin;
import BukkitBridge.Text;
import BukkitBridge.TextStyle;
import com.thepastimers.Database.Database;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 6/22/13
 * Time: 9:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class Chat extends Plugin implements Listener {
    Database database;

    Map<Integer,Map<Class,JavaPlugin>> listeners;
    List<ChatCode> codes;
    Map<String,Map<Class,JavaPlugin>> commandListeners;

    PrintWriter chatLog = null;

    @Override
    public void onEnable() {
        getLogger().info("Chat init");

        getServer().getPluginManager().registerEvents(this,this);

        database = (Database)getServer().getPluginManager().getPlugin("Database");

        if (database == null) {
            getLogger().warning("Unable to load Database plugin. Some functionality may not be available");
        }

        getLogger().info(ChatData.getTableInfo());
        getLogger().info(CommandData.getTableInfo());

        //BukkitTask task = new GetChats(this,database).runTaskTimer(this,0,60);
        //BukkitTask task2 = new GetCommands(this,database).runTaskTimer(this,0,60);
        if (listeners == null) listeners = new HashMap<Integer,Map<Class,JavaPlugin>>();
        if (commandListeners == null) commandListeners = new HashMap<String, Map<Class, JavaPlugin>>();

        codes = new ArrayList<ChatCode>();

        codes.add(new ChatCode("c",ChatColor.RED,"Red text"));
        codes.add(new ChatCode("1",ChatColor.BLUE,"Blue text"));
        codes.add(new ChatCode("a",ChatColor.GREEN,"Green text"));
        codes.add(new ChatCode("2",ChatColor.DARK_GREEN,"Dark Green text"));
        codes.add(new ChatCode("e",ChatColor.YELLOW,"Yellow text"));
        codes.add(new ChatCode("d",ChatColor.LIGHT_PURPLE,"Purple text"));
        codes.add(new ChatCode("f",ChatColor.WHITE,"White text"));
        codes.add(new ChatCode("6",ChatColor.GOLD,"Gold text"));
        codes.add(new ChatCode("7",ChatColor.GRAY,"Gray text"));
        codes.add(new ChatCode("0",ChatColor.BLACK,"Black text"));
        codes.add(new ChatCode("4",ChatColor.DARK_RED,"Dark Red text"));
        codes.add(new ChatCode("b",ChatColor.AQUA,"Aqua text"));
        codes.add(new ChatCode("3",ChatColor.DARK_AQUA,"Dark Aqua text"));
        codes.add(new ChatCode("9",ChatColor.DARK_BLUE,"Dark Blue text"));
        codes.add(new ChatCode("5",ChatColor.DARK_PURPLE,"Dark Purple text"));
        codes.add(new ChatCode("8",ChatColor.DARK_GRAY,"Dark Gray text"));

        //codes.add(new ChatCode(":obf:;&k",ChatColor.getByChar("k").toString(),"Obfuscated text"));
        //codes.add(new ChatCode(":strike:;&m",ChatColor.getByChar("m").toString(),"Strikethrough"));
        //codes.add(new ChatCode(":italic:;&o",ChatColor.getByChar("o").toString(),"Italic"));
        //codes.add(new ChatCode(":underline:;&n",ChatColor.getByChar("n").toString(),"Underline"));
        //codes.add(new ChatCode(":bold:;&l",ChatColor.getByChar("l").toString(),"Bold"));

        //codes.add(new ChatCode(":reset:;&r",ChatColor.getByChar("r").toString(),"Reset formatting"));

        File dataFolder = getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }
        File chatFile = new File(dataFolder,"chatLog.txt");
        try {
            if (!chatFile.exists()) {
                chatFile.createNewFile();
            }
            FileWriter fw = new FileWriter(chatFile,true);
            chatLog = new PrintWriter(fw);
        } catch (Exception e) {
            getLogger().warning("Unable to create chatLog");
        }

        getLogger().info("Chat init complete");
    }

    @Override
    public void onDisable() {
        chatLog.close();
        getLogger().info("Chat disable");
    }

    public void writeChatLine(String line) {
        chatLog.write(line + "\n");
        chatLog.flush();
    }

    public void register(Class c, JavaPlugin plugin) {
        register(c,plugin,1);
    }

    public void register(Class c, JavaPlugin plugin, int priority) {
        if (listeners == null) {
            listeners = new HashMap<Integer,Map<Class,JavaPlugin>>();
        }
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
        if (commandListeners == null) {
            commandListeners = new HashMap<String, Map<Class, JavaPlugin>>();
        }
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
        data.setPlayerString(Text.make().text("<")
                .text(player)
                .text(">")
                .style(TextStyle.RESET)
        );
        data.setMessage(message);
        Date date = new Date();
        data.setTime(new Timestamp(date.getTime()));
        data.setSeen(true);
        String originalMessage = data.getMessage();

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
                    Throwable t = e.getCause();
                    getLogger().warning("Unable to call doChat for " + c.getName());
                    t.printStackTrace();
                }
            }
        }

        /*String messageBak = data.getMessage();
        data.setMessage(replaceColor(data.getMessage()));
        data.setMessage(data.getMessage());
        String fancyMessage = data.getMessage();
        String originalMessage = stripColors(data.getMessage());*/

        Date now = new Date();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(now);

        int minute = calendar.get(Calendar.MINUTE);
        String minuteString = minute >= 10 ? "" + minute : "0" + minute;
        Text mainMessage = Text.make().text("(" + calendar.get(Calendar.HOUR_OF_DAY) + ":" + minuteString + ") ")
                .compound(data.getPlayerString())
                .style(TextStyle.RESET)
                .text(" ")
                .text(data.getMessage());
        String vanillaMessage = "";
        if (web) {
            mainMessage = Text.make().color(ChatColor.LIGHT_PURPLE)
                    .text("[WEB]")
                    .color(ChatColor.WHITE)
                    .text(" " + mainMessage);
        }
        vanillaMessage = "<" + data.getPlayer() + "> " + originalMessage;
        //getLogger().info(vanillaMessage);

        List<BukkitBridge.Player> list = server().getOnlinePlayers();

        for (BukkitBridge.Player p : list) {
            p.sendMessage(mainMessage);
        }
        //getLogger().info(mainMessage);
        getLogger().info(vanillaMessage);

        writeChatLine(mainMessage.getPlainText());
    }
    
    public Text replaceColor(String text) {
        Text result = Text.make();
        boolean atAmpersand = false;
        StringBuilder builder = new StringBuilder();
        for (int i=0;i<text.length();i++) {
            char ch = text.charAt(i);
            //getLogger().info(Character.toString(ch));
            if (ch == '&') {
                result.text(builder.toString());
                builder.setLength(0);
                atAmpersand = true;
            } else if (atAmpersand) {
                for (ChatCode code : codes) {
                    //getLogger().info(code.getKey() + " == " + Character.toString(ch));
                    if (code.getKey().charAt(0) == ch) {
                        result.color(code.getCode());
                        break;
                    }
                }
                atAmpersand = false;
            } else {
                builder.append(ch);
            }
        }
        result.text(builder.toString());
        return result;
    }

    public String stripColors(String text) {
        for (ChatCode code : codes) {
            //getLogger().info(text + " " + code.getKey() + " " + code.getCode());
            String[] codeList = code.getKey().split(";");
            for (int i=0;i<codeList.length;i++) {
                text = text.replace(codeList[i],"");
            }
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
                    Throwable t = e.getCause();
                    getLogger().warning("Unable to call doChat for " + c.getName());
                    t.printStackTrace();
                }
            }
        }

        if (database != null) {
            //data.save(database);
        }
    }

    public void sendRaw(String string, Player player) {
        player.sendMessage(string);
        /*try {
            JSONObject jsonObject = (JSONObject)new JSONParser().parse(string);
            sendRaw(jsonObject,player);
        } catch (Exception e) {
            // ignore
        }*/
    }

    /*public void sendRaw(JSONObject obj, Player player) {
        IChatBaseComponent comp = ChatSerializer.a(obj.toString());
        PacketPlayOutChat packet = new PacketPlayOutChat(comp, true);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }*/

    public void sendRaw(ChatObject obj, Player player) {
        this.sendRaw(obj.getText(), player);
        /*IChatBaseComponent comp = ChatSerializer.a(obj.toString());
        PacketPlayOutChat packet = new PacketPlayOutChat(comp, true);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);*/
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
                String key = code.getKey().replace(";"," or ");
                sender.sendMessage("&" + key + " = " + code.getDescription());
            }
        }/* else if ("testChat".equalsIgnoreCase(command)) {
            Player p = (Player)sender;
            ChatObject obj = new ChatObject();
            obj.text("Some text").text("Colored text", ChatColor.BLUE);
            sendRaw(obj, p);
            obj = new ChatObject();
            obj.url("A url","http://www.google.com").url("http://www.yahoo.com").url("http://www.ruinsofchaos.com",ChatColor.AQUA).url("Another url","http://mail.google.com",ChatColor.DARK_GREEN);
            sendRaw(obj,p);
            obj = new ChatObject();
            obj.command("A command","/go main").command("Another command","/go economy",ChatColor.DARK_GREEN);
            sendRaw(obj,p);
        }*/ else {
            return false;
        }
        return true;
    }
}
