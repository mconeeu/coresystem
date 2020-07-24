/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.exception;

public class MotionCaptureAlreadyExistsException extends RuntimeCoreException {

    public MotionCaptureAlreadyExistsException() {
        super();
    }

    public MotionCaptureAlreadyExistsException(String message) {
        super(message);
    }

    public MotionCaptureAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public MotionCaptureAlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
