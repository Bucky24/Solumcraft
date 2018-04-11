package org.bukkit.event.block;

import SpongeBridge.SpongeBridge;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;

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

    public Block getBlock() {
        for (Transaction<BlockSnapshot> transaction : this.event.getTransactions()) {
            BlockSnapshot before = (BlockSnapshot)transaction.getOriginal(); // Block before change
            return new Block(before);
        }

        return null;
    }

    public boolean isCancelled() {
        return event.isCancelled();
    }

    public void setCancelled(boolean cancelled) {
        event.setCancelled(cancelled);
    }
}
