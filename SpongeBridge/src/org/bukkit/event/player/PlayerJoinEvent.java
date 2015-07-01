package org.bukkit.event.player;

import org.bukkit.entity.Player;

/**
 * Created by solum on 5/24/2015.
 */
public class PlayerJoinEvent {
    private Player player;

    public PlayerJoinEvent(org.spongepowered.api.event.entity.player.PlayerJoinEvent event) {
        this.player = new Player(event.getPlayer());
    }

    public Player getPlayer() {
        return player;
    }
}
