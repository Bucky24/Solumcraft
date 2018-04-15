package org.bukkit.event.block;

import SpongeBridge.SpongeBridge;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;

public class BlockPlaceEvent extends Event {
    org.spongepowered.api.event.block.ChangeBlockEvent.Place event;

    public BlockPlaceEvent(SpongeBridge bridge, org.spongepowered.api.event.block.ChangeBlockEvent.Place event) {
        this.event = event;
    }

    public Player getPlayer() {
        org.spongepowered.api.entity.living.player.Player player = this.event.getCause().first(org.spongepowered.api.entity.living.player.Player.class).orElse(null);
        Player bukkitPlayer = new Player(player);

        return bukkitPlayer;
    }

    public Block getBlockPlaced() {
        for (Transaction<BlockSnapshot> transaction : this.event.getTransactions()) {
            BlockSnapshot after = (BlockSnapshot)transaction.getFinal(); // Block before change
            return new Block(after);
        }

        return null;
    }

    public Block getBlock() {
        return this.getBlockPlaced();
    }

    public void setCancelled(boolean cancelled) {
        this.event.setCancelled(cancelled);
    }
}
