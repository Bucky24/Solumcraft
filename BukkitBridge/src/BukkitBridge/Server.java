package BukkitBridge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Server {
    private org.bukkit.Server server;
    public Server(org.bukkit.Server server) {
        this.server = server;
    }

    public List<Player> getOnlinePlayers() {
        ArrayList<Player> playerList = new ArrayList<>();
        for (org.bukkit.entity.Player p : this.server.getOnlinePlayers()) {
            playerList.add(new Player(p));
        }
        return playerList;
    }

    public void broadcastMessage(Text text) {
        this.server.broadcastMessage(text.getPlainText());
    }
}
