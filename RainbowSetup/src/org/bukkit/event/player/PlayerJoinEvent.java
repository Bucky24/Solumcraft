package org.bukkit.event.player;

import org.bukkit.entity.Player;

/**
 * Created by solum on 12/21/2014.
 */
public class PlayerJoinEvent {
    private Player player;

    public PlayerJoinEvent(Player p) {
        player = p;
    }

    public Player getPlayer() {
        return player;
    }
}
