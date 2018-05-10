/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.core.mysql;

import java.sql.ResultSet;

public interface MySQL {

    /**
     * UPDATE/INSERT/DELETE/... without getting data back
     * @param qry sql query
     * @param parameters ? statement parameters
     */
    void update(String qry, Object... parameters);

    /**
     * UPDATE/INSERT/DELETE/... with getting affected id
     * @param qry sql query
     * @param parameters ? statement parameters
     * @return affected id
     */
    int updateWithGetId(String qry, Object... parameters);

    /**
     * SELECT/... with getting data as ResultSet
     * @param qry sql query
     * @param task task to do after querying
     */
    void select(String qry, Callback<ResultSet> task);

    /**
     * SELECT/... with getting data as ResultSet and doing task async
     * @param qry sql query
     * @param task task to do after querying
     */
    void selectAsync(String qry, Callback<ResultSet> task);

    /**
     * SELECT/... with getting direct Result from task
     * @param qry sql query
     * @param task task to do after querying
     * @return Object returned from task
     */
    Object select(String qry, CallbackResult<ResultSet> task);

}
