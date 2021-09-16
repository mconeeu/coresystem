package eu.mcone.coresystem.api.core.util;

import eu.mcone.coresystem.api.core.GlobalCorePlugin;

import java.util.Collection;

public interface GlobalPluginManager {

    GlobalCorePlugin getCorePluginByPackage(String path);

    void registerCorePlugin(GlobalCorePlugin corePlugin);

    Collection<GlobalCorePlugin> getCorePlugins();

}
