package RainbowSetup;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by solum on 1/14/2015.
 */
public class PluginDescription {
    public String name;
    public String mainClass;

    public PluginDescription() {
        name = "";
        mainClass = "";
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
            }
        }

        return desc;
    }
}
