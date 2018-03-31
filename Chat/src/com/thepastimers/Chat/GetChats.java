package com.thepastimers.Chat;

import com.thepastimers.Database.Database;
import org.bukkit.plugin.java.JavaPlugin;
//import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 6/9/13
 * Time: 8:04 PM
 * To change this template use File | Settings | File Templates.
 */
/*public class GetChats extends BukkitRunnable {
    private final Database database;
    private final JavaPlugin plugin;

    public GetChats(JavaPlugin plugin, Database d) {
        this.plugin = plugin;
        this.database = d;
    }

    public void run() {
        List<ChatData> data = (List<ChatData>)database.select(ChatData.class,"seen = 0 AND time > NOW() - interval 10 second",false);

        if (data == null) {
            plugin.getLogger().warning("Can't get chats.");
            return;
        }

        for (ChatData cd : data) {
            Chat c = (Chat)plugin;
            c.sendChat(cd.getMessage(),cd.getPlayer(),false,true);
            cd.setSeen(true);
            cd.save(database);
        }
    }
}*/