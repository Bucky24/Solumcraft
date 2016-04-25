package org.bukkit.entity;

import SpongeBridge.Logger;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.entity.AgeableData;
import org.spongepowered.api.data.value.mutable.MutableBoundedValue;

import java.util.Optional;

/**
 * Created by solum on 2/13/2016.
 */
public class Animals extends Entity {
    private static Logger logger;

    public static void init(Logger bridge) {
        Animals.logger = bridge;
    }

    public Animals(org.spongepowered.api.entity.Entity entity) {
        super(entity);
    }

    public boolean isAdult() {
        logger.warning("Animals::isAdult stub method-Sponge does not support animal aging yet");
        return true;
    }

    public boolean canBreed() {
        org.spongepowered.api.entity.living.animal.Animal animal = (org.spongepowered.api.entity.living.animal.Animal)this.entity;
        logger.warning("Animals::canBreed stub method-Sponge does not support breeding yet");
        return true;
    }
}
