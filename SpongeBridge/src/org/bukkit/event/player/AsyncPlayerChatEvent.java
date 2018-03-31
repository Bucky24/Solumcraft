package org.bukkit.event.player;

import SpongeBridge.SpongeBridge;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.spongepowered.api.event.message.MessageChannelEvent;

public class AsyncPlayerChatEvent extends Event {
    private MessageChannelEvent.Chat event;

    public AsyncPlayerChatEvent(SpongeBridge bridge, MessageChannelEvent.Chat event) {
        this.event = event;
    }

    public String getMessage() {
        return event.getRawMessage().toPlain();
    }

    public Player getPlayer() {
        Object source = this.event.getSource();
        if (source instanceof org.spongepowered.api.entity.living.player.Player) {
            org.spongepowered.api.entity.living.player.Player spongePlayer = (org.spongepowered.api.entity.living.player.Player)source;
            return new Player(spongePlayer);
        } else {
            return null;
        }
    }

    public void setCancelled(boolean cancelled) {
        this.event.setCancelled(cancelled);
    }
}
