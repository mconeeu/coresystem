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
        recordBreadcrumb(null, "Join", "logged in", new HashMap<String, String>(){{
            put("cancelled", String.valueOf(e.isCancelled()));
            put("cancelReason", e.getCancelReasonComponents() != null ? BaseComponent.toLegacyText(e.getCancelReasonComponents()) : null);
        }});
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerDisconnectEvent e) {
        recordBreadcrumb(e.getPlayer(), "Quit", "disconnected");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommandPreprocess(ChatEvent e) {
        if (e.isCommand()) {
            recordBreadcrumb((ProxiedPlayer) e.getSender(), "Command", "performed Command "+e.getMessage(), new HashMap<String, String>(){{
                put("command", e.getMessage());
                put("cancelled", String.valueOf(e.isCancelled()));
                put("proxyCommand", String.valueOf(e.isProxyCommand()));
            }});
        }
    }

    private static void recordBreadcrumb(ProxiedPlayer p, String category, String message) {
        recordBreadcrumb(p, category, message, new HashMap<>());
    }

    private static void recordBreadcrumb(ProxiedPlayer p, String category, String message, Map<String, String> data) {
        for (Plugin plugin : ProxyServer.getInstance().getPluginManager().getPlugins()) {
            if (plugin instanceof CorePlugin) {
                CorePlugin corePlugin = (CorePlugin) plugin;

                if (corePlugin.hasSentryClient()) {
                    if (p != null) {
                        data.put("name", p.getName());
                        data.put("uuid", p.getUniqueId().toString());
                    }

                    corePlugin.getSentryClient().getContext().recordBreadcrumb(
                            new BreadcrumbBuilder().setCategory(category).setMessage("Player "+message).setData(data).build()
                    );
                }
            }
        }
    }

}
