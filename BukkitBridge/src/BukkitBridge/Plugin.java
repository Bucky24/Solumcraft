package BukkitBridge;

import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {
    Logger logger;
    Server server;
    public Logger logger() {
        if (this.logger == null) {
            this.logger = new Logger(this, super.getLogger());
        }
        return this.logger;
    }

    public Server server() {
        if (this.server == null) {
            this.server = new Server(super.getServer());
        }

        return this.server;
    }
}
