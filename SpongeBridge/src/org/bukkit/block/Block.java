package org.bukkit.block;

import org.bukkit.Material;
import org.spongepowered.api.item.ItemType;

/**
 * Created by solum on 1/2/2016.
 */
public class Block {
    org.spongepowered.api.block.BlockSnapshot snapshot;

    public Block(org.spongepowered.api.block.BlockSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    public Material getType() {
        org.spongepowered.api.block.BlockState state = snapshot.getState();
        ItemType type = state.getType().getItem().orElse(null);
        if (type == null) {
            System.out.println("Did not get a type for block state");
            return null;
        }
        return Material.getValueOf(new org.bukkit.inventory.ItemType(type));
    }
}
