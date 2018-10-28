/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.player.profile;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.player.profile.GameProfileType;

public class TestProfile extends GameProfile {

    protected TestProfile() {
        super("TestProfile", GameProfileType.GAME_PROFILE, CoreSystem.getInstance().getGameSectionName());
    }

    public void test() {
        System.out.println("Test message");
    }
}
