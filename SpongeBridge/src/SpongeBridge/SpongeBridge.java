package SpongeBridge;

import org.bukkit.OfflinePlayer;
import org.bukkit.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.entity.player.PlayerJoinEvent;
import org.spongepowered.api.event.state.InitializationEvent;
import org.spongepowered.api.event.state.ServerStartedEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.command.CommandService;
import org.spongepowered.api.text.chat.ChatTypes;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by solum on 4/29/2015.
 */



@Plugin(id="SpongeBridge", name="Sponge Bridge", version="1.0")
public class SpongeBridge {
    private static String pluginDir = "bukkit_plugins";

    private Map<String, JavaPlugin> pluginMap;
    private List<JavaPlugin> pluginList;
    private Map<String, JavaPlugin> readyPluginMap;
    private final Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
    private final Map<String, PluginClassLoader> loaders = new LinkedHashMap<String, PluginClassLoader>();
    private final Map<String, CommandHandler> commandHandlers = new HashMap<String, CommandHandler>();
    public Game game;

    @Subscribe
    public void onServerInit(InitializationEvent event) {
        this.game = event.getGame();
        logger = new Logger();
        getLogger().info("SpongeBridge server start! Loading bukkit plugins now.");

        pluginMap = new HashMap<String, JavaPlugin>();
        readyPluginMap = new HashMap<String, JavaPlugin>();
        pluginList = new ArrayList<JavaPlugin>();

        this.loadPlugins();
        this.initPlugins();

        CommandHandler reloadHandler = new CommandHandler("reload",this);
    }

    private Logger logger;

    public Logger getLogger() {
        return logger;
    }

