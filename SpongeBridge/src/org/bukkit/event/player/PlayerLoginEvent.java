package org.bukkit.event.player;

import SpongeBridge.SpongeBridge;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class PlayerLoginEvent extends Event {
    Player player;

    public PlayerLoginEvent(SpongeBridge bridge, org.spongepowered.api.event.network.ClientConnectionEvent.Login event) {
        try {
            this.player = Player.fromUser(event.getTargetUser());
        } catch (Exception e) {
            bridge.getLogger().logError("Can't add player object",e.getStackTrace());
        }
    }

    public Player getPlayer() {
        return this.player;
    }
}
