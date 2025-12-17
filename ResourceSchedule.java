import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Weekly schedule tracking reservations for a single resource
 * Manages time-based ownership of resource
 */
public class ResourceSchedule implements Serializable {
    // Constants defining schedule dimensions
    public static final int DAYS_PER_WEEK = 5;  // Monday-Friday
    public static final int SLOTS_PER_DAY = 8;  // 8 time slots per day
    
    // 2D array representing weekly schedule (days × time slots)
    private Reservation[][] weeklySchedule;
    // ID of resource this schedule belongs to
    private String resourceId;
    
    // Constructor creates empty schedule for specified resource
    public ResourceSchedule(String resourceId) {
        this.resourceId = resourceId;
        this.weeklySchedule = new Reservation[DAYS_PER_WEEK][SLOTS_PER_DAY];
    }
    
    // Returns the resource ID this schedule tracks
    public String getResourceId() { return resourceId; }
    
    // Adds reservation to specific day/time slot
    public void addReservation(int day, int slot, Reservation reservation) {
        validateIndices(day, slot);
        weeklySchedule[day][slot] = reservation;
    }
    
    // Removes reservation from specific day/time slot
    public void removeReservation(int day, int slot) {
        validateIndices(day, slot);
        weeklySchedule[day][slot] = null;
    }
    
    // Gets reservation at specific day/time slot
    public Reservation getReservation(int day, int slot) {
        validateIndices(day, slot);
        return weeklySchedule[day][slot];
    }
    
    // Checks if specific time slot has an active reservation
    public boolean hasReservationAt(int day, int slot) {
        validateIndices(day, slot);
        Reservation reservation = weeklySchedule[day][slot];
        return reservation != null && reservation.isActive();
    }
    
    // Checks if resource has any active reservations at all
    public boolean hasActiveReservations() {
        for (int day = 0; day < DAYS_PER_WEEK; day++) {
            for (int slot = 0; slot < SLOTS_PER_DAY; slot++) {
                if (hasReservationAt(day, slot)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    // Returns list of all active reservations in this schedule
    public List<Reservation> getAllReservations() {
        List<Reservation> allReservations = new ArrayList<>();
        for (int day = 0; day < DAYS_PER_WEEK; day++) {
            for (int slot = 0; slot < SLOTS_PER_DAY; slot++) {
                Reservation reservation = weeklySchedule[day][slot];
                if (reservation != null && reservation.isActive()) {
                    allReservations.add(reservation);
                }
            }
        }
        return allReservations;
    }
    
    // Returns human-readable schedule contents for display
    public List<String> getScheduleContents() {
        List<String> contents = new ArrayList<>();
        String[] dayNames = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        String[] slotTimes = {
            "8:00-10:00", "10:00-12:00", "13:00-15:00", "15:00-17:00",
            "17:00-19:00", "19:00-21:00", "21:00-23:00", "23:00-1:00"
        };
        
        // Iterate through all time slots to find bookings
        for (int day = 0; day < DAYS_PER_WEEK; day++) {
            for (int slot = 0; slot < SLOTS_PER_DAY; slot++) {
                Reservation res = weeklySchedule[day][slot];
                if (res != null && res.isActive()) {
                    // Format: "Monday 8:00-10:00: username (RES-001)"
                    contents.add(dayNames[day] + " " + slotTimes[slot] + ": " + 
                                res.getUsername() + " (" + res.getReservationId() + ")");
                }
            }
        }
        return contents;
    }
    
    // Validates day and slot indices are within bounds
    private void validateIndices(int day, int slot) {
        if (day < 0 || day >= DAYS_PER_WEEK) {
            throw new IndexOutOfBoundsException(
                "Invalid day index: " + day + ". Must be 0-" + (DAYS_PER_WEEK-1));
        }
        if (slot < 0 || slot >= SLOTS_PER_DAY) {
            throw new IndexOutOfBoundsException(
                "Invalid time slot: " + slot + ". Must be 0-" + (SLOTS_PER_DAY-1));
        }
    }
    
    @Override
    // Provides summary string of schedule
    public String toString() {
        int activeCount = getAllReservations().size();
        return "Schedule for " + resourceId + " (" + activeCount + " active reservations)";
    }
}
