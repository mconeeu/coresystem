/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.player;

public class Nick {

    private String name;
    private String value;
    private String signature;

    Nick(String name, String value, String signature) {
        this.name = name;
        this.value = value;
        this.signature = signature;
    }

    String getName() {
        return name;
    }

    String getSignature() {
        return signature;
    }

    String getValue() {
        return value;
    }

}
