package org.bukkit.command;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Created by solum on 5/2/2015.
 */
public interface CommandSender extends Entity {
    public abstract void sendMessage(String message);
}
