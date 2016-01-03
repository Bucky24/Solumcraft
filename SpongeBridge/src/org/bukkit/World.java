package org.bukkit;

import org.bukkit.block.Block;
import org.spongepowered.api.world.Chunk;

/**
 * Created by solum on 1/2/2016.
 */
public class World {
    private org.spongepowered.api.world.World world;

    public World(org.spongepowered.api.world.World world) {
        this.world = world;
    }

    public String getName() {
        return this.world.getName();
    }

    public org.spongepowered.api.world.Location getSpongeLocation(double x, double y, double z) throws Exception {
        return this.world.getLocation(x,y,z);
    }

    /*public Block getBlockAt(Location l) throws Exception {
        org.spongepowered.api.world.Location loc = this.world.getLocation((int)l.getX(),(int)l.getY(),(int)l.getZ());
    }*/
}
