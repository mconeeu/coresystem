/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.core.exception;

public class LabyModAPIException extends RuntimeException {

    public LabyModAPIException() {
        super();
    }

    public LabyModAPIException(String message) {
        super(message);
    }

    public LabyModAPIException(String message, Throwable cause) {
        super(message, cause);
    }

    public LabyModAPIException(Throwable cause) {
        super(cause);
    }

}
