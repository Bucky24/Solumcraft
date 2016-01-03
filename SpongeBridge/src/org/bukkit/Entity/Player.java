package org.bukkit.entity;

import org.bukkit.Location;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.command.CommandSender;

import java.util.UUID;

/**
 * Created by solum on 5/2/2015.
 */
public class Player extends CommandSender {
    org.spongepowered.api.entity.player.Player serverPlayer;

    public Player() {
        super(null);
        serverPlayer = null;
    }

    public Player(org.spongepowered.api.util.command.CommandSource source) throws Exception {
        super(source);
        if (source instanceof org.spongepowered.api.entity.player.Player) {
            this.serverPlayer = (org.spongepowered.api.entity.player.Player)source;
        } else {
            throw new Exception("Player constructor called with CommandSource that was not a Player. Class is: " + source.getClass().getName());
        }
    }

    public String getName() {
        return this.serverPlayer.getName();
    }

    public UUID getUniqueId() {
        return this.serverPlayer.getUniqueId();
    }

    public PlayerInventory getInventory() {
        return new PlayerInventory(serverPlayer.getInventory());
    }

    public Location getLocation() {
        return new Location(this.serverPlayer.getLocation(),this.serverPlayer.getWorld());
    }

    public void teleport(Location l) throws Exception {
        this.serverPlayer.setLocation(l.getWorld().getSpongeLocation((int) l.getX(), (int) l.getY(), (int) l.getZ()));
    }

    public void teleport(Player p) throws Exception {
        this.teleport(p.getLocation());
    }
}
