package org.bukkit;

import com.flowpowered.math.vector.Vector3i;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.world.extent.Extent;

import java.util.Optional;

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

    public org.spongepowered.api.world.Location getSpongeLocation(double x, double y, double z) {
        return this.world.getLocation(x,y,z);
    }

    /*public Block getBlockAt(Location l) throws Exception {
        org.spongepowered.api.world.Location loc = this.world.getLocation((int)l.getX(),(int)l.getY(),(int)l.getZ());
    }*/

    public Location getSpawnLocation() {
        return new Location(this.world.getSpawnLocation(),this.world);
    }

    public void setSpawnLocation(int x, int y, int z) {
        this.world.getProperties().setSpawnPosition(new Vector3i(x, y, z));
    }

    public void dropItem(Location l, ItemStack is) {
        Extent extent = l.getSpongeLocation().getExtent();
        org.spongepowered.api.entity.Entity entity = extent
                .createEntity(EntityTypes.EGG, l.getSpongeLocation().getPosition());
        entity.offer(Keys.REPRESENTED_ITEM, is.getStack().createSnapshot());
        extent.spawnEntity(entity);
    }

    public long getSeed() {
        return this.world.getProperties().getSeed();
    }
}
