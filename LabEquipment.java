/**
 * Lab equipment resource with equipment type attribute
 * Examples: Microscopes, Bunsen Burners, 3D Printers
 */
public class LabEquipment extends CampusResource {
    // Type of equipment (e.g., "Biology", "Chemistry", "Engineering")
    private String equipmentType;
    
    // Constructor initializes lab equipment with ID, name, and equipment type
    public LabEquipment(String id, String name, String equipmentType) {
        super(id, name);
        this.equipmentType = equipmentType;
    }
    
    // Returns the equipment type
    public String getEquipmentType() { return equipmentType; }
    
    // Updates equipment type with validation
    public void setEquipmentType(String newType) { 
        if (newType == null || newType.trim().isEmpty()) {
            throw new IllegalArgumentException("Equipment type cannot be empty");
        }
        this.equipmentType = newType.trim(); 
    }
    
    @Override
    public String getResourceType() { 
        return "Lab Equipment"; 
    }
    
    @Override
    // Returns equipment-specific details for display and export
    public String getSpecificDetails() {
        return "Type: " + equipmentType;
    }
    
    @Override
    // Formatted string representation for display
    public String toString() {
        return "LAB EQUIPMENT: " + getName() + 
               " [" + equipmentType + "] [ID: " + getId() + "]";
    }
}
