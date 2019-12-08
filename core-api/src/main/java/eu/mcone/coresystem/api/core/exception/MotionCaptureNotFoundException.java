package eu.mcone.coresystem.api.core.exception;

public class MotionCaptureNotFoundException extends Exception {

    public MotionCaptureNotFoundException() {
        super();
    }

    public MotionCaptureNotFoundException(String message) {
        super(message);
    }

    public MotionCaptureNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MotionCaptureNotFoundException(Throwable cause) {
        super(cause);
    }
}
