/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.core;

import eu.mcone.coresystem.api.core.mysql.Database;
import eu.mcone.coresystem.core.mysql.MySQL;

public interface CoreModuleCoreSystem {

    MySQL getMySQL(Database database);

}
