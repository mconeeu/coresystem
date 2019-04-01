/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
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
