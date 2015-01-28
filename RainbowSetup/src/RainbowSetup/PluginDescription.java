package RainbowSetup;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by solum on 1/14/2015.
 */
public class PluginDescription {
    public String name;
    public String mainClass;
    public List<String> softDepends;

    public PluginDescription() {
        name = "";
        mainClass = "";
        softDepends = new ArrayList<String>();
    }

    public static PluginDescription getDescriptionForPlugin(File entry) throws Exception {
        PluginDescription desc = new PluginDescription();

        JarFile jar = new JarFile(entry);
        JarEntry fileEntry = jar.getJarEntry("plugin.yml");
        InputStream input = jar.getInputStream(fileEntry);
        if (input == null) {
            throw new Exception("File plugin.yml is not available");
        }
        byte[] data = new byte[(int) input.available()];
        input.read(data);
        input.close();
        String str = new String(data, "UTF-8");
        String[] strArr = str.split("\n");
        for (String configLine : strArr) {
            String[] configArr = configLine.split(":");
            if (configArr.length == 2 && configArr[0].equals("main")) {
                desc.mainClass = configArr[1].trim();
            } else if (configArr.length == 2 && configArr[0].equals("name")) {
                desc.name = configArr[1].trim();
            } else if (configArr.length > 1 && configArr[0].equals("softdepend")) {
                String plugins = configArr[1].trim();
                plugins = plugins.replace("[","");
                plugins = plugins.replace("]","");
                String[] pluginArr = plugins.split(",");
                for (String plugin : pluginArr) {
                    desc.softDepends.add(plugin);
                }
            }
        }

        return desc;
    }
}
