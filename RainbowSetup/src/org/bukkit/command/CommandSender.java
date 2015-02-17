package org.bukkit.command;

import PluginReference.MC_Player;
import org.bukkit.entity.Player;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 12/13/14
 * Time: 7:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class CommandSender extends Player {

    public CommandSender(MC_Player player) {
        super(player);
    }

}
