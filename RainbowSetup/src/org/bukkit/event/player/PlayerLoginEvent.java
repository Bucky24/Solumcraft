package org.bukkit.event.player;

import org.bukkit.entity.Player;

/**
 * Created by solum on 12/21/2014.
 */
public class PlayerLoginEvent {
    Player player;
    String address;

    public PlayerLoginEvent(Player player, String address) {
        this.player = player;
        this.address = address;
    }

    public Player getPlayer() {
        return player;
    }

    public String getAddress() {
        return address;
    }
}
