package org.bukkit;

import PluginReference.MC_Player;
import org.bukkit.entity.Player;

/**
 * Created by solum on 12/21/2014.
 */
public class OfflinePlayer extends Player {
    public OfflinePlayer(MC_Player p) {
        super(p);
    }

    public Player getPlayer() {
        return (Player)this;
    }
}
