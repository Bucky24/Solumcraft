package RainbowSetup;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * Created by solum on 12/20/2014.
 */
public class PluginClassLoader extends URLClassLoader {
    private MyPlugin loader;
    public JavaPlugin plugin;
    public File file;
    private final Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
    private String name;

    public PluginClassLoader(MyPlugin loader, File file, ClassLoader parent, PluginDescription description) throws Exception {
        super(new URL[] {file.toURI().toURL()},parent);
        this.loader = loader;
        this.name = description.name;
        this.file = file;

        Class<?> jarClass;
        try {
            jarClass = Class.forName(description.mainClass, true, this);
        } catch (ClassNotFoundException ex) {
            throw new Exception("Cannot find main class `" + description.mainClass + "'", ex);
        } catch (NoClassDefFoundError ex) {
            throw new Exception("Cannot load main class `" + description.mainClass + "'", ex);
        }

        Class<? extends JavaPlugin> pluginClass;
        try {
            pluginClass = jarClass.asSubclass(JavaPlugin.class);
        } catch (ClassCastException ex) {
            throw new Exception("main class `" + description.mainClass + "' does not extend JavaPlugin", ex);
        }

        loader.logger.info("Loaded plugin " + description.name);

        plugin = pluginClass.newInstance();
        plugin.myClass = jarClass;
        plugin.description = description;
    }

    public PluginClassLoader(MyPlugin loader, File file, ClassLoader parent) throws Exception {
        super(new URL[] {file.toURI().toURL()},parent);
        this.file = file;
        this.loader = loader;
        this.name = file.getName();
        plugin = null;
    }

    public void addJar(URL jar) {
        addURL(jar);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return findClass(name, true);
    }

    Class<?> findClass(String className, boolean checkGlobal) throws ClassNotFoundException {
        //System.out.println("PluginClassLoader(" + this.name +").findClass: looking for " + className);
        Class<?> result = classes.get(className);

        if (result == null) {
            if (checkGlobal) {
                //System.out.println("PluginClassLoader(" + this.name + ").findClass: checking global for " + className);
                result = loader.getClassByName(className, this.name);
            }

            if (result == null) {
                //System.out.println("PluginClassLoader(" + this.name + ").findClass: global can't find " + className);
                try {
                    result = super.findClass(className);
                } catch (ClassNotFoundException e) {
                    //System.out.println("PluginClassLoader(" + this.name + ").findClass: parent can't find " + className + ", got exception!");
                    throw e;
                }

                if (result != null) {
                    //System.out.println("PluginClassLoader(" + this.name + ").findClass: parent found " + className);
                    loader.setClass(className, result);
                } else {
                    //System.out.println("PluginClassLoader(" + this.name + ").findClass: parent can't find " + className);
                }
            }
        }
        //System.out.println("PluginClassLoader(" + this.name + ").findClass: done searching for " + className);
        if (result == null) {
            //System.out.println("PluginClassLoader(" + this.name +").findClass: can't find " + className);
        } else {
            classes.put(className, result);
            //System.out.println("PluginClassLoader(" + this.name +").findClass: found " + className);
        }
        return result;
    }

    /*private static URL getResourceFromEntry(File entry,String classPath) {
        classPath = classPath.replace(".","/");
        try {
            URL myJarFile = new URL("jar:file:" + entry.getAbsolutePath() + "!/");
            JarURLConnection connection = (JarURLConnection) myJarFile.openConnection();
            JarFile jarFile = connection.getJarFile();
            Enumeration<JarEntry> jarEnum = jarFile.entries();
            while (jarEnum.hasMoreElements()) {
                try {
                    JarEntry jarEntry = jarEnum.nextElement();
                    if (jarEntry.getName().startsWith(classPath)) {
                        return new URL("jar:file:" + entry.getAbsolutePath() + "!" + classPath);
                    }
                } catch (Exception e) {
                    System.out.println("Could not print entry");
                }
            }
        } catch (Exception e) {
            System.out.println("Can't get resource from jar, got exception.");
        }
        return null;
    }*/

    @Override
    public URL getResource(final String name) {
        URL result = super.findResource(name);
        if (result == null) {
            try {
                //System.out.println("Super can't find resource " + name);
                URL jarUrl = new URL("jar:file:" + file.getAbsolutePath() + "!/");

            } catch (Exception e) {
                System.out.println("Can't get resource from jar, got exception.");
            }
        }

        return result;
    }
}
