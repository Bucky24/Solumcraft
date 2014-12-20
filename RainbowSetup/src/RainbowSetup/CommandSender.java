package RainbowSetup;

import PluginReference.MC_Player;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 12/13/14
 * Time: 7:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class CommandSender {
    public MC_Player player;

    public CommandSender(MC_Player p) {
        player = p;
    }

    public void sendMessage(String message) {
        player.sendMessage(message);
    }

    public String getName() {
        return player.getName();
    }
}
