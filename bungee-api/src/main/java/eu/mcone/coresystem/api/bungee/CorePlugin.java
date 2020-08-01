/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bungee;

import eu.mcone.coresystem.api.bungee.util.Messenger;
import eu.mcone.coresystem.api.core.GlobalCorePlugin;
import io.sentry.SentryClient;
import io.sentry.SentryClientFactory;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

public abstract class CorePlugin extends Plugin implements GlobalCorePlugin {

    @Getter
    private final String pluginName;
    private final String consolePrefix;
    @Getter
    private final Messenger messenger;
    @Getter
    protected final SentryClient sentryClient;

    protected CorePlugin(String pluginName, ChatColor pluginColor, String prefixTranslation) {
        this(pluginName, pluginColor, prefixTranslation, null);
    }

    protected CorePlugin(String pluginName, ChatColor pluginColor, String prefixTranslation, String sentryDsn) {
        this.pluginName = pluginName;
        this.consolePrefix = "§8[" + pluginColor + pluginName + "§8] §7";
        this.messenger = new Messenger(prefixTranslation);

        if (sentryDsn != null && Boolean.parseBoolean(System.getProperty("EnableSentry"))) {
            sendConsoleMessage("§aInitialzing Sentry...");
            this.sentryClient = SentryClientFactory.sentryClient(sentryDsn);
        } else {
            this.sentryClient = null;
        }
    }

    public void sendConsoleMessage(String message) {
        ProxyServer.getInstance().getConsole().sendMessage(new TextComponent(TextComponent.fromLegacyText(consolePrefix + message)));
    }

    public void registerEvents(Listener... listeners) {
        for (Listener listener : listeners) {
            getProxy().getPluginManager().registerListener(this, listener);
        }
    }

    public void registerCommands(Command... commands) {
        for (Command command : commands) {
            getProxy().getPluginManager().registerCommand(this, command);
        }
    }

    public boolean hasSentryClient() {
        return sentryClient != null;
    }

    public void withErrorLogging(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            if (sentryClient != null) {
                sentryClient.sendException(e);
            }
            throw e;
        }
    }

}
