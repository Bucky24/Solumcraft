package org.bukkit.command;

import SpongeBridge.SpongeBridge;
import SpongeBridge.SpongeText;
import org.bukkit.Text;
import org.bukkit.entity.Entity;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;

/**
 * Created by solum on 5/2/2015.
 */
public class CommandSender extends Entity {
    CommandSource source;

    public CommandSender(CommandSource source) {
        this.source = source;
    }

    public void sendMessage(String message) {
        source.sendMessage(SpongeText.getText(message));
    }

    public void sendMessage(Text message) {
        source.sendMessage(SpongeText.getText(message));
    }
}
