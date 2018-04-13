package org.bukkit.event.player;

import SpongeBridge.SpongeBridge;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKey;

import java.util.Map;

public class PlayerInteractEvent extends Event {
    org.spongepowered.api.event.block.InteractBlockEvent event;

    public PlayerInteractEvent(SpongeBridge bridge, org.spongepowered.api.event.block.InteractBlockEvent event) {
        this.event = event;
    }

    public Block getClickedBlock() {
        return new Block(event.getTargetBlock());
    }

    public Player getPlayer() {
        Cause c = event.getCause();
        org.spongepowered.api.entity.living.player.Player player = c.first(org.spongepowered.api.entity.living.player.Player.class).orElse(null);
        return new Player(player);
    }

    public Action getAction() {
        if (event instanceof org.spongepowered.api.event.block.InteractBlockEvent.Primary) {
            return Action.LEFT_CLICK_BLOCK;
        }

        return null;
    }

    public void setCancelled(boolean cancelled) {
        event.setCancelled(cancelled);
    }
}
