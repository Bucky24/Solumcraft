package org.bukkit.entity;

import org.spongepowered.api.entity.*;

/**
 * Created by solum on 5/2/2015.
 */
public class Entity {
    private org.spongepowered.api.entity.Entity entity;

    public Entity(org.spongepowered.api.entity.Entity entity) {
        this.entity = entity;
    }

    public EntityType getType() {
        return EntityType.getValueOf(entity.getType());
    }
}
