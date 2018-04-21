/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.core.mysql;

import java.sql.ResultSet;

public interface MySQL {

    void update(String qry, Object... parameters);

    int updateWithGetId(String qry, Object... parameters);

    void select(String qry, Callback<ResultSet> task);

    void selectAsync(String qry, Callback<ResultSet> task);

    Object select(String qry, CallbackResult<ResultSet> task);

}
