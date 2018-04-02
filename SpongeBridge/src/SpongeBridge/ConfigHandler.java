package SpongeBridge;

import java.io.*;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
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

    public static String getPluginConfigDir(String plugin) {
        String cwd = System.getProperty("user.dir");
        String pluginPath = cwd + "/" + SpongeBridge.pluginDir;

        return pluginPath + "/" + plugin;
    }

    public void createDefaultConfig(String plugin, Logger logger) {
        String cwd = System.getProperty("user.dir");
        String pluginPath = cwd + "/" + SpongeBridge.pluginDir;
        String path = ConfigHandler.getPluginConfigDir(plugin);
        File dir = new File(path);

        path += "/config.yml";
        File f = new File(path);
        System.out.println("Does config " + path + " exist? " + f.exists());
        if (f.exists()) {
            return;
        }

        try {
            URL myJarFile = new URL("jar:file:" + pluginPath + "/" + plugin + ".jar!/config.yml");

            System.out.println("Attempting to create config: " + path);
            System.out.println("Attempting to find config from jar:file:" + pluginPath + "/" + plugin + ".jar!/config.yml");

            JarURLConnection conn = (JarURLConnection)myJarFile.openConnection();
            InputStream input = conn.getInputStream();
            if (input == null) {
                throw new Exception("Unable to get an input stream for config.yml");
            }
            System.out.println("Got configuration stream");
            byte[] data = new byte[(int) input.available()];
            input.read(data);
            System.out.println("Read in data");
            input.close();

            System.out.println("Read in default config file");

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

        } catch(IOException ioe) {
            logger.logError("IOException when reading config file: " + ioe.getMessage(), ioe.getStackTrace());
        } catch (Exception e) {
            logger.warning("Unable to create default configuration for " + plugin + ": (" + e.getClass().getName() + ") " + e.getMessage());
        }
    }

    public ConfigHandler(String plugin, Logger logger) {
        config = new HashMap<String,String>();
        // NOTE: Temporary value only!
        String path = ConfigHandler.getPluginConfigDir(plugin);
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
