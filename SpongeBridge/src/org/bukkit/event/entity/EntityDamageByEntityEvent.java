package org.bukkit.event.entity;

import SpongeBridge.SpongeBridge;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;

/**
 * Created by solum on 2/7/2016.
 */
public class EntityDamageByEntityEvent extends Event {
    private DamageEntityEvent event;
    private SpongeBridge bridge;

    public EntityDamageByEntityEvent(SpongeBridge bridge, DamageEntityEvent event) {
        this.event = event;
        this.bridge = bridge;
    }

    public Entity getDamager() {
        Cause cause = event.getCause();
        EntityDamageSource source = (EntityDamageSource)cause.root();
        org.spongepowered.api.entity.Entity entity = source.getSource();
        if (entity instanceof org.spongepowered.api.entity.living.player.Player) {
            return new Player((org.spongepowered.api.entity.living.player.Player)entity);
        }

        try {
            throw new Exception("STUB: Cannot get damager, this function can only return Player damagers right now.");
        } catch (Exception e) {
            bridge.getLogger().logError(e);
        }
        return null;
    }

    public Entity getEntity() {
        return new Entity(event.getTargetEntity());
    }

    public void setCancelled(boolean canceled) {
        event.setCancelled(canceled);
    }
}
