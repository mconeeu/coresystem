/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public abstract class CorePlugin extends JavaPlugin {

    @Getter
    private String pluginName, consolePrefix, prefixTranslationName;
    @Getter
    private List<Function> functions;

    public enum Function {
        NPC_MANAGER, HOLOGRAM_MANANGER, LOCATION_MANAGER


    }

    protected CorePlugin(String pluginName, String consolePrefix, String prefixTranslationName) {
        this.pluginName = pluginName;
        this.consolePrefix = consolePrefix;
        this.prefixTranslationName = prefixTranslationName;

        CoreSystem.getInstance().registerPlugin(this);
    }

    protected void enableFunctions(Function... functions) {
        for (Function function : functions) {

        }
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

}
