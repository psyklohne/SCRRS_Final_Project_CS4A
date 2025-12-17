// Exception thrown when attempting to create resource with duplicate ID
public class DuplicateResourceException extends Exception {
    public DuplicateResourceException(String message) { super(message); }
}
