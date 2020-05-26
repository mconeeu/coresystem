/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.exception;

public class NpcCreateException extends RuntimeCoreException {

    public NpcCreateException() {
        super();
    }

    public NpcCreateException(String message) {
        super(message);
    }

    public NpcCreateException(String message, Throwable cause) {
        super(message, cause);
    }

    public NpcCreateException(Throwable cause) {
        super(cause);
    }

}
