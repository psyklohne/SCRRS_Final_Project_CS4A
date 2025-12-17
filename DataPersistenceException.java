// Exception thrown when file operations fail (save/load/export)
public class DataPersistenceException extends Exception {
    public DataPersistenceException(String message) { super(message); }
}
