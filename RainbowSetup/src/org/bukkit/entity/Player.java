package org.bukkit.entity;

import PluginReference.MC_Player;
import org.bukkit.command.CommandSender;

import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 12/13/14
 * Time: 7:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class Player extends CommandSender {
    public MC_Player player;

    public Player(MC_Player p) {
        player = p;
    }

    public void sendMessage(String message) {
        player.sendMessage(message);
    }

    public String getName() {
        return player.getName();
    }

    public UUID getUniqueId() {
        return player.getUUID();
    }
}
