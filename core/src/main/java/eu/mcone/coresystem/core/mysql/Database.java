/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.core.mysql;

import lombok.Getter;

public enum Database {

    SYSTEM("mysql.mcone.eu", 3306, "mc1system", "core-system", "RugQsbRUDABCG6zHrjLva4L7cLryL8tEScDDW3g2GGVg3M9zA9fEVkg2yU9r9KHG", 2),
    STATS("mysql.mcone.eu", 3306, "mc1stats", "core-system", "RugQsbRUDABCG6zHrjLva4L7cLryL8tEScDDW3g2GGVg3M9zA9fEVkg2yU9r9KHG", 2),
    DATA("mysql.mcone.eu", 3306, "mc1data", "core-system", "RugQsbRUDABCG6zHrjLva4L7cLryL8tEScDDW3g2GGVg3M9zA9fEVkg2yU9r9KHG", 2),
    CLOUD("mysql.mcone.eu", 3306, "mc1cloud", "cloud-system", "5CjLP5dHYXQPX85zPizx5hayz0AYNOuNmzcegO0Id0AXnp3w1OJ3fkEQxbGJZAuJ", 2);

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
