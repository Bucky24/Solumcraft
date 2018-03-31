package org.bukkit;

import org.bukkit.entity.Player;

/**
 * Created by solum on 5/24/2015.
 */
public class OfflinePlayer {
    private Player player;
    public OfflinePlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public String getName() {
        return player.getName();
    }
}
