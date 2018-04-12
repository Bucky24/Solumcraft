package org.bukkit.event.entity;

import SpongeBridge.SpongeBridge;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.spongepowered.api.entity.living.Creature;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKey;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.cause.entity.spawn.SpawnType;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class CreatureSpawnEvent extends Event {
    public enum SpawnReason {
        SPAWNER_EGG(1),
        BUILD_SNOWMAN(2),
        BUILD_IRONGOLEM(3),
        BUILD_WITHER(4);

        private final Integer type;
        SpawnReason(Integer type) { this.type = type; }
        public Integer getValue() { return type; }
    }

    org.spongepowered.api.event.entity.SpawnEntityEvent event;

    public CreatureSpawnEvent(SpongeBridge bridge, org.spongepowered.api.event.entity.SpawnEntityEvent event) {
        this.event = event;
    }

    public SpawnReason getSpawnReason() {
        Cause c = event.getCause();
        EventContext context = c.getContext();
        Map<EventContextKey<?>, Object> map = context.asMap();
        for (EventContextKey key : map.keySet()) {
            String keyName = key.getId();

            //System.out.println(keyName);

            if ("sponge:used_item".equals(keyName)) {
                ItemStackSnapshot snapshot = (ItemStackSnapshot)map.get(key);
                org.spongepowered.api.item.ItemType type = snapshot.getType();
                if (type.equals(ItemTypes.SPAWN_EGG)) {
                    return SpawnReason.SPAWNER_EGG;
                } else if(type.equals(ItemTypes.PUMPKIN)) {
                    return SpawnReason.BUILD_SNOWMAN;
                } else if (type.equals(ItemTypes.IRON_BLOCK)) {
                    return SpawnReason.BUILD_IRONGOLEM;
                } else {
                   // System.out.println("Spawned by " + type.getName());
                }
            }
        }

        return null;
    }

    public void setCancelled(boolean cancelled) {
        event.setCancelled(cancelled);
    }

    public Player getPlayer() {
        Cause c = event.getCause();
        EventContext context = c.getContext();
        Map<EventContextKey<?>, Object> map = context.asMap();
        for (EventContextKey key : map.keySet()) {
            String keyName = key.getId();

            if ("sponge:owner".equals(keyName)) {
                org.spongepowered.api.entity.living.player.Player player = (org.spongepowered.api.entity.living.player.Player)map.get(key);
                return new Player(player);
            }
        }

        return null;
    }
}
