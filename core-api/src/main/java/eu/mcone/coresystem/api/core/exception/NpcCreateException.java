/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
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
