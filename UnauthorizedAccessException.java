// Exception thrown when user attempts unauthorized operation
public class UnauthorizedAccessException extends Exception {
    public UnauthorizedAccessException(String message) { super(message); }
}
