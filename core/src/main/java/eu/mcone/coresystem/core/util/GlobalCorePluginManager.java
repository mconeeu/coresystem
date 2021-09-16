package eu.mcone.coresystem.core.util;

import eu.mcone.coresystem.api.core.GlobalCorePlugin;
import eu.mcone.coresystem.api.core.chat.CoreMsgFacade;
import eu.mcone.coresystem.api.core.util.GlobalPluginManager;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

public class GlobalCorePluginManager implements GlobalPluginManager {

    @Getter
    private final Set<GlobalCorePlugin> corePlugins;

    public GlobalCorePluginManager() {
        this.corePlugins = new HashSet<>();
        CoreMsgFacade.setPluginManager(this);
    }

    @Override
    public void registerCorePlugin(GlobalCorePlugin corePlugin) {
        corePlugins.add(corePlugin);
    }

    @Override
    public GlobalCorePlugin getCorePluginByPackage(String path) {
        String[] packageNames = path.split("\\.");
        StringBuilder sb = new StringBuilder(packageNames[0]);

        GlobalCorePlugin result = null;
        for (int i = 1; i < packageNames.length; i++) {
            sb.append(".").append(packageNames[i]);

            int results = 0;
            for (GlobalCorePlugin plugin : corePlugins) {
                if (plugin.getClass().getName().startsWith(sb.toString())) {
                    result = plugin;
                    results++;
                }
            }

            if (results <= 1) {
                break;
            }
        }

        return result;
    }

}
