package org.bukkit.event.block;

import SpongeBridge.SpongeBridge;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Created by solum on 1/10/2016.
 */
public class BlockBreakEvent extends Event {
    private org.spongepowered.api.event.block.ChangeBlockEvent.Break event;

    public BlockBreakEvent(SpongeBridge bridge, org.spongepowered.api.event.block.ChangeBlockEvent.Break event) {
        this.event = event;
    }

    public Player getPlayer() {
        org.spongepowered.api.entity.living.player.Player player = event.getCause().first(org.spongepowered.api.entity.living.player.Player.class).orElse(null);
        if (player == null) {
            return null;
        }
        return new Player(player);
    }

    public boolean isCancelled() {
        return event.isCancelled();
    }
}
