package org.bukkit;

import com.flowpowered.math.vector.Vector3i;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.world.Chunk;
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

    public void dropItem(Location l, ItemStack is) {
        /*
        Vector3i vector = new Vector3i(l.getX(),l.getY(),l.getZ());
        Item item = (Item) l.getWorld().world.createEntity(EntityTypes.DROPPED_ITEM, vector) ;
        item.offer(item.getItemData().set(is.getStack()));
        world.spawnEntity(item);*/
        Extent extent = l.getSpongeLocation().getExtent();
        Optional<org.spongepowered.api.entity.Entity> optional = extent.createEntity(EntityTypes.ITEM, l.getSpongeLocation().getBlockPosition());
        if (optional.isPresent()) {
            org.spongepowered.api.entity.Entity entity = optional.get();
            entity.offer(Keys.REPRESENTED_ITEM, is.getStack().createSnapshot());
            extent.spawnEntity(entity, Cause.of(this));
        }
    }
}
