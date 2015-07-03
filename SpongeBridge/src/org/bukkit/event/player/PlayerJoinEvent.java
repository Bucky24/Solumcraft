package org.bukkit.event.player;

import SpongeBridge.SpongeBridge;
import org.bukkit.entity.Player;

/**
 * Created by solum on 5/24/2015.
 */
public class PlayerJoinEvent {
    private Player player;

    public PlayerJoinEvent(SpongeBridge bridge, org.spongepowered.api.event.entity.player.PlayerJoinEvent event) {
        try {
            this.player = new Player(event.getEntity());
        } catch (Exception e) {
            bridge.getLogger().logError("Can't add player object",e.getStackTrace());
        }
    }

    public Player getPlayer() {
        return player;
    }
}
