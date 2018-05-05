/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.core.translation;

import lombok.Getter;

public enum Language {

    ENGLISH("EN"),
    GERMAN("DE"),
    FRENCH("FR"),
    ITALIAN("IT");

    @Getter
    private String id;

    Language(String id) {
        this.id = id;
    }

}
