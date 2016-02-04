package SpongeBridge;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Text;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.world.ExplosionEvent;
import org.spongepowered.api.plugin.Plugin;

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

    @Listener
    public void onServerInit(GameInitializationEvent event) {
        this.game = Sponge.getGame();
        logger = new Logger();

        Material.init(logger);

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
    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        org.bukkit.event.player.PlayerJoinEvent newEvent = new org.bukkit.event.player.PlayerJoinEvent(this,event);
        fireEvent(newEvent);
    }

    @Listener
    public void onExplode(ExplosionEvent.Detonate event) {
        org.bukkit.event.entity.EntityExplodeEvent newEvent = new org.bukkit.event.entity.EntityExplodeEvent(this,event);
        fireEvent(newEvent);
    }

    @Listener
    public void onBlockBreak(ChangeBlockEvent.Break event) {
        org.bukkit.event.block.BlockBreakEvent newEvent = new org.bukkit.event.block.BlockBreakEvent(this,event);
        fireEvent(newEvent);
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

    public void registerEvents(JavaPlugin plugin, org.bukkit.event.Listener listener) {}

    public List<Player> getOnlinePlayers() {
        List<Player> players = new ArrayList<Player>();

        Collection<org.spongepowered.api.entity.living.player.Player> realPlayers = game.getServer().getOnlinePlayers();
        for (Iterator<org.spongepowered.api.entity.living.player.Player> i = realPlayers.iterator(); i.hasNext();) {
            org.spongepowered.api.entity.living.player.Player player = i.next();
            try {
                players.add(new Player(player));
            } catch (Exception e) {
                logger.logError("Can't add player object",e.getStackTrace());
            }
        }

        return players;
    }

    public Player getPlayer(String name) {
        try {
            org.spongepowered.api.entity.living.player.Player player = game.getServer().getPlayer(name).orElse(null);
            if (player == null) {
                // try to get via UUID
                UUID id;
                try {
                    id = UUID.fromString(name);
                } catch (Exception e) {
                    return null;
                }
                player = game.getServer().getPlayer(id).orElse(null);
                if (player == null) {
                    return null;
                }
            }
            return new Player(player);
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
        for (org.spongepowered.api.entity.living.player.Player p : game.getServer().getOnlinePlayers()) {
            p.sendMessage(SpongeText.getText(text));
        }
    }

    public World getWorld(String worldName) {
        org.spongepowered.api.world.World world = this.game.getServer().getWorld(worldName).orElse(null);
        if (world == null) {
            logger.warning("World " + worldName + " does not exist");
            return null;
        }
        return new World(world);
    }

    ///////////////////////////////////////////////////////
    // Bridge server methods
    ///////////////////////////////////////////////////////

    public void fireEvent(Event newEvent) {
        Iterator it = pluginMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            JavaPlugin plugin = (JavaPlugin)pair.getValue();
            try {
                List<Method> methods = this.getMethodsThatTakeEvent(plugin, newEvent.getClass());
                for (Method m : methods) {
                    m.invoke(plugin,newEvent);
                }
            } catch (Exception e) {
                logger.logError(e);
            }
        }
    }

    public boolean handleCommand(org.spongepowered.api.command.CommandSource source, String command, String []commandArr) {
        System.out.println("Got command! " + command);
        CommandSender sender;
        try {
            sender = new CommandSender(source);
            if (source instanceof org.spongepowered.api.entity.living.player.Player) {
                org.spongepowered.api.entity.living.player.Player player = (org.spongepowered.api.entity.living.player.Player)source;
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
            int pluginIndex = i;
            boolean skipLoading = false;
            JavaPlugin plugin = (JavaPlugin)pluginList.get(i);
            plugin.server = this;
            List<String> deps = new ArrayList<String>();
            deps.addAll(plugin.description.softDepends);
            deps.addAll(plugin.description.depends);
            for (String dep : deps) {
                if (!readyPluginMap.containsKey(dep)) {
                    logger.info(plugin.description.name + " Depends on " + dep + ", which is not loaded");
                    boolean found = false;
                    int foundIndex = -1;
                    for (int j=pluginIndex;j<pluginList.size();j++) {
                        JavaPlugin p2 = (JavaPlugin)pluginList.get(j);
                        logger.info(p2.description.name);
                        if (p2.description.name.equals(dep)) {
                            found = true;
                            foundIndex = j;
                            break;
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
                            pluginList.set(pluginIndex, p2);
                            pluginList.set(foundIndex,plugin);
                            plugin = p2;
                            i --;
                            logger.info("Now loading " + plugin.description.name);
                            // don't handle more dependencies
                            skipLoading = true;
                            break;
                        } else {
                            logger.warning("Plugin " + plugin.description.name + " depends on " + dep + ", which also depends on " + plugin.description.name + ". Cannot continue loading this plugin.");
                            skipLoading = true;
                            break;
                        }
                    } else {
                        if (plugin.description.depends.contains(dep)) {
                            logger.warning("Plugin " + plugin.description.name + " depends on " + dep + ", which was not found. Cannot continue loading this plugin.");
                            skipLoading = true;
                            break;
                        }
                    }
                }
            }
            if (skipLoading) {
                continue;
            }
            logger.info("Plugin " + plugin.description.name + " has " + plugin.description.commands.size() + " commands");
            try {
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
