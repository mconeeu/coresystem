/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.exception;

public class CoreConfigException extends RuntimeException {

    public CoreConfigException() {
        super();
    }

    public CoreConfigException(String message) {
        super(message);
    }

    public CoreConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public CoreConfigException(Throwable cause) {
        super(cause);
    }

}
