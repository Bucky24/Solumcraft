package org.bukkit.event.entity;

import org.bukkit.event.Event;
import org.spongepowered.api.event.world.ExplosionEvent;

/**
 * Created by solum on 1/10/2016.
 */
public class EntityExplodeEvent extends Event {
    private ExplosionEvent.Pre event;
    public EntityExplodeEvent(ExplosionEvent.Pre event) {
        this.event = event;
    }
}
