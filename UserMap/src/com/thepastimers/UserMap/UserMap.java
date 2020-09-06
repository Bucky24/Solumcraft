package com.thepastimers.UserMap;

import com.thepastimers.Database.Database;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

/**
 * Created by rwijtman on 3/7/14.
 */
public class UserMap extends JavaPlugin implements Listener {
    Database database;

    public static String NO_USER = "";

    @Override
    public void onEnable() {
        getLogger().info("UserMap init");

        getServer().getPluginManager().registerEvents(this,this);

        database = (Database)getServer().getPluginManager().getPlugin("Database");
        if (database == null) {
            getLogger().warning("Unable to load Database plugin. Some functionality will not be available.");
        }

        UserMapping.init(database);
        getLogger().info("Table info: ");
        getLogger().info(UserMapping.getTableInfo());
        UserMapping.refreshCache(database, getLogger());

        // now map every user that's currently on the server

        for (Player p : getServer().getOnlinePlayers()) {
            updateUUID(p);
        }

        getLogger().info("UserMap init complete");
    }

    @Override
    public void onDisable() {
        getLogger().info("UserMap disabled");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        getLogger().info("Updating uuid for " + event.getPlayer().getName());
        updateUUID(event.getPlayer());
    }

    public UserMapping getUUIDObject(Player player) {
        if (player == null) {
            return null;
        }
        return UserMapping.getMappingForPlayer(player);
    }

    public String getUUID(Player player) {
        if (player == null) {
            return NO_USER;
        }
        UserMapping um = UserMapping.getMappingForPlayer(player);
        if (um == null) {
            return NO_USER;
        }
        return um.getUuid();
    }

    public String getUUID(String player) {
        if (player == null) {
            return NO_USER;
        }

        Player p = getServer().getPlayer(player);
        if (p != null) {
            return p.getUniqueId().toString();
        }

        OfflinePlayer[] ops = Bukkit.getOfflinePlayers();
        for (OfflinePlayer op : ops) {
            Player pop = op.getPlayer();
            if (pop != null && pop.getName().equals(player)) {
                return pop.getUniqueId().toString();
            }
        }

        UserMapping um = UserMapping.getMappingForPlayer(player);
        if (um == null) {
            um = UserMapping.getPlayerForMapping(player);
        }
        return um == null? NO_USER : um.getUuid();
    }

    public UUID getId(String uuid) {
        return UUID.fromString(uuid);
    }

    public void updateUUID(Player p) {
        if (p == null) return;
        UserMapping um = getUUIDObject(p);
        if (um == null) {
            um = new UserMapping();
        }
        um.setUserName(p.getName());
        um.setUuid(p.getUniqueId().toString());
        if (!um.save(database)) {
            getLogger().warning("Warning: cannot update player's user mapping for " + p.getName());
        }
    }

    public String getPlayer(String uuid) {
        UserMapping map = UserMapping.getPlayerForMapping(uuid);
        if (map == null) {
            return UserMap.NO_USER;
        }
        return map.getUserName();
    }
}
