package eu.mcone.coresystem.bungee.listener;


import eu.mcone.coresystem.api.bungee.CorePlugin;
import io.sentry.event.BreadcrumbBuilder;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.HashMap;
import java.util.Map;

public class SentryListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(LoginEvent e) {
        recordBreadcrumb(null, "Join", "logged in", new String[][]{
                {"cancelled", String.valueOf(e.isCancelled())},
                {"cancelReason", e.getCancelReasonComponents() != null ? BaseComponent.toLegacyText(e.getCancelReasonComponents()) : null}
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerDisconnectEvent e) {
        recordBreadcrumb(e.getPlayer(), "Quit", "disconnected");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommandPreprocess(ChatEvent e) {
        if (e.isCommand()) {
            recordBreadcrumb((ProxiedPlayer) e.getSender(), "Command", "performed Command " + e.getMessage(), new String[][]{
                    {"command", e.getMessage()},
                    {"cancelled", String.valueOf(e.isCancelled())},
                    {"proxyCommand", String.valueOf(e.isProxyCommand())}
            });
        }
    }

    private static void recordBreadcrumb(ProxiedPlayer p, String category, String message) {
        recordBreadcrumb(p, category, message, new String[0][]);
    }

    private static void recordBreadcrumb(ProxiedPlayer p, String category, String message, String[][] data) {
        for (Plugin plugin : ProxyServer.getInstance().getPluginManager().getPlugins()) {
            if (plugin instanceof CorePlugin) {
                CorePlugin corePlugin = (CorePlugin) plugin;

                if (corePlugin.hasSentryClient()) {
                    Map<String, String> dataMap = new HashMap<>();
                    if (p != null) {
                        dataMap.put("name", p.getName());
                        dataMap.put("uuid", p.getUniqueId().toString());
                    }

                    if (data != null) {
                        for (String[] entry : data) {
                            dataMap.put(entry[0], entry[1]);
                        }
                    }

                    corePlugin.getSentryClient().getContext().recordBreadcrumb(
                            new BreadcrumbBuilder().setCategory(category).setMessage("Player " + message).setData(dataMap).build()
                    );
                }
            }
        }
    }

}
