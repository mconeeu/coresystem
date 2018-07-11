/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.core.translation;

import lombok.Getter;

public enum Language {

    ENGLISH("EN", "§c§lEnglish", "http://textures.minecraft.net/texture/6840e67a86cc3140afca057ed27013b19b5a20e528b147005036e29ce53e53f4"),
    GERMAN("DE", "§e§lDeutsch", "http://textures.minecraft.net/texture/b948d8dedc30a8db264f17ac3950eb67fd30b249488691cb60f4092402905055"),
    FRENCH("FR", "§9§lFrançais", "http://textures.minecraft.net/texture/70cc11c49397fc16f0469095f89675e439869c8414bb695971872ba16544ff61");

    @Getter
    private String id, name, textureUrl;

    Language(String id, String name, String textureUrl) {
        this.id = id;
        this.name = name;
        this.textureUrl = textureUrl;
    }

}
