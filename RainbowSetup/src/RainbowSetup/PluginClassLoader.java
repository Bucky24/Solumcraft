package RainbowSetup;

import sun.plugin2.main.server.Plugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by solum on 12/20/2014.
 */
public class PluginClassLoader extends URLClassLoader {
    public PluginClassLoader(URL[] urls, ClassLoader parent) {
        super(urls,parent);
    }

    public void addJar(URL jar) {
        addURL(jar);
    }
}
