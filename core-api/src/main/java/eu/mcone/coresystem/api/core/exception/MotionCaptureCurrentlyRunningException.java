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
