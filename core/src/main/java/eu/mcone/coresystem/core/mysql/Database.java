/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.core.mysql;

import lombok.Getter;

public enum Database {

    SYSTEM("78.46.249.195", 3306, "mc1system", "core-system", "An.§U!s>k+qxQL=sd.z.4r?Yr}:83QB4y6N$.)kcvxnFC(>-CA§8§<E+yL&(vN,n", 2),
    STATS("78.46.249.195", 3306, "mc1stats", "core-system", "An.§U!s>k+qxQL=sd.z.4r?Yr}:83QB4y6N$.)kcvxnFC(>-CA§8§<E+yL&(vN,n", 2),
    DATA("78.46.249.195", 3306, "mc1data", "core-system", "An.§U!s>k+qxQL=sd.z.4r?Yr}:83QB4y6N$.)kcvxnFC(>-CA§8§<E+yL&(vN,n", 2);

    @Getter
    private String hostname, database, username, password;
    @Getter
    private int port, poolsize;

    Database(String hostname, int port, String database, String username, String password, int poolsize) {
        this.hostname = hostname;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.poolsize = poolsize;
    }

}
