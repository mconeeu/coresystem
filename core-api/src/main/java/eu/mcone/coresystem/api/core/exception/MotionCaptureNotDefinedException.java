package eu.mcone.coresystem.api.core.exception;

public class MotionCaptureNotDefinedException extends Exception {

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
