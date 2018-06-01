/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit;

import eu.mcone.coresystem.api.bukkit.util.Messager;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class CorePlugin extends JavaPlugin {

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
        getServer().getConsoleSender().sendMessage(consolePrefix+message);
    }

}
