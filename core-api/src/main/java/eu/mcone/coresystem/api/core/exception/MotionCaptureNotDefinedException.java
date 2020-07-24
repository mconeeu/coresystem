/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.exception;

public class MotionCaptureNotDefinedException extends RuntimeCoreException {

    public MotionCaptureNotDefinedException() {
        super();
    }

    public MotionCaptureNotDefinedException(String message) {
        super(message);
    }

    public MotionCaptureNotDefinedException(String message, Throwable cause) {
        super(message, cause);
    }

    public MotionCaptureNotDefinedException(Throwable cause) {
        super(cause);
    }
}
