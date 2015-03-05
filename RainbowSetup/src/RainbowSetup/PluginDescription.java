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
    public List<String> depends;
    public List<String> commands;

    public PluginDescription() {
        name = "";
        mainClass = "";
        softDepends = new ArrayList<String>();
        depends = new ArrayList<String>();
        commands = new ArrayList<String>();
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
        for (int i=0;i<strArr.length;i++) {
            String configLine = strArr[i];
            // skip any tabbed lines-those should be handled by their parent lines
            if (configLine.charAt(0) == '\t') {
                continue;
            }
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
            } else if (configArr.length > 1 && configArr[0].equals("depends")) {
                String plugins = configArr[1].trim();
                plugins = plugins.replace("[","");
                plugins = plugins.replace("]","");
                String[] pluginArr = plugins.split(",");
                for (String plugin : pluginArr) {
                    desc.depends.add(plugin);
                }
            } else if (configArr.length > 0 && configArr[0].equals("commands")) {
                // read until the next line that isn't indented
                // the i+1 because we don't want to run over the current line again
                for (int j=i+1;j<strArr.length;j++) {
                    String line = strArr[j];
                    line = line.trim();
                    line = line.substring(0);
                    String[] lineArr = line.split(":");
                    String command = lineArr[0];
                    // skip command definitions, we only care about the command
                    if (command.equalsIgnoreCase("description")) {
                        continue;
                    }
                    desc.commands.add(command);
                }
            }
        }

        return desc;
    }
}
