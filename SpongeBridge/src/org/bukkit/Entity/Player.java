package org.bukkit.entity;

import SpongeBridge.SpongeText;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.command.CommandSender;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.entity.GameModeData;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;

import java.util.UUID;

/**
 * Created by solum on 5/2/2015.
 */
public class Player extends CommandSender {
    org.spongepowered.api.entity.living.player.Player serverPlayer;

    public static Player fromUser(org.spongepowered.api.entity.living.player.User user) throws Exception {
        org.spongepowered.api.entity.living.player.Player player = user.getPlayer().orElse(null);
        if (player == null) {
            throw new Exception("Unable to get player object from user object");
        }
        return new Player(player);
    }

    public Player() {
        super(null);
        serverPlayer = null;
    }

    public Player(org.spongepowered.api.entity.living.player.Player player) {
        super(player);
        this.serverPlayer = player;
    }

    public Player(org.spongepowered.api.command.CommandSource source) throws Exception {
        super(source);
        if (source instanceof org.spongepowered.api.entity.living.player.Player) {
            this.serverPlayer = (org.spongepowered.api.entity.living.player.Player)source;
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

    public void teleport(Location l) {
        this.serverPlayer.setLocation(l.getWorld().getSpongeLocation(l.getX(), l.getY(), l.getZ()));
    }

    public void teleport(Player p) throws Exception {
        this.teleport(p.getLocation());
    }

    public World getWorld() {
        return new World(this.serverPlayer.getWorld());
    }

    public ItemStack getItemInHand() {
        return this.getItemInHand(HandTypes.MAIN_HAND);
    }

    public ItemStack getItemInHand(HandType type) {
        if (this.serverPlayer.getItemInHand(type).isPresent()) {
            return new ItemStack(this.serverPlayer.getItemInHand(type).get());
        } else {
            return new ItemStack(Material.AIR);
        }
    }

    public void setGameMode(GameMode mode) {
        this.serverPlayer.offer(Keys.GAME_MODE, mode.getValue());
    }

    public GameMode getGameMode() {
        org.spongepowered.api.entity.living.player.gamemode.GameMode mode = this.serverPlayer.gameMode().get();
        return GameMode.getValueOf(mode);
    }

    public boolean isOp() {
        // sponge has no concept of OP
        return false;
    }

    public void kickPlayer(String reason) {
        this.serverPlayer.kick(SpongeText.getText(Text.make().text(reason)));
    }
}
