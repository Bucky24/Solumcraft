package SpongeBridge;

import com.google.inject.Inject;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.ServerStartedEvent;
import org.spongepowered.api.plugin.Plugin;
import org.slf4j.Logger;

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
        getLogger().info("SpongeBridge server start! Loading bukkit plugins now.");

        this.loadPlugins();
    }

    @Inject
    private Logger logger;

    public Logger getLogger() {
        return logger;
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
        System.out.println("Loading plugins...");
        String cwd = System.getProperty("user.dir");
        System.out.println("Starting in " + cwd);
        File pluginDir = new File(cwd + "/" + SpongeBridge.pluginDir);
        System.out.println("Plugin directory should be " + pluginDir.getAbsolutePath());
        if (!pluginDir.exists()) {
            System.out.println("Error, expected plugin directory " + pluginDir.getAbsolutePath() + " does not exist!");
            return;
        }

        File[] files = pluginDir.listFiles();

        Map<PluginDescription,File> plugins = new HashMap<PluginDescription, File>();
        List<File> extraJars = new ArrayList<File>();

        for (File entry : files) {
            if (!entry.getName().endsWith(".jar")) {
                continue;
            }
            System.out.println("Plugin jar: " + entry.getName());

            try {
                PluginDescription description;
                try {
                    description = PluginDescription.getDescriptionForPlugin(entry);
                    plugins.put(description,entry);
                } catch (Exception e) {
                    logger.warn("Adding " + entry.getName() + " as extra jar");
                    extraJars.add(entry);
                }
            } catch (Exception e) {
                System.out.println("Can't load jar " + entry.getAbsolutePath() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        // extraJars are jars for which there was no plugin.yml. Probably utility jars.
        for (File entry : extraJars) {
            try {
                logger.info("Loading extra jar " + entry.getName());
                PluginClassLoader loader = new PluginClassLoader(this, entry, getClass().getClassLoader());
                loaders.put(entry.getName(), loader);
            } catch (Exception e) {
                System.out.println("Can't load extra jar " + entry.getAbsolutePath() + ": " + e.getMessage());
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
                logger.info("Attempting to load " + description.name);

                // make sure all dependencies were resolved
                boolean resolved = true;
                for (String dep : description.depends) {
                    logger.info("Has dependency on " + dep);
                    if (!pluginMap.containsKey(dep)) {
                        logger.info("Which is not resolved");
                        resolved = false;
                    }
                }
                if (!resolved) {
                    newPlugins.put(description,entry);
                } else {
                    try {
                        logger.info("Beginning code load");
                        PluginClassLoader loader = new PluginClassLoader(this, entry, getClass().getClassLoader(), description);
                        pluginMap.put(description.name,loader.plugin);
                        pluginList.add(loader.plugin);
                        loaders.put(description.name, loader);
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                }
            }
            if (newPlugins.size() == plugins.size()) break;
            plugins = newPlugins;
            logger.info("We have " + plugins.keySet().size() + " plugins left");
        }
        logger.info(plugins.keySet().size() + " plugins failed to load");
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
