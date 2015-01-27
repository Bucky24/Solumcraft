package RainbowSetup;

import PluginReference.MC_EventInfo;
import PluginReference.MC_Player;
import PluginReference.MC_Server;
import PluginReference.PluginBase;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 12/13/14
 * Time: 6:48 PM
 * To change this template use File | Settings | File Templates.
 */

public class MyPlugin extends PluginBase {
    private static String pluginDir = "bukkit_plugins";
    MC_Server server;
    private Map<String, JavaPlugin> pluginMap;
    private final Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
    private final Map<String, PluginClassLoader> loaders = new LinkedHashMap<String, PluginClassLoader>();

    Logger logger;

    ////////////////////////////
    // Rainbow functions
    ////////////////////////////
    @Override
    public void onStartup(MC_Server argServer) {
        System.out.println("RainbowSetup active!");

        server = argServer;

        pluginMap = new HashMap<String, JavaPlugin>();

        logger = new Logger();

        this.loadPlugins();
    }

    @Override
    public void onServerFullyLoaded() {
        System.out.println("Server loaded, beginning to initialize plugins.");
        Iterator it = pluginMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            JavaPlugin plugin = (JavaPlugin)pair.getValue();
            plugin.server = this;
            try {
                plugin.onEnable();
            } catch (Exception e) {
                logger.logError(e);
            }
        }
    }

    ///////////////////////////
    // Events
    ///////////////////////////

    @Override
    public void onPlayerInput(MC_Player plr, String msg, MC_EventInfo ei) {
        if (msg.startsWith("/")) {
            System.out.println("Got command! " + msg);
            msg = msg.substring(1);
            String[] commandArr = msg.split(" ");
            String command = commandArr[0];
            CommandSender sender = new Player(plr);
            commandArr = Arrays.copyOfRange(commandArr, 1, commandArr.length);
            Iterator it = pluginMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                JavaPlugin plugin = (JavaPlugin)pair.getValue();
                try {
                    plugin.onCommand(sender,new Command(command),command,commandArr);
                } catch (Exception e) {
                    logger.logError(e);
                }
            }
        }
    }

    /////////////////////////
    // Other
    /////////////////////////

    public MyPlugin getPluginManager() {
        return this;
    }

    public JavaPlugin getPlugin(String plugin) {
        logger.info("Getting plugin " + plugin);
        return pluginMap.get(plugin);
    }

    public void registerEvents(JavaPlugin plugin, Listener listener) {

    }

    public Player getPlayer(String player) {
        MC_Player p = server.getOnlinePlayerByName(player);
        if (p == null) return null;
        return new Player(p);
    }

    public OfflinePlayer getOfflinePlayer(String player) {
        List<MC_Player> players = server.getOfflinePlayers();
        for (MC_Player p : players) {
            if (p.getName().equals(player)) {
                return new OfflinePlayer(p);
            }
        }
        return null;
    }

    public List<Player> getOnlinePlayers() {
        List<MC_Player> players = server.getPlayers();
        List<Player> response = new ArrayList<Player>();
        for (MC_Player player : players) {
            Player p = new Player(player);
            response.add(p);
        }
        return response;
    }

    private void loadPlugins() {
        System.out.println("Loading plugins...");
        String cwd = System.getProperty("user.dir");
        System.out.println("Starting in " + cwd);
        File pluginDir = new File(cwd + "/" + MyPlugin.pluginDir);
        System.out.println("Plugin directory should be " + pluginDir.getAbsolutePath());
        if (!pluginDir.exists()) {
            System.out.println("Error, expected plugin directory " + pluginDir.getAbsolutePath() + " does not exist!");
            return;
        }

        for (File entry : pluginDir.listFiles()) {
            if (!entry.getName().endsWith(".jar")) {
                continue;
            }
            System.out.println("Plugin jar: " + entry.getName());

            try {
                //printJarContents(entry);

                PluginDescription description;
                try {
                    description = PluginDescription.getDescriptionForPlugin(entry);
                    PluginClassLoader loader = new PluginClassLoader(this, entry, getClass().getClassLoader(), description);
                    loaders.put(description.name, loader);
                    pluginMap.put(description.name,loader.plugin);
                } catch (Exception e) {
                    PluginClassLoader loader = new PluginClassLoader(this, entry, getClass().getClassLoader());
                    loaders.put(entry.getName(), loader);
                }


            } catch (Exception e) {
                System.out.println("Can't load jar " + entry.getAbsolutePath() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    Class<?> getClassByName(final String name, String handlerName) {
        //logger.info("MyPlugin.getClassByName: " + name);
        Class<?> cachedClass = classes.get(name);

        if (cachedClass != null) {
            return cachedClass;
        } else {
            for (String current : loaders.keySet()) {
                if (current.equals(handlerName)) continue;
                PluginClassLoader loader = loaders.get(current);

                try {
                    cachedClass = loader.findClass(name, false);
                } catch (ClassNotFoundException cnfe) {
                    //logger.info("MyPlugin.getClassByName: loader couldn't find " + name + ", got ClassNotFoundException");
                }
                if (cachedClass != null) {
                    return cachedClass;
                }
            }
        }
        return null;
    }

    void setClass(final String name, final Class<?> clazz) {
        if (!classes.containsKey(name)) {
            classes.put(name, clazz);
        }
    }

    private static void printJarContents(File entry) {
        try {
            URL myJarFile = new URL("jar:file:" + entry.getAbsolutePath() + "!/");
            JarURLConnection connection = (JarURLConnection) myJarFile.openConnection();
            JarFile jarFile = connection.getJarFile();
            Enumeration<JarEntry> jarEnum = jarFile.entries();
            while (jarEnum.hasMoreElements()) {
                try {
                    JarEntry jarEntry = jarEnum.nextElement();
                    System.out.println(jarEntry.getName());
                } catch (Exception e) {
                    System.out.println("Could not print entry");
                }
            }
        } catch (Exception e) {
            System.out.println("Can't print jar contents, got exception.");
        }
    }
}
