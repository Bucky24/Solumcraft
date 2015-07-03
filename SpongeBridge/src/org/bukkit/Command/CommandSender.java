package org.bukkit.command;

import SpongeBridge.SpongeText;
import org.bukkit.Text;
import org.bukkit.entity.Entity;
import org.spongepowered.api.util.command.CommandSource;

/**
 * Created by solum on 5/2/2015.
 */
public class CommandSender implements Entity {
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
