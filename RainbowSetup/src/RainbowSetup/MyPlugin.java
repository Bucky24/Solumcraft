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
import org.bukkit.event.player.PlayerJoinEvent;
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
    private Map<String, JavaPlugin> readyPluginMap;
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
        readyPluginMap = new HashMap<String, JavaPlugin>();

        logger = new Logger();

        this.loadPlugins();
    }

    @Override
    public void onServerFullyLoaded() {
        System.out.println("Server loaded, beginning to initialize plugins.");
        List<JavaPlugin> plugins = new ArrayList<JavaPlugin>();
        Iterator it = pluginMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            JavaPlugin plugin = (JavaPlugin)pair.getValue();
            plugins.add(plugin);
        }

        Object[] pluginList = plugins.toArray();

        for (int i=0;i<pluginList.length;i++) {
            JavaPlugin plugin = (JavaPlugin)pluginList[i];
            plugin.server = this;
            for (String dep : plugin.description.softDepends) {
                logger.info(plugin.description.name + " Depends on " + dep);
                if (!readyPluginMap.containsKey(dep)) {
                    logger.info("Dep not loaded!");
                    boolean found = false;
                    int foundIndex = -1;
                    for (int j=i;j<pluginList.length;j++) {
                        JavaPlugin p2 = (JavaPlugin)pluginList[j];
                        logger.info(p2.description.name);
                        if (p2.description.name.equals(dep)) {
                            found = true;
                            foundIndex = j;
                        }
                    }
                    if (found) {
                        logger.info("But we're loading it soon!");
                        // if the plugins are not dependent on each other, then swap them
                        // note this doesn't handle dependency circles, such as A -> B -> C -> A
                        // will have to improve it if that ever happens.
                        JavaPlugin p2 = (JavaPlugin)pluginList[foundIndex];
                        if (!p2.description.softDepends.contains(plugin.description.name)) {
                            logger.info("Swapping spaces!");
                            pluginList[i] = p2;
                            pluginList[foundIndex] = plugin;
                            plugin = p2;
                            logger.info("Now loading " + plugin.description.name);
                        }
                    }
                }
            }
            try {
                System.out.println("Enabling " + plugin.description.name);
                plugin.onEnable();
                readyPluginMap.put(plugin.description.name,plugin);
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

    @Override
    public void onPlayerJoin(MC_Player plr) {
        Iterator it = pluginMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            JavaPlugin plugin = (JavaPlugin)pair.getValue();
            try {
                List<Method> methods = this.getMethodsThatTakeEvent(plugin, PlayerJoinEvent.class);
                for (Method m : methods) {
                    PlayerJoinEvent event = new PlayerJoinEvent(new Player(plr));
                    m.invoke(plugin,event);
                }
            } catch (Exception e) {
                logger.logError(e);
            }
        }
    }

    ///////////////////////////////////
    // Official "server" functions
    ///////////////////////////////////

    public MyPlugin getPluginManager() {
        return this;
    }

    public JavaPlugin getPlugin(String plugin) {
        return readyPluginMap.get(plugin);
    }

    public void registerEvents(JavaPlugin plugin, Listener listener) {}

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

    public void broadcastMessage(String message) {
        server.broadcastMessage(message);
    }

    /////////////////////////
    // Other
    /////////////////////////

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

        File[] files = pluginDir.listFiles();

        for (File entry : files) {
            if (!entry.getName().endsWith(".jar")) {
                continue;
            }
            System.out.println("Plugin jar: " + entry.getName());

            try {
                //printJarContents(entry);

                PluginDescription description;
                try {
                    description = PluginDescription.getDescriptionForPlugin(entry);

                    // TODO: At this point we need to verify dependencies, since otherwise classes don't load properly.

                    PluginClassLoader loader = new PluginClassLoader(this, entry, getClass().getClassLoader(), description);
                    loaders.put(description.name, loader);
                    pluginMap.put(description.name,loader.plugin);
                } catch (Exception e) {
                    PluginClassLoader loader = new PluginClassLoader(this, entry, getClass().getClassLoader());
                    loaders.put(entry.getName(), loader);
                    logger.warning(e.getMessage());
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

    private List<Method> getMethodsThatTakeEvent(JavaPlugin plugin, Class comp) {
        List<Method> methods = new ArrayList<Method>();
        Class c = plugin.myClass;
        Method[] allMethods = c.getDeclaredMethods();
        for (Method m : allMethods) {
            Class<?>[] pType  = m.getParameterTypes();
            for (int i = 0; i < pType.length; i++) {
                if (comp.equals(pType[i])) {
                    methods.add(m);
                }
            }
        }

        return methods;
    }
}
