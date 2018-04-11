package org.bukkit;

import org.spongepowered.api.entity.living.player.gamemode.GameModes;

public enum GameMode {
    CREATIVE(GameModes.CREATIVE),
    ADVENTURE(GameModes.ADVENTURE),
    SURVIVAL(GameModes.SURVIVAL);

    private final org.spongepowered.api.entity.living.player.gamemode.GameMode mode;
    GameMode(org.spongepowered.api.entity.living.player.gamemode.GameMode mode) { this.mode = mode; }
    public org.spongepowered.api.entity.living.player.gamemode.GameMode getValue() { return mode; }

    public static GameMode getValueOf(org.spongepowered.api.entity.living.player.gamemode.GameMode mode) {
        for (GameMode gm : GameMode.values()) {
            if (gm.getValue().equals(mode)) {
                return gm;
            }
        }
        return null;
    }
}
