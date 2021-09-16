package eu.mcone.coresystem.api.core.chat;

import eu.mcone.coresystem.api.core.GlobalCorePlugin;
import eu.mcone.coresystem.api.core.util.GlobalPluginManager;

public abstract class CoreMsgFacade {

    private static GlobalPluginManager pluginManager;

    public static void setPluginManager(GlobalPluginManager pluginManager) {
        if (CoreMsgFacade.pluginManager == null) {
            CoreMsgFacade.pluginManager = pluginManager;
        } else throw new IllegalStateException("Could not set PluginManager instance. Instance already set!");
    }

    protected static GlobalCorePlugin getCorePlugin() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        return pluginManager.getCorePluginByPackage(stackTrace[3].getClassName());
    }

}
