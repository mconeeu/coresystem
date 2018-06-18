/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bungee;

import eu.mcone.coresystem.api.bungee.util.Messager;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;

public abstract class CorePlugin extends Plugin {

    @Getter
    private String pluginName;
    private String consolePrefix;
    @Getter
    private Messager messager;

    protected CorePlugin(String pluginName, ChatColor pluginColor, String prefixTranslation) {
        this.pluginName = pluginName;
        this.consolePrefix = "§8[" + pluginColor + pluginName + "§8] §7";
        this.messager = new Messager(prefixTranslation);

        if (CoreSystem.getInstance() != null) CoreSystem.getInstance().registerPlugin(this);
    }

    public void sendConsoleMessage(String message) {
        ProxyServer.getInstance().getConsole().sendMessage(new TextComponent(TextComponent.fromLegacyText(consolePrefix + message)));
    }
}
