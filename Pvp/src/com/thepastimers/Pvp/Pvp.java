package com.thepastimers.Pvp;


import com.thepastimers.Chat.Chat;
import com.thepastimers.Chat.ChatData;
import com.thepastimers.Database.Database;
import com.thepastimers.ItemName.ItemName;
import com.thepastimers.Worlds.Worlds;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: rwijtman
 * Date: 8/8/13
 * Time: 11:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class Pvp extends JavaPlugin implements Listener {
    Database database;
    ItemName itemName;
    Chat chat;
    Worlds worlds;
    String world = "world";

    @Override
    public void onEnable() {
        getLogger().info("Pvp init");

        getServer().getPluginManager().registerEvents(this,this);

        database = (Database)getServer().getPluginManager().getPlugin("Database");

        if (database == null) {
            getLogger().warning("Unable to load Database module. Some functionality may not be available.");
        } else {
            UniqueHead.createTables(database,getLogger());
        }

        itemName = (ItemName)getServer().getPluginManager().getPlugin("ItemName");

        if (itemName == null) {
            getLogger().warning("Unable to load ItemName module. Some functionality may not be available.");
        }

        chat = (Chat)getServer().getPluginManager().getPlugin("Chat");
        if (chat == null) {
            getLogger().warning("Unabel to load Chat module. Some functionality may not be available.");
        } else {
            chat.register(Pvp.class,this,2);
        }

        worlds = (Worlds)getServer().getPluginManager().getPlugin("Worlds");
        if (worlds == null) {
            getLogger().warning("Unable to load Worlds plugin. Some functionality may not be available.");
        }

        getLogger().info("Printing table data:");
        getLogger().info(HeadCount.getTableInfo());
        HeadCount.refreshCache(database,getLogger());
        getLogger().info(UniqueHead.getTableInfo());
        UniqueHead.refreshCache(database,getLogger());

        getLogger().info("Pvp loaded");
    }

    @Override
    public void onDisable() {
        getLogger().info("Pvp disable");
    }

    @EventHandler
    public void killed(PlayerDeathEvent event) {
        if (worlds != null && worlds.getPlayerWorldType(event.getEntity().getName()) == Worlds.VANILLA) {
            return;
        }
        String message = event.getDeathMessage();
        //getLogger().info("Death: " + message);
        String name = null;
        if (message.contains("slain by")) {
            //getLogger().info("killed by player");
            String[] parts = message.split("slain by ");
            message = parts[1];
            //getLogger().info("message now:" + message);
            parts = message.split(" ");
            message = parts[0];
            //getLogger().info("message 2: " + message);
            name = message;
        } else if (message.contains("shot by")) {
            getLogger().info("killed by player");
            String[] parts = message.split("shot by ");
            message = parts[1];
            //getLogger().info("message now:" + message);
            parts = message.split(" ");
            message = parts[0];
            //getLogger().info("message 2: " + message);
            name = message;
        }

        if (name != null) {
            Player p = getServer().getPlayer(name);
            if (p != null) {
                ItemStack is = itemName.getItemFromName("STEVE_HEAD");
                SkullMeta meta = (SkullMeta)is.getItemMeta();
                String killed = event.getEntity().getName();
                meta.setOwner(killed);
                meta.setDisplayName(killed + "'s head");
                is.setItemMeta(meta);
                itemName.giveItem(p,is,1);
                HeadCount count = HeadCount.getHeadsForPlayer(p.getName());
                if (count == null) {
                    count = new HeadCount();
                    count.setPlayer(p.getName());
                    count.setHeadCount(0);
                }

                count.setHeadCount(count.getHeadCount() + 1);
                count.save(database);

                // add unique
                boolean found = false;
                List<UniqueHead> heads = UniqueHead.getUniqueHeadsForPlayer(p.getName());
                for (UniqueHead head : heads) {
                    if (head.getKilled().equalsIgnoreCase(killed)) {
                        found = true;
                    }
                }
                if (!found) {
                    UniqueHead h = new UniqueHead();
                    h.setPlayer(p.getName());
                    h.setKilled(killed);
                    h.save(database);
                }
            }
        }
    }

    public void doChat(ChatData cd) {
        int count = 0;

        HeadCount hc = HeadCount.getHeadsForPlayer(cd.getPlayer());
        if (hc != null) {
            count = hc.getHeadCount();
        }
        List<UniqueHead> heads = UniqueHead.getUniqueHeadsForPlayer(cd.getPlayer());
        int unique = heads.size();

        if (count > 0) {
            cd.setPlayerString(":purple:[" + count + ":" + unique + "]:reset: " + cd.getPlayerString());
        }
    }
}
