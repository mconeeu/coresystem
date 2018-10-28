/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.utils.bots.discord;

public enum DiscordToken {

    API("MCONE_VERIFIER", "NTAwMzY3NDAzMTYzODQ0NjI4.DqJzxw.dwsrub1YFyI5OeElWyU7cemvMao");

    private String name;
    private String apiToken;

    DiscordToken(final String name, final String apiToken) {
        this.name = name;
        this.apiToken = apiToken;
    }

    public String getName() {
        return name;
    }

    public String getApiToken() {
        return apiToken;
    }
}
