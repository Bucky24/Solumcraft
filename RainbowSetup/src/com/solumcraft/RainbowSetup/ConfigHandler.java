package com.solumcraft.RainbowSetup;

import java.io.IOException;
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
    public static void createDefaultConfig(String name, String defaultData) {

    }

    public ConfigHandler(String plugin) {
        config = new HashMap<String,String>();
        // NOTE: Temporary value only!
        String path = "/testbed/plugins";
        path += "/" + plugin;
        path += "/" + plugin + ".yml";

        try {
            List<String> lines = Files.readAllLines(Paths.get(path),
                    Charset.defaultCharset());
            for (String line : lines) {
                if (line.startsWith("#")) continue;
                String[] lineData = line.split(":");
                String key = lineData[0];
                String value = lineData[1].trim();

                config.put(key,value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getString(String key) {
        return config.get(key);
    }
}
