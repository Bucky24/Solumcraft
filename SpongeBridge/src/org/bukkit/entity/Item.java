package org.bukkit.entity;

public class Item extends Entity {
    org.spongepowered.api.entity.Item item;
    public Item(org.spongepowered.api.entity.Item item) {
        this.item = item;
    }

    public void remove() {
        item.remove();
    }
}
