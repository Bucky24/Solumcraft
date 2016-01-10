package org.bukkit.inventory;

/**
 * Created by solum on 12/30/2015.
 */
public class ItemType {
    org.spongepowered.api.item.ItemType itemType;

    public ItemType(org.spongepowered.api.item.ItemType type) {
        this.itemType = type;
    }

    public String name() {
        return itemType.getName();
    }

    public org.spongepowered.api.item.ItemType getType() {
        return itemType;
    }
}
