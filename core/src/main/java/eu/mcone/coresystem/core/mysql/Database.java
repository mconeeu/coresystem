/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.core.mysql;

import lombok.Getter;

public enum Database {

    MONGO_SYSTEM("db.mcone.eu", 27017, "admin", "admin", "T6KIq8gjmmF1k7futx0cJiJinQXgfguYXruds1dFx1LF5IsVPQjuDTnlI1zltpD9", 0),
    SYSTEM("db.mcone.eu", 3306, "mc1system", "core-system", "RugQsbRUDABCG6zHrjLva4L7cLryL8tEScDDW3g2GGVg3M9zA9fEVkg2yU9r9KHG", 5),
    STATS("db.mcone.eu", 3306, "mc1stats", "core-system", "RugQsbRUDABCG6zHrjLva4L7cLryL8tEScDDW3g2GGVg3M9zA9fEVkg2yU9r9KHG", 5),
    DATA("db.mcone.eu", 3306, "mc1data", "core-system", "RugQsbRUDABCG6zHrjLva4L7cLryL8tEScDDW3g2GGVg3M9zA9fEVkg2yU9r9KHG", 5);

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
