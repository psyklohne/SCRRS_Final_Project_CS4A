import java.io.Serializable;

/**
 * Reservation links users to resources at specific times
 * Core object in ownership transfer between user and schedule
 */
public class Reservation implements Serializable {
    // Unique identifier for the reservation (e.g., RES-001)
    private String reservationId;
    // The resource being reserved
    private CampusResource resource;
    // Username of person making the reservation
    private String username;
    // Day index (0=Monday through 4=Friday)
    private int dayIndex;
    // Time slot index (0=8:00-10:00 through 7=23:00-1:00)
    private int slotIndex;
    // Active status (true=active, false=cancelled)
    private boolean active;
    
    // Constructor creates new active reservation
    public Reservation(String reservationId, CampusResource resource, 
                      String username, int dayIndex, int slotIndex) {
        this.reservationId = reservationId;
        this.resource = resource;
        this.username = username;
        this.dayIndex = dayIndex;
        this.slotIndex = slotIndex;
        this.active = true; // Reservations start as active
    }
    
    // Returns the unique reservation ID
    public String getReservationId() { return reservationId; }
    // Returns the reserved resource object
    public CampusResource getResource() { return resource; }
    // Returns the resource ID for convenience
    public String getResourceId() { return resource.getId(); }
    // Returns username of person who made reservation
    public String getUsername() { return username; }
    // Returns day index (0-4)
    public int getDayIndex() { return dayIndex; }
    // Returns time slot index (0-7)
    public int getSlotIndex() { return slotIndex; }
    // Returns whether reservation is still active (not cancelled)
    public boolean isActive() { return active; }
    
    // Cancels the reservation (marks as inactive)
    public void cancel() {
        active = false;
    }
    
    // Returns human-readable time range for the slot
    public String getTimeRange() {
        String[] slotTimes = {
            "8:00-10:00", "10:00-12:00", "13:00-15:00", "15:00-17:00",
            "17:00-19:00", "19:00-21:00", "21:00-23:00", "23:00-1:00"
        };
        return slotTimes[slotIndex];
    }
    
    // Returns human-readable day name
    public String getDayName() {
        String[] dayNames = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        return dayNames[dayIndex];
    }
    
    @Override
    // Provides formatted string representation with status
    public String toString() {
        String status = active ? "ACTIVE" : "CANCELLED";
        return "RESERVATION (" + reservationId + ") [" + status + "]: " +
               resource.getName() + " (" + resource.getId() + ") " +
               getDayName() + " " + getTimeRange() + " by " + username;
    }
}