    /////////////////////////////////////////////////////////
    // Event handling
    /////////////////////////////////////////////////////////
    @Subscribe
    public void onPlayerJoin(PlayerJoinEvent event) {
        Iterator it = pluginMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            JavaPlugin plugin = (JavaPlugin)pair.getValue();
            try {
                org.bukkit.event.player.PlayerJoinEvent newEvent = new org.bukkit.event.player.PlayerJoinEvent(this,event);
                List<Method> methods = this.getMethodsThatTakeEvent(plugin, newEvent.getClass());
                for (Method m : methods) {
                    m.invoke(plugin,newEvent);
                }
            } catch (Exception e) {
                logger.logError(e);
            }
        }
    }

    ///////////////////////////////////////////////////////
    // Bukkit server methods
    //////////////////////////////////////////////////////

    public SpongeBridge getPluginManager() {
        return this;
    }

    public JavaPlugin getPlugin(String plugin) {
        return readyPluginMap.get(plugin);
    }

    public void registerEvents(JavaPlugin plugin, Listener listener) {}

    public List<Player> getOnlinePlayers() {
        List<Player> players = new ArrayList<Player>();

        Collection<org.spongepowered.api.entity.player.Player> realPlayers = game.getServer().getOnlinePlayers();
        for (Iterator<org.spongepowered.api.entity.player.Player> i = realPlayers.iterator(); i.hasNext();) {
            org.spongepowered.api.entity.player.Player player = i.next();
            try {
                players.add(new Player(player));
            } catch (Exception e) {
                logger.logError("Can't add player object",e.getStackTrace());
            }
        }

        return players;
    }

    public Player getPlayer(String name) {
        com.google.common.base.Optional<org.spongepowered.api.entity.player.Player> player = game.getServer().getPlayer(name);
        try {
            return new Player(player.get());
        } catch (Exception e) {
            logger.logError(e);
        }
        return null;
    }

    public OfflinePlayer getOfflinePlayer(String name) {
        List<Player> players = getOnlinePlayers();
        for (Player player : players) {
            if (player.getName().equals(name)) {
                return new OfflinePlayer(player);
            }
        }
        return null;
    }

    public void broadcastMessage(Text text) {
        for (org.spongepowered.api.entity.player.Player p : game.getServer().getOnlinePlayers()) {
            p.sendMessage(SpongeText.getText(text));
        }
    }

    ///////////////////////////////////////////////////////
    // Bridge server methods
    ///////////////////////////////////////////////////////

    public boolean handleCommand(org.spongepowered.api.util.command.CommandSource source, String command, String []commandArr) {
        System.out.println("Got command! " + command);
        CommandSender sender;
        try {
            sender = new CommandSender(source);
            if (source instanceof org.spongepowered.api.entity.player.Player) {
                org.spongepowered.api.entity.player.Player player = (org.spongepowered.api.entity.player.Player)source;
                sender = new Player(player);
            }
        } catch (Exception e) {
            logger.logError(e);
            return false;
        }
        Iterator it = pluginMap.entrySet().iterator();
        if (command.equalsIgnoreCase("reload")) {
            logger.info("Reloading server!");
            loadPlugins();
            initPlugins();
        } else {
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                JavaPlugin plugin = (JavaPlugin) pair.getValue();
                try {
                    boolean result = plugin.onCommand(sender, new Command(command), command, commandArr);
                    if (result) {
                        return true;
                    }
                } catch (Exception e) {
                    logger.logError(e);
                }
            }
        }
        return false;
    }

    //////////////////////////////////////////////////////
    // Plugin loading methods
    //////////////////////////////////////////////////////

    private void loadPlugins() {
        pluginList.clear();
        pluginMap.clear();
        readyPluginMap.clear();
        classes.clear();
        loaders.clear();
        getLogger().info("Loading plugins...");
        String cwd = System.getProperty("user.dir");
        getLogger().info("Starting in " + cwd);
        File pluginDir = new File(cwd + "/" + SpongeBridge.pluginDir);
        System.out.println("Plugin directory should be " + pluginDir.getAbsolutePath());
        if (!pluginDir.exists()) {
            getLogger().warning("Error, expected plugin directory " + pluginDir.getAbsolutePath() + " does not exist!");
            return;
        }

        File[] files = pluginDir.listFiles();

        Map<PluginDescription,File> plugins = new HashMap<PluginDescription, File>();
        List<File> extraJars = new ArrayList<File>();

        for (File entry : files) {
            if (!entry.getName().endsWith(".jar")) {
                continue;
            }
            getLogger().info("Plugin jar: " + entry.getName());

            try {
                PluginDescription description;
                try {
                    description = PluginDescription.getDescriptionForPlugin(entry);
                    plugins.put(description,entry);
                } catch (Exception e) {
                    getLogger().warning("Adding " + entry.getName() + " as extra jar");
                    extraJars.add(entry);
                }
            } catch (Exception e) {
                getLogger().warning("Can't load jar " + entry.getAbsolutePath() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        // extraJars are jars for which there was no plugin.yml. Probably utility jars.
        for (File entry : extraJars) {
            try {
                getLogger().info("Loading extra jar " + entry.getName());
                PluginClassLoader loader = new PluginClassLoader(this, entry, getClass().getClassLoader());
                loaders.put(entry.getName(), loader);
            } catch (Exception e) {
                getLogger().warning("Can't load extra jar " + entry.getAbsolutePath() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        Map<PluginDescription,File> newPlugins = new HashMap<PluginDescription, File>();
        while (true) {
            newPlugins = new HashMap<PluginDescription, File>();
            Iterator it = plugins.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                PluginDescription description = (PluginDescription)pairs.getKey();
                File entry = (File)pairs.getValue();
                getLogger().info("Attempting to load " + description.name);

                // make sure all dependencies were resolved
                boolean resolved = true;
                for (String dep : description.depends) {
                    getLogger().info("Has dependency on " + dep);
                    if (!pluginMap.containsKey(dep)) {
                        getLogger().info("Which is not resolved");
                        resolved = false;
                    }
                }
                if (!resolved) {
                    newPlugins.put(description,entry);
                } else {
                    try {
                        getLogger().info("Beginning code load");
                        PluginClassLoader loader = new PluginClassLoader(this, entry, getClass().getClassLoader(), description);
                        pluginMap.put(description.name,loader.plugin);
                        pluginList.add(loader.plugin);
                        loaders.put(description.name, loader);
                    } catch (Exception e) {
                        logger.logError(e);
                    }
                }
            }
            if (newPlugins.size() == plugins.size()) break;
            plugins = newPlugins;
            getLogger().info("We have " + plugins.keySet().size() + " plugins left");
        }
        getLogger().info(plugins.keySet().size() + " plugins failed to load");
    }

    private void initPlugins() {
        getLogger().info("Server loaded, beginning to initialize plugins.");

        for (int i=0;i<pluginList.size();i++) {
            JavaPlugin plugin = (JavaPlugin)pluginList.get(i);
            plugin.server = this;
            for (String dep : plugin.description.softDepends) {
                logger.info(plugin.description.name + " Depends on " + dep);
                if (!readyPluginMap.containsKey(dep)) {
                    logger.info("Dep not loaded!");
                    boolean found = false;
                    int foundIndex = -1;
                    for (int j=i;j<pluginList.size();j++) {
                        JavaPlugin p2 = (JavaPlugin)pluginList.get(i);
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
                        JavaPlugin p2 = (JavaPlugin)pluginList.get(foundIndex);
                        if (!p2.description.softDepends.contains(plugin.description.name)) {
                            logger.info("Swapping spaces!");
                            pluginList.set(i, p2);
                            pluginList.set(foundIndex,plugin);
                            plugin = p2;
                            i --;
                            logger.info("Now loading " + plugin.description.name);
                        }
                    }
                }
            }
            try {
                logger.info("Plugin " + plugin.description.name + " has " + plugin.description.commands.size() + " commands");
                for (String command : plugin.description.commands) {
                    logger.info("Registering " + command);
                    if (commandHandlers.containsKey(command)) continue;
                    CommandHandler handler = new CommandHandler(command,this);
                    commandHandlers.put(command,handler);
                }
                logger.info("Enabling " + plugin.description.name);
                plugin.onEnable();
                readyPluginMap.put(plugin.description.name,plugin);
            } catch (Exception e) {
                logger.logError(e);
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

    /////////////////////////////////
    // Misc
    /////////////////////////////////

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
