// Exception thrown when reservation conflicts with existing booking
public class ReservationConflictException extends Exception {
    public ReservationConflictException(String message) { super(message); }
}
