package org.bukkit.block;

import org.bukkit.Material;

/**
 * Created by solum on 1/2/2016.
 */
public class Block {
    private Material type;

    public Block(org.spongepowered.api.world.Location location) {
        //this.type = new Material(location.getBlockType());
    }

    public Material getType() {
        return this.type;
    }
}
