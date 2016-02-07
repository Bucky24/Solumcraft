package org.bukkit.entity;

import org.spongepowered.api.entity.*;
import org.spongepowered.api.event.cause.Cause;

/**
 * Created by solum on 1/10/2016.
 */
public enum EntityType {
    CREEPER(EntityTypes.CREEPER),
    ZOMBIE(EntityTypes.ZOMBIE),
    COW(EntityTypes.COW),
    HORSE(EntityTypes.HORSE),
    PIG(EntityTypes.PIG),
    WOLF(EntityTypes.WOLF),
    MUSHROOM_COW(EntityTypes.MUSHROOM_COW),
    SHEEP(EntityTypes.SHEEP),
    CHICKEN(EntityTypes.CHICKEN),
    RABBIT(EntityTypes.RABBIT);

    private final org.spongepowered.api.entity.EntityType type;
    EntityType(org.spongepowered.api.entity.EntityType type) { this.type = type; }
    public org.spongepowered.api.entity.EntityType getValue() { return type; }
    public static EntityType getForCause(Cause cause) {
        org.spongepowered.api.entity.Entity entity = cause.first(org.spongepowered.api.entity.Entity.class).orElse(null);
        if (entity == null) {
            return null;
        }
        return EntityType.getValueOf(entity.getType());
    }
    public static EntityType getValueOf(org.spongepowered.api.entity.EntityType type) {
        for (EntityType e : EntityType.values()) {
            if (e.getValue().equals(type)) {
                return e;
            }
        }
        return null;
    }
}

