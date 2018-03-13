package org.bukkit.event.entity;

import SpongeBridge.SpongeBridge;
import SpongeBridge.BlockList;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.spongepowered.api.event.world.ExplosionEvent;

/**
 * Created by solum on 1/10/2016.
 */
public class EntityExplodeEvent extends Event {
    private ExplosionEvent.Detonate event;
    public EntityExplodeEvent(SpongeBridge bridge, ExplosionEvent.Detonate event) {
        this.event = event;
    }

    public EntityType getEntityType() {
        return EntityType.getForCause(event.getCause());
    }

    public BlockList blockList() {
        return new BlockList(event.getAffectedLocations());
    }
}
