package org.bukkit.event.player;

import SpongeBridge.SpongeBridge;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Created by solum on 5/24/2015.
 */
public class PlayerJoinEvent extends Event {
    private Player player;

    public PlayerJoinEvent(SpongeBridge bridge, org.spongepowered.api.event.network.ClientConnectionEvent.Join event) {
        try {
            this.player = new Player(event.getTargetEntity());
        } catch (Exception e) {
            bridge.getLogger().logError("Can't add player object",e.getStackTrace());
        }
    }

    public Player getPlayer() {
        return player;
    }
}
