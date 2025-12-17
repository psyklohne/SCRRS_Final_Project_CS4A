import java.io.Serializable;

/**
 * Abstract base class for all campus resources
 * Defines common interface for StudyRoom and LabEquipment
 */
public abstract class CampusResource implements Serializable {
    // Unique identifier for the resource (e.g., SR101, LE201)
    protected String id;
    // Descriptive name of the resource
    protected String name;
    
    // Constructor initializes resource with ID and name
    public CampusResource(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    // Returns the resource's unique identifier
    public String getId() { return id; }
    // Returns the resource's descriptive name
    public String getName() { return name; }
    
    // Updates the resource name
    public void setName(String newName) { 
        this.name = newName; 
    }
    
    // Abstract method that concrete classes must implement
    // Returns the type of resource (e.g., "Study Room", "Lab Equipment")
    public abstract String getResourceType();
    
    // Abstract method that concrete classes must implement
    // Returns specific details relevant to the resource type
    public abstract String getSpecificDetails();
    
    // Abstract method for string representation
    @Override
    public abstract String toString();
}
