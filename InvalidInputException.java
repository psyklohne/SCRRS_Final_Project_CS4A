// Exception thrown for invalid user input (empty fields, invalid values)
public class InvalidInputException extends Exception {
    public InvalidInputException(String message) { super(message); }
}
