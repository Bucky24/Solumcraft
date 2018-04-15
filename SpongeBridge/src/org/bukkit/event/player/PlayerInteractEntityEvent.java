package org.bukkit.event.player;

import SpongeBridge.SpongeBridge;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.spongepowered.api.event.cause.Cause;

public class PlayerInteractEntityEvent extends Event {
    private org.spongepowered.api.event.entity.InteractEntityEvent.Secondary event;

    public PlayerInteractEntityEvent(SpongeBridge bridge, org.spongepowered.api.event.entity.InteractEntityEvent.Secondary event) {
        this.event = event;
    }

    public Entity getRightClicked() {
        return new Entity(event.getTargetEntity());
    }

    public Player getPlayer() {
        Cause c = event.getCause();
        org.spongepowered.api.entity.living.player.Player player = c.first(org.spongepowered.api.entity.living.player.Player.class).orElse(null);
        return new Player(player);
    }

    public void setCancelled(boolean cancelled) {
        event.setCancelled(cancelled);
    }
}
