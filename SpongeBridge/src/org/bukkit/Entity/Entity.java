package org.bukkit.entity;

import org.bukkit.Location;
import org.spongepowered.api.entity.*;

/**
 * Created by solum on 5/2/2015.
 */
public class Entity {
    protected org.spongepowered.api.entity.Entity entity;

    public Entity() {
        this.entity = null;
    }

    public Entity(org.spongepowered.api.entity.Entity entity) {
        this.entity = entity;
    }

    public EntityType getType() {
        return EntityType.getValueOf(entity.getType());
    }

    public Location getLocation() {
        return new Location(entity.getLocation(),entity.getWorld());
    }

    public void remove() {
        entity.remove();
    }
}
