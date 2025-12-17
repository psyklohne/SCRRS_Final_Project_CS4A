// Exception thrown when attempting to create duplicate reservation
public class DuplicateReservationException extends Exception {
    public DuplicateReservationException(String message) { super(message); }
}
