package org.bukkit.entity;

import org.spongepowered.api.entity.*;
import org.spongepowered.api.event.cause.Cause;

import java.util.HashMap;
import java.util.Map;

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
    RABBIT(EntityTypes.RABBIT),
    PLAYER(EntityTypes.PLAYER);

    private org.spongepowered.api.entity.EntityType type;
    private static Map<Integer,EntityType> durabilityMap;
    EntityType(org.spongepowered.api.entity.EntityType type) { this.type = type; }
    public org.spongepowered.api.entity.EntityType getValue() { return type; }

   /* dataList.add(new ItemData("CREEPER_EGG",Material.getMaterial("minecraft:spawn_egg"),50));
    dataList.add(new ItemData("SKELE_EGG",Material.getMaterial("minecraft:spawn_egg"),51));
    dataList.add(new ItemData("SPIDER_EGG",Material.getMaterial("minecraft:spawn_egg"),52));
    dataList.add(new ItemData("ZOMBIE_EGG",Material.getMaterial("minecraft:spawn_egg"),54));
    dataList.add(new ItemData("SLIME_EGG",Material.getMaterial("minecraft:spawn_egg"),55));
    dataList.add(new ItemData("GHAST_EGG",Material.getMaterial("minecraft:spawn_egg"),56));
    dataList.add(new ItemData("ZOMBIE_PIG_EGG",Material.getMaterial("minecraft:spawn_egg"),57));
    dataList.add(new ItemData("ENDERMAN_EGG",Material.getMaterial("minecraft:spawn_egg"),58));
    dataList.add(new ItemData("CAVE_SPIDER_EGG",Material.getMaterial("minecraft:spawn_egg"),59));
    dataList.add(new ItemData("SLIVERFISH_EGG",Material.getMaterial("minecraft:spawn_egg"),60));
    dataList.add(new ItemData("BLAZE_EGG",Material.getMaterial("minecraft:spawn_egg"),61));
    dataList.add(new ItemData("MAGMA_EGG",Material.getMaterial("minecraft:spawn_egg"),62));
    dataList.add(new ItemData("BAT_EGG",Material.getMaterial("minecraft:spawn_egg"),65));
    dataList.add(new ItemData("WITCH_EGG",Material.getMaterial("minecraft:spawn_egg"),66));
    dataList.add(new ItemData("PIG_EGG",Material.getMaterial("minecraft:spawn_egg"),90));
    dataList.add(new ItemData("SHEEP_EGG",Material.getMaterial("minecraft:spawn_egg"),91));
    dataList.add(new ItemData("COW_EGG",Material.getMaterial("minecraft:spawn_egg"),92));
    dataList.add(new ItemData("CHICKEN_EGG",Material.getMaterial("minecraft:spawn_egg"),93));
    dataList.add(new ItemData("SQUID_EGG",Material.getMaterial("minecraft:spawn_egg"),94));
    dataList.add(new ItemData("WOLF_EGG",Material.getMaterial("minecraft:spawn_egg"),95));
    dataList.add(new ItemData("MOOSHROOM_EGG",Material.getMaterial("minecraft:spawn_egg"),96));
    dataList.add(new ItemData("CAT_EGG",Material.getMaterial("minecraft:spawn_egg"),98));
    dataList.add(new ItemData("HORSE_EGG",Material.getMaterial("minecraft:spawn_egg"),100));
    dataList.add(new ItemData("VILLAGER_EGG",Material.getMaterial("minecraft:spawn_egg"),120));*/

    public static void init() {
        EntityType.durabilityMap = new HashMap<Integer, EntityType>();
        EntityType.durabilityMap.put(50,CREEPER);

        EntityType.durabilityMap.put(93,CHICKEN);
    }

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

    public static EntityType getForDurability(int durability) {
        if (EntityType.durabilityMap.containsKey(durability)) {
            return EntityType.durabilityMap.get(durability);
        }
        return null;
    }
}

