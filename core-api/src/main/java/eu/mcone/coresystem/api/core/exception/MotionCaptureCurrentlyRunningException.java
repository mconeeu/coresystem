/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.exception;

public class MotionCaptureCurrentlyRunningException extends Exception {

    public MotionCaptureCurrentlyRunningException() {
        super();
    }

    public MotionCaptureCurrentlyRunningException(String message) {
        super(message);
    }

    public MotionCaptureCurrentlyRunningException(String message, Throwable cause) {
        super(message, cause);
    }

    public MotionCaptureCurrentlyRunningException(Throwable cause) {
        super(cause);
    }
}
