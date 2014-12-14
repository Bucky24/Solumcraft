package RainbowSetup;

import PluginReference.MC_EventInfo;
import PluginReference.MC_Player;
import PluginReference.MC_Server;
import PluginReference.PluginBase;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 12/13/14
 * Time: 6:48 PM
 * To change this template use File | Settings | File Templates.
 */

public class MyPlugin extends PluginBase {
    private static String pluginDir = "bukkit_plugins";

    ////////////////////////////
    // Rainbow functions
    ////////////////////////////
    @Override
    public void onStartup(MC_Server argServer){
        System.out.println("RainbowSetup active!");

        this.loadPlugins();
    }

    @Override
    public void onPlayerInput(MC_Player plr, String msg, MC_EventInfo ei) {
        if (msg.startsWith("/")) {
            System.out.println("Got command! " + msg);
            msg = msg.substring(1);
            String[] commandArr = msg.split(" ");
            String command = commandArr[0];
            commandArr = Arrays.copyOfRange(commandArr, 1, commandArr.length);
            //onCommand((CommandSender)plr,new Command(command),command,commandArr);
        }
    }

    private void loadPlugins() {
        System.out.println("Loading plugins...");
        String cwd = System.getProperty("user.dir");
        System.out.println("Starting in " + cwd);
        File pluginDir = new File(cwd + "/" + MyPlugin.pluginDir);
        System.out.println("Plugin directory should be " + pluginDir.getAbsolutePath());
        if (!pluginDir.exists()) {
            System.out.println("Error, expected plugin directory " + pluginDir.getAbsolutePath() + " does not exist!");
            return;
        }

        for (File entry : pluginDir.listFiles()) {
            System.out.println("Plugin jar: " + entry.getName());

            try {
                URL myJarFile = new URL("jar:file:"+entry.getAbsolutePath()+"!/");

                JarURLConnection connection = (JarURLConnection)myJarFile.openConnection();
                JarFile jarFile = connection.getJarFile();
                Enumeration<JarEntry> jarEnum = jarFile.entries();
                while (jarEnum.hasMoreElements()) {
                    JarEntry jarEntry = jarEnum.nextElement();
                    System.out.println(jarEntry.getName());
                }

                URLClassLoader sysLoader = (URLClassLoader)ClassLoader.getSystemClassLoader();
                Class sysClass = URLClassLoader.class;
                Method sysMethod = sysClass.getDeclaredMethod("addURL",new Class[] {URL.class});
                sysMethod.setAccessible(true);
                sysMethod.invoke(sysLoader, new Object[]{myJarFile});
                URLClassLoader cl = URLClassLoader.newInstance(new URL[]{myJarFile});

                InputStream input = cl.getResourceAsStream("plugin.yml");
                byte[] data = new byte[(int) input.available()];
                input.read(data);
                input.close();
                String mainClass = "";

                String str = new String(data, "UTF-8");
                String[] strArr = str.split("\n");
                for (String configLine : strArr) {
                    String[] configArr = configLine.split(":");
                    if (configArr.length == 2 && configArr[0].equals("main")) {
                        mainClass = configArr[1].trim();
                    }
                }
                if (mainClass.equals("")) {
                    throw new Exception("Cannot find main class in file.");
                } else {
                    System.out.println("Got main class of " + mainClass);
                }
                Class MyClass = cl.loadClass(mainClass);
                Object MyClassObj = MyClass.newInstance();
            } catch (Exception e) {
                System.out.println("Can't load jar " + entry.getAbsolutePath() + "!");
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
