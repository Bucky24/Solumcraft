package org.bukkit.entity;

import SpongeBridge.SpongeText;
import org.bukkit.Text;
import org.bukkit.command.CommandSender;

import java.util.UUID;

/**
 * Created by solum on 5/2/2015.
 */
public class Player implements CommandSender {
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

    public void sendMessage(Text text) {
        this.serverPlayer.sendMessage(SpongeText.getText(text));
    }

    public UUID getUniqueId() {
        return this.serverPlayer.getUniqueId();
    }
}
