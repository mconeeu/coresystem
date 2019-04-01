/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.exception;

public class RuntimeCoreException extends RuntimeException {

    public RuntimeCoreException() {
        super();
    }

    public RuntimeCoreException(String message) {
        super(message);
    }

    public RuntimeCoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public RuntimeCoreException(Throwable cause) {
        super(cause);
    }

}
