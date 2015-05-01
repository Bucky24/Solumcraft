package SpongeBridge;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.ServerStartedEvent;
import org.spongepowered.api.plugin.Plugin;

import java.io.File;
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

    @Subscribe
    public void onServerStart(ServerStartedEvent event) {
        logger = new Logger();
        getLogger().info("SpongeBridge server start! Loading bukkit plugins now.");

        pluginMap = new HashMap<String, JavaPlugin>();
        readyPluginMap = new HashMap<String, JavaPlugin>();
        pluginList = new ArrayList<JavaPlugin>();

        this.loadPlugins();
    }

    private Logger logger;

    public Logger getLogger() {
        return logger;
    }

    ///////////////////////////////////////////////////////
    // Server methods
    //////////////////////////////////////////////////////

    public SpongeBridge getPluginManager() {
        return this;
    }

    public JavaPlugin getPlugin(String plugin) {
        return readyPluginMap.get(plugin);
    }

    public void registerEvents(JavaPlugin plugin, Listener listener) {}

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
}
