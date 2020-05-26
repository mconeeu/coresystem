/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.exception;

public class PlayerNotResolvedException extends CoreException {

    public PlayerNotResolvedException() {
        super();
    }

    public PlayerNotResolvedException(String message) {
        super(message);
    }

    public PlayerNotResolvedException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlayerNotResolvedException(Throwable cause) {
        super(cause);
    }

}
