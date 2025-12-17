// Exception thrown when attempting to create user with duplicate username
public class DuplicateUserException extends Exception {
    public DuplicateUserException(String message) { super(message); }
}
