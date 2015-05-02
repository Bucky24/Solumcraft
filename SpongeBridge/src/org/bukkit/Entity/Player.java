package org.bukkit.entity;

import SpongeBridge.SpongeText;
import org.bukkit.command.CommandSender;

/**
 * Created by solum on 5/2/2015.
 */
public class Player extends CommandSender {
    org.spongepowered.api.entity.player.Player serverPlayer;

    public Player(org.spongepowered.api.entity.player.Player player) {
        this.serverPlayer = player;
    }

    public String getName() {
        return this.serverPlayer.getName();
    }

    public void sendMessage(String message) {
        this.serverPlayer.sendMessage(SpongeText.getText(message));
    }
}
