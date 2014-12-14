package RainbowTest;

import PluginReference.MC_Server;
import PluginReference.PluginBase;
import PluginReference.PluginInfo;

public class MyPlugin extends PluginBase {

    public MC_Server server;

    @Override
    public void onStartup(MC_Server server) {
        this.server = server;
        System.out.println("YourRainbowPlugin activated!");
        // If you have configuration, load it here.
    }

    @Override
    public void onShutdown() {
        System.out.println("YourRainbowPlugin deactivated!");
    }

    @Override
    public PluginInfo getPluginInfo() {
        PluginInfo info = new PluginInfo();

        info.name = "YourRainbowPlugin";
        info.description = "Setting up eclipse is easy!";

        return info;
    }

}