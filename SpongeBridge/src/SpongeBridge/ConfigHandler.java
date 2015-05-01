package SpongeBridge;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 12/13/14
 * Time: 6:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConfigHandler {
    private Map<String,String> config;
    public void createDefaultConfig(String plugin, Logger logger) {
        String path = "/testbed/bukkit_plugins";
        path += "/" + plugin;
        File dir = new File(path);

        path += "/config.yml";
        File f = new File(path);
        if (f.exists()) {
            return;
        }

        try {
            URL myJarFile = new URL("jar:file:/testbed/bukkit_plugins/" + plugin + ".jar!/");

            URLClassLoader sysLoader = (URLClassLoader)ClassLoader.getSystemClassLoader();
            Class sysClass = URLClassLoader.class;
            Method sysMethod = sysClass.getDeclaredMethod("addURL",new Class[] {URL.class});
            sysMethod.setAccessible(true);
            sysMethod.invoke(sysLoader, new Object[]{myJarFile});
            URLClassLoader cl = URLClassLoader.newInstance(new URL[]{myJarFile});

            InputStream input = cl.getResourceAsStream("config.yml");
            byte[] data = new byte[(int) input.available()];
            input.read(data);
            input.close();

            String str = new String(data, "UTF-8");
            if (!dir.exists()) {
                dir.mkdir();
            }

            try {
                PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(path, true)));
                out.println(str);
                out.close();
            } catch (IOException e) {
                System.out.println("Can't write to config file " + path + ": " + e.getMessage());
            }

        } catch (Exception e) {
            logger.warning("Unable to create default configuration for " + plugin + ": " + e.getMessage());
        }
    }

    public ConfigHandler(String plugin,Logger logger) {
        config = new HashMap<String,String>();
        // NOTE: Temporary value only!
        String path = "/testbed/bukkit_plugins";
        path += "/" + plugin;
        path += "/config.yml";

        try {
            List<String> lines = Files.readAllLines(Paths.get(path),
                    Charset.defaultCharset());
            for (String line : lines) {
                //logger.info(line);
                if (line.startsWith("#")) continue;
                String[] lineData = line.split(":");
                //logger.info(line);
                String key = lineData[0];
                String value = lineData[1].trim();

                config.put(key,value);
            }
        } catch (IOException e) {}
    }

    public String getString(String key) {
        return config.get(key);
    }
}
