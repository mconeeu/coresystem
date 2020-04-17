/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.translation;

import lombok.Getter;

public enum Language {

    GERMAN("DE", "Deutsch", "§e", "http://textures.minecraft.net/texture/b948d8dedc30a8db264f17ac3950eb67fd30b249488691cb60f4092402905055"),
    BAVARIA("BY", "Boarisch", "§b", "http://textures.minecraft.net/texture/bf79448a48f8fbac041a1b7d43c95de4e8b27aff7ed58f31309afd299e17f4b"),
    FRENCH("FR", "Français", "§9", "http://textures.minecraft.net/texture/70cc11c49397fc16f0469095f89675e439869c8414bb695971872ba16544ff61"),
    ENGLISH("EN", "English", "§c", "http://textures.minecraft.net/texture/6840e67a86cc3140afca057ed27013b19b5a20e528b147005036e29ce53e53f4"),
    ITALIAN("IT", "Italiano", "§2", "http://textures.minecraft.net/texture/85ce89223fa42fe06ad65d8d44ca412ae899c831309d68924dfe0d142fdbeea4"),
    SPAN("ES", "Espanol", "§c", "http://textures.minecraft.net/texture/4d3923b2d050cd42cd9fbb89ee683a2a9899345e3518bd6c7f3cbb53fd151d72"),
    POLISH("PL", "Polski", "§f", "http://textures.minecraft.net/texture/921b2af8d2322282fce4a1aa4f257a52b68e27eb334f4a181fd976bae6d8eb"),
    PORTUGUESE("PT", "Português", "§2", "http://textures.minecraft.net/texture/ebd51f4693af174e6fe1979233d23a40bb987398e3891665fafd2ba567b5a53a"),
    SWEDISH("SW", "Svenska", "§1", "http://textures.minecraft.net/texture/7d86242b0d97ece9994660f3974d72df7b887f630a4530dadc5b1ab7c2134aec"),
    RUSSIAN("RU", "Pусский", "§1", "http://textures.minecraft.net/texture/16eafef980d6117dabe8982ac4b4509887e2c4621f6a8fe5c9b735a83d775ad"),
    TURKISH("TU", "Türk", "§c", "http://textures.minecraft.net/texture/6bbeaf52e1c4bfcd8a1f4c6913234b840241aa48829c15abc6ff8fdf92cd89e");

    @Getter
    private final String id, name, color, textureUrl;

    Language(String id, String name, String color, String textureUrl) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.textureUrl = textureUrl;
    }

    public String getLabel() {
        return color+"§l"+name;
    }

}
