package org.bukkit;

/**
 * Created by solum on 7/22/2015.
 */
public class Location {
    double x;
    double y;
    double z;
    World world;

    public Location(org.spongepowered.api.world.Location location,org.spongepowered.api.world.World world) {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.world = new World(world);
    }

    public Location(World world, double x, double y, double z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public World getWorld() {
        return this.world;
    }

    public org.spongepowered.api.world.Location getSpongeLocation() {
        return world.getSpongeLocation(this.x,this.y,this.z);
    }
}
