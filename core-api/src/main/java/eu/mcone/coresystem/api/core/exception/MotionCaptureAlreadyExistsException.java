package eu.mcone.coresystem.api.core.exception;

public class MotionCaptureAlreadyExistsException extends Exception {

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
