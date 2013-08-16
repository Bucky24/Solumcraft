package com.thepastimers.Chat;

import com.thepastimers.Database.Database;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 6/9/13
 * Time: 8:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetCommands extends BukkitRunnable {
    private final Database database;
    private final JavaPlugin plugin;

    public GetCommands(JavaPlugin plugin, Database d) {
        this.plugin = plugin;
        this.database = d;
    }

    public void run() {
        List<CommandData> data = (List<CommandData>)database.select(CommandData.class,"handled = 0 LIMIT 10",false);

        if (data == null) {
            plugin.getLogger().warning("Can't get commands.");
            return;
        }

        for (CommandData cd : data) {
            Chat c = (Chat)plugin;

            boolean found = false;
            for (String s : c.commandListeners.keySet()) {
                if (s.equalsIgnoreCase(cd.getCommand())) {
                    Map<Class,JavaPlugin> classMap = c.commandListeners.get(s);
                    Class c2 = (Class)classMap.keySet().toArray()[0];
                    try {
                        JavaPlugin p = classMap.get(c2);
                        Class[] argTypes = new Class[] {CommandData.class};
                        Method m = c2.getDeclaredMethod("handleCommand",argTypes);
                        m.invoke(p,cd);
                        found = true;
                    } catch (Exception e) {
                        c.getLogger().warning("Unable to call handleCommand for " + c2.getName());
                        c.getLogger().warning(e.getMessage());
                    }
                    break;
                }
            }

            if (!found) {
                cd.setResponse("Unknown command");
            }

            cd.setHandled(true);
            cd.setRead(false);
            cd.save(database);
        }
    }
}