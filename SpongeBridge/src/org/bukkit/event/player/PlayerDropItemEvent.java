package org.bukkit.event.player;

import SpongeBridge.SpongeBridge;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.item.ItemType;

import java.util.List;

public class PlayerDropItemEvent extends Event {
    SpongeBridge bridge;
    org.spongepowered.api.event.item.inventory.DropItemEvent.Dispense event;
    public PlayerDropItemEvent(SpongeBridge bridge, org.spongepowered.api.event.item.inventory.DropItemEvent.Dispense event) {
        this.event = event;
        this.bridge = bridge;
    }

    public Player getPlayer() {
        org.spongepowered.api.entity.living.player.Player player = this.event.getCause().first(org.spongepowered.api.entity.living.player.Player.class).orElse(null);
        Player bukkitPlayer = new Player(player);

        return bukkitPlayer;
    }

    public Item getItemDrop() {
        List<Entity> entities = event.getEntities();
        for (Entity e : entities) {
            org.spongepowered.api.entity.Item item = (org.spongepowered.api.entity.Item)e;
            // return the first one we find
            return new Item(item);
        }

        return null;
    }
}
