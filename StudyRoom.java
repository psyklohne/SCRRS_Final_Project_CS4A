/**
 * Study room resource with capacity attribute
 * Examples: Computer Lab, Group Study Room, Presentation Room
 */
public class StudyRoom extends CampusResource {
    // Maximum number of people the room can accommodate
    private int capacity;
    
    // Constructor initializes study room with ID, name, and capacity
    public StudyRoom(String id, String name, int capacity) {
        super(id, name);
        // Validate capacity is positive
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;
    }
    
    // Returns the room's capacity
    public int getCapacity() { return capacity; }
    
    // Updates room capacity with validation
    public void setCapacity(int newCapacity) { 
        if (newCapacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = newCapacity; 
    }
    
    @Override
    public String getResourceType() { 
        return "Study Room"; 
    }
    
    @Override
    // Returns capacity information for display and export
    public String getSpecificDetails() {
        return "Capacity: " + capacity + " people";
    }
    
    @Override
    // Formatted string representation for display
    public String toString() {
        return "STUDY ROOM: " + getName() + 
               " (Capacity: " + capacity + ") [ID: " + getId() + "]";
    }
}
