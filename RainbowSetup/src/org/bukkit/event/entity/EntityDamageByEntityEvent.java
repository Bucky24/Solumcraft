package org.bukkit.event.entity;

import org.bukkit.entity.Entity;

/**
 * Created by solum on 2/15/2015.
 */
public class EntityDamageByEntityEvent {
    Entity damager;
    Entity damaged;

    public EntityDamageByEntityEvent(Entity damager, Entity damaged) {
        this.damager = damager;
        this.damaged = damaged;
    }

    public Entity getDamager() {
        return damager;
    }

    public Entity getEntity() {
        return damaged;
    }
}
