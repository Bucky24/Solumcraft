package org.bukkit.inventory;

import org.bukkit.Material;

/**
 * Created by solum on 12/30/2015.
 */
public class ItemType {
    org.spongepowered.api.item.ItemType itemType;

    public ItemType(org.spongepowered.api.item.ItemType type) {
        this.itemType = type;
    }

    public ItemType(Material mat) {
        this.itemType = mat.getValue().itemType;
    }

    public org.spongepowered.api.item.ItemType getItemType() {
        return itemType;
    }

    public String name() {
        return itemType.getName();
    }

    public boolean equals(ItemType other) {
        return other.itemType == this.itemType;
    }
}
