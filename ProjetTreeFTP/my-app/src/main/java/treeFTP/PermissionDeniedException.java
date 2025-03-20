package treeFTP;

/**
 * Exception thrown when a user does not have permission to perform an action on the server
 */
public class PermissionDeniedException extends Exception {
    public PermissionDeniedException(String message) {
        super(message);
    }
}
