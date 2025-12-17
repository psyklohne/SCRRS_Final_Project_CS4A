import java.util.Scanner;
import java.util.List;

/**
 * User interface for Campus Resource Reservation System
 * Provides menu-driven interaction with visual feedback
 */
public class CampusMenu {
    // Reference to the main system controller
    private CampusSystem campus;
    // Scanner for reading user input from console
    private Scanner scanner;
    // Currently logged-in user's name
    private String currentUser;
    // Flag indicating if current user has administrator privileges
    private boolean isAdmin;
    
    // Constructor initializes with system reference
    public CampusMenu(CampusSystem campus) {
        this.campus = campus;
        this.scanner = new Scanner(System.in);
    }
    
    // Main entry point for the menu system
    public void run() {
        displayWelcomeBanner();
        currentUser = promptForUsername();
        isAdmin = determineIfAdmin();
        displayWelcomeMessage();
        
        if (isAdmin) {
            showAdminMenu();
        } else {
            showStudentMenu();
        }
    }
    
    // Displays decorative welcome banner at startup
    private void displayWelcomeBanner() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("    SMART CAMPUS RESOURCE RESERVATION SYSTEM");
        System.out.println("=".repeat(60));
    }
    
    // Prompts user for username and validates non-empty input
    private String promptForUsername() {
        System.out.print("\nEnter your username: ");
        String username = scanner.nextLine().trim();
        
        while (username.isEmpty()) {
            System.out.print("Username cannot be empty. Enter username: ");
            username = scanner.nextLine().trim();
        }
        
        return username;
    }
    
    // Checks if entered username indicates administrator privileges
    private boolean determineIfAdmin() {
        return currentUser.equalsIgnoreCase("admin") || 
               currentUser.equalsIgnoreCase("administrator");
    }
    
    // Displays personalized welcome message with role information
    private void displayWelcomeMessage() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("Welcome, " + currentUser + 
                         (isAdmin ? " (Administrator)" : " (Student)"));
        System.out.println("=".repeat(40));
    }
    
    // Main loop for student menu navigation
    private void showStudentMenu() {
        String choice;
        do {
            displayStudentMenuOptions();
            choice = scanner.nextLine().trim();
            handleStudentChoice(choice);
        } while (!choice.equals("7"));  // Continue until logout (option 7)
    }
    
    // Displays the seven options available to student users
    private void displayStudentMenuOptions() {
        System.out.println("\n--- STUDENT MENU ---");
        System.out.println("1. View Available Resources");
        System.out.println("2. Search & Filter Resources");
        System.out.println("3. Make a Reservation");
        System.out.println("4. View My Reservations");
        System.out.println("5. Cancel a Reservation");
        System.out.println("6. View Resource Schedule");
        System.out.println("7. Logout");
        System.out.print("\nEnter your choice (1-7): ");
    }
    
    // Routes student menu choice to appropriate handler method
    private void handleStudentChoice(String choice) {
        switch (choice) {
            case "1": handleViewAvailableResources(); break;
            case "2": handleSearchAndFilter(); break;
            case "3": handleMakeReservation(); break;
            case "4": handleViewMyReservations(); break;
            case "5": handleCancelMyReservation(); break;
            case "6": handleViewResourceSchedule(); break;
            case "7": handleLogout(); break;
            default: System.out.println("\nInvalid choice. Please enter 1-7.");
        }
    }
    
    // Main loop for administrator menu navigation
    private void showAdminMenu() {
        String choice;
        do {
            displayAdminMenuOptions();
            choice = scanner.nextLine().trim();
            handleAdminChoice(choice);
        } while (!choice.equals("12"));  // Continue until logout (option 12)
    }
    
    // Displays twelve options available to administrator users
    private void displayAdminMenuOptions() {
        System.out.println("\n--- ADMINISTRATOR MENU ---");
        System.out.println(" 1. View All Resources");
        System.out.println(" 2. Search & Filter Resources");
        System.out.println(" 3. Add New Resource");
        System.out.println(" 4. Edit Resource");
        System.out.println(" 5. Remove Resource");
        System.out.println(" 6. View All Reservations");
        System.out.println(" 7. Cancel Any Reservation");
        System.out.println(" 8. View Resource Schedule");
        System.out.println(" 9. Manual Save System to File or Backup File");
        System.out.println("10. Manual Load System from File or Backup File");
        System.out.println("11. Export Data to Text Files");
        System.out.println("12. Logout");
        System.out.print("\nEnter your choice (1-12): ");
    }
    
    // Routes administrator menu choice to appropriate handler method
    private void handleAdminChoice(String choice) {
        switch (choice) {
            case "1": campus.printAllResources(); break;
            case "2": handleSearchAndFilter(); break;
            case "3": handleAddResource(); break;
            case "4": handleEditResource(); break;
            case "5": handleRemoveResource(); break;
            case "6": campus.printAllReservations(); break;
            case "7": handleAdminCancelReservation(); break;
            case "8": handleViewResourceSchedule(); break;
            case "9": handleSaveSystem(); break;
            case "10": handleLoadSystem(); break;
            case "11": handleExportTextFiles(); break;
            case "12": handleLogout(); break;
            default: System.out.println("\nInvalid choice. Please enter 1-12.");
        }
    }
    
    // Student operation: Displays all resources with their current availability
    private void handleViewAvailableResources() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("AVAILABLE RESOURCES");
        System.out.println("=".repeat(50));
        
        List<CampusResource> allResources = campus.getResources();
        
        for (CampusResource resource : allResources) {
            System.out.println("\n" + "-".repeat(40));
            System.out.println(resource);
            
            // Get current reservations for this specific resource
            List<Reservation> resourceReservations = campus.getResourceReservations(resource.getId());
            
            if (resourceReservations.isEmpty()) {
                // No bookings exist for this resource
                System.out.println("  Available all week!");
            } else {
                // Display existing bookings with user and time information
                System.out.println("  Currently booked for:");
                for (Reservation reservation : resourceReservations) {
                    System.out.println("  -  " + reservation.getDayName() + " " + 
                                    reservation.getTimeRange() + ": " + 
                                    reservation.getUsername());
                }
            }
        }
        System.out.println("=".repeat(60));
    }
    
    // Student operation: Guides user through creating a new reservation
    private void handleMakeReservation() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("MAKE A RESERVATION");
        System.out.println("=".repeat(50));
        
        try {
            // Show all available resources first
            campus.printAllResources();
            
            System.out.print("\nEnter the Resource ID: ");
            String resourceId = scanner.nextLine().trim();
            
            // Display day mapping (0-4 corresponds to Monday-Friday)
            System.out.println("\nDays Available:");
            System.out.println("  0 = Monday");
            System.out.println("  1 = Tuesday");
            System.out.println("  2 = Wednesday");
            System.out.println("  3 = Thursday");
            System.out.println("  4 = Friday");
            System.out.print("Enter day number (0-4): ");
            int day = getValidatedNumber(0, 4);
            
            // Display time slot options (8am-1am in 2-hour blocks)
            System.out.println("\nTime Slots Available:");
            System.out.println("  0: 8:00 AM - 10:00 AM");
            System.out.println("  1: 10:00 AM - 12:00 PM");
            System.out.println("  2: 1:00 PM - 3:00 PM");
            System.out.println("  3: 3:00 PM - 5:00 PM");
            System.out.println("  4: 5:00 PM - 7:00 PM");
            System.out.println("  5: 7:00 PM - 9:00 PM");
            System.out.println("  6: 9:00 PM - 11:00 PM");
            System.out.println("  7: 11:00 PM - 1:00 AM");
            System.out.print("Enter time slot (0-7): ");
            int slot = getValidatedNumber(0, 7);
            
            // Attempt to create reservation through main system
            Reservation reservation = campus.makeReservation(resourceId, currentUser, day, slot);
            
            // Display success confirmation with reservation details
            System.out.println("\n" + "=".repeat(50));
            System.out.println("RESERVATION SUCCESSFUL!");
            System.out.println("=".repeat(50));
            System.out.println("Reservation ID: [" + reservation.getReservationId() + "]");
            System.out.println("Resource: " + reservation.getResource().getName());
            System.out.println("Time: " + reservation.getDayName() + " " + reservation.getTimeRange());
            System.out.println("=".repeat(50));
            
        } catch (Exception e) {
            // Catch any errors during reservation process and show user-friendly message
            System.out.println("\nError: " + e.getMessage());
        }
    }
    
    // Student operation: Shows all reservations belonging to current user
    private void handleViewMyReservations() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("YOUR RESERVATIONS");
        System.out.println("=".repeat(50));
        campus.printUserReservations(currentUser);
    }
    
    // Student operation: Allows user to cancel one of their own reservations
    private void handleCancelMyReservation() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("CANCEL A RESERVATION");
        System.out.println("=".repeat(50));
        
        // First show user their current reservations
        campus.printUserReservations(currentUser);
        
        System.out.print("\nEnter Reservation ID to cancel (or type 'back' to return): ");
        String reservationId = scanner.nextLine().trim();
        
        // Allow user to back out without cancelling
        if (reservationId.equalsIgnoreCase("back")) {
            System.out.println("Cancellation cancelled.");
            return;
        }
        
        try {
            // Attempt cancellation through main system
            Reservation cancelled = campus.cancelReservation(reservationId, currentUser);
            
            // Display confirmation of successful cancellation
            System.out.println("\n" + "=".repeat(50));
            System.out.println("RESERVATION CANCELLED!");
            System.out.println("=".repeat(50));
            System.out.println("Cancelled: " + cancelled.getResource().getName());
            System.out.println("Was scheduled for: " + cancelled.getDayName() + " " + cancelled.getTimeRange());
            System.out.println("Reservation ID: " + cancelled.getReservationId() + " is now inactive");
            System.out.println("=".repeat(50));
            
        } catch (Exception e) {
            // Show error if cancellation fails (e.g., reservation not found)
            System.out.println("\nError: " + e.getMessage());
        }
    }
    
    // Administrator operation: Adds a new resource to the system
    private void handleAddResource() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("ADD NEW RESOURCE");
        System.out.println("=".repeat(50));
        
        try {
            System.out.print("Enter Resource ID (e.g., SR101, LE201): ");
            String id = scanner.nextLine().trim();
            
            System.out.print("Enter Resource Name: ");
            String name = scanner.nextLine().trim();
            
            // Prompt for resource type (Study Room or Lab Equipment)
            System.out.println("\nSelect Resource Type:");
            System.out.println("1. Study Room");
            System.out.println("2. Lab Equipment");
            System.out.print("Enter choice (1 or 2): ");
            String typeChoice = scanner.nextLine().trim();
            
            CampusResource newResource = null;
            
            if (typeChoice.equals("1")) {
                // Additional information needed for study rooms
                System.out.print("Enter room capacity (minimum 1): ");
                int capacity = getValidatedNumber(1, 100);
                newResource = new StudyRoom(id, name, capacity);
            } else if (typeChoice.equals("2")) {
                // Additional information needed for lab equipment
                System.out.print("Enter equipment type (e.g., Microscope, Laptop, Burner): ");
                String equipmentType = scanner.nextLine().trim();
                newResource = new LabEquipment(id, name, equipmentType);
            } else {
                System.out.println("\nInvalid choice. Resource not added.");
                return;
            }
            
            // Add resource through main system
            CampusResource added = campus.addResource(newResource, currentUser);
            
            // Display success confirmation
            System.out.println("\n" + "=".repeat(50));
            System.out.println("RESOURCE ADDED!");
            System.out.println("=".repeat(50));
            System.out.println("ID: " + added.getId());
            System.out.println("Name: " + added.getName());
            System.out.println("Type: " + added.getResourceType());
            System.out.println("=".repeat(50));
            
        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }
    
    // Administrator operation: Modifies existing resource properties
    private void handleEditResource() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("EDIT RESOURCE");
        System.out.println("=".repeat(50));
        
        try {
            // Show all resources so admin can choose which to edit
            campus.printAllResources();
            
            System.out.print("\nEnter Resource ID to edit: ");
            String resourceId = scanner.nextLine().trim();
            
            // Find the resource to ensure it exists
            CampusResource resource = campus.findResource(resourceId);
            if (resource == null) {
                System.out.println("\nResource not found.");
                return;
            }
            
            System.out.println("\nEditing: " + resource);
            
            // Different editing options based on resource type
            if (resource instanceof StudyRoom) {
                // Study room specific editing
                System.out.print("Enter new name (or press Enter to keep current): ");
                String newName = scanner.nextLine().trim();
                if (newName.isEmpty()) newName = null;
                
                System.out.print("Enter new capacity (or 0 to keep current): ");
                int newCapacity = getValidatedNumber(0, 100);
                if (newCapacity == 0) newCapacity = 0;
                
                boolean success = campus.editStudyRoom(resourceId, newName, newCapacity, currentUser);
                if (success) {
                    System.out.println("\nStudy Room updated successfully!");
                }
                
            } else if (resource instanceof LabEquipment) {
                // Lab equipment specific editing
                System.out.print("Enter new name (or press Enter to keep current): ");
                String newName = scanner.nextLine().trim();
                if (newName.isEmpty()) newName = null;
                
                System.out.print("Enter new equipment type (or press Enter to keep current): ");
                String newType = scanner.nextLine().trim();
                if (newType.isEmpty()) newType = null;
                
                boolean success = campus.editLabEquipment(resourceId, newName, newType, currentUser);
                if (success) {
                    System.out.println("\nLab Equipment updated successfully!");
                }
            }
            
        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }
    
    // Administrator operation: Removes resource from system (with confirmation)
    private void handleRemoveResource() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("REMOVE RESOURCE");
        System.out.println("=".repeat(50));
        
        try {
            // Show all resources for selection
            campus.printAllResources();
            
            System.out.print("\nEnter Resource ID to remove: ");
            String resourceId = scanner.nextLine().trim();
            
            CampusResource resource = campus.findResource(resourceId);
            if (resource == null) {
                System.out.println("\nResource not found.");
                return;
            }
            
            // Require confirmation before permanent deletion
            System.out.println("\nWARNING: This will permanently remove:");
            System.out.println("   " + resource);
            System.out.print("\nAre you sure? (Type 'yes' to confirm): ");
            String confirm = scanner.nextLine().trim();
            
            if (!confirm.equalsIgnoreCase("yes")) {
                System.out.println("Removal cancelled.");
                return;
            }
            
            // Attempt removal through main system
            boolean removed = campus.removeResource(resourceId, currentUser);
            if (removed) {
                System.out.println("\nResource removed successfully!");
            }
            
        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }
    
    // Administrator operation: Cancels any reservation (not just their own)
    private void handleAdminCancelReservation() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("CANCEL ANY RESERVATION (ADMIN)");
        System.out.println("=".repeat(50));
        
        try {
            // Show all reservations in system
            campus.printAllReservations();
            
            System.out.print("\nEnter Reservation ID to cancel: ");
            String reservationId = scanner.nextLine().trim();
            
            // Cancel through main system (admin override)
            Reservation cancelled = campus.cancelReservation(reservationId, currentUser);
            
            // Display admin-specific cancellation confirmation
            System.out.println("\n" + "=".repeat(50));
            System.out.println("RESERVATION CANCELLED BY ADMIN");
            System.out.println("=".repeat(50));
            System.out.println("Cancelled: " + cancelled.getUsername() + "'s reservation");
            System.out.println("Resource: " + cancelled.getResource().getName());
            System.out.println("Was scheduled for: " + cancelled.getDayName() + " " + cancelled.getTimeRange());
            System.out.println("=".repeat(50));
            
        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }
    
    // Shared operation: Provides multiple ways to search and filter resources
    private void handleSearchAndFilter() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("SEARCH & FILTER RESOURCES");
        System.out.println("=".repeat(50));
        
        System.out.println("Search Options:");
        System.out.println("1. Search by Name");
        System.out.println("2. Search by ID");
        System.out.println("3. Filter by Type");
        System.out.println("4. Filter by Attributes");
        System.out.println("5. Show All-Day Availability Only");
        System.out.println("6. Show All Resources");
        System.out.print("\nEnter choice (1-6): ");
        
        String choice = scanner.nextLine().trim();
        
        try {
            switch (choice) {
                case "1":
                    // Search by partial name match
                    System.out.print("Enter name to search: ");
                    String name = scanner.nextLine().trim();
                    List<CampusResource> results = campus.searchByName(name);
                    displaySearchResults(results, "Name search: '" + name + "'");
                    break;
                    
                case "2":
                    // Search by partial ID match
                    System.out.print("Enter ID (exact or partial): ");
                    String id = scanner.nextLine().trim();
                    List<CampusResource> partialResults = campus.searchByPartialId(id);
                    displaySearchResults(partialResults, "ID contains: '" + id + "'");
                    break;
                    
                case "3":
                    // Filter by resource type (Study Room or Lab Equipment)
                    System.out.println("Available types: Study Room, Lab Equipment");
                    System.out.print("Enter type: ");
                    String type = scanner.nextLine().trim();
                    results = campus.filterByType(type);
                    displaySearchResults(results, "Type: " + type);
                    break;
                    
                case "4":
                    // Advanced attribute-based filtering
                    System.out.println("\n" + "=".repeat(50));
                    System.out.println("FILTER BY ATTRIBUTES");
                    System.out.println("=".repeat(50));
                    
                    System.out.println("Filter Options:");
                    System.out.println("1. Study Rooms - Minimum Capacity");
                    System.out.println("2. Lab Equipment - Equipment Type");
                    System.out.print("Enter choice (1-2): ");
                    
                    String filterChoice = scanner.nextLine().trim();
                    
                    if (filterChoice.equals("1")) {
                        // Filter study rooms by minimum capacity
                        System.out.print("Enter minimum capacity required: ");
                        int minCapacity = getValidatedNumber(1, 100);
                        List<StudyRoom> rooms = campus.filterStudyRoomsByMinCapacity(minCapacity);
                        displayStudyRooms(rooms, "Study Rooms (Capacity >= " + minCapacity + ")");
                    } else if (filterChoice.equals("2")) {
                        // Filter lab equipment by equipment type
                        System.out.print("Enter equipment type (e.g., Microscope, Laptop): ");
                        String equipmentType = scanner.nextLine().trim();
                        List<LabEquipment> equipment = campus.filterLabEquipmentByType(equipmentType);
                        displayLabEquipment(equipment, "Lab Equipment - Type: " + equipmentType);
                    }
                    break;
                    
                case "5":
                    // Show only resources with no current bookings
                    campus.printAvailableResources();
                    break;
                    
                case "6":
                    // Show all resources regardless of availability
                    campus.printAllResources();
                    break;
                    
                default:
                    System.out.println("\nInvalid choice.");
            }
        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }
    
    // Shared operation: Shows schedule for specific resource
    private void handleViewResourceSchedule() {
        System.out.print("\nEnter Resource ID to view schedule: ");
        String resourceId = scanner.nextLine().trim();
        campus.printResourceSchedule(resourceId);
    }
    
    // File operation: Saves current system state to file
    private void handleSaveSystem() {
        System.out.print("\nEnter filename to save to (or press Enter for 'campus_system.txt'): ");
        String filename = scanner.nextLine().trim();
        if (filename.isEmpty()) filename = "campus_system.txt";
        
        try {
            campus.saveToFile(filename);
            System.out.println("System saved successfully to: " + filename);
        } catch (DataPersistenceException e) {
            System.out.println("\nSave failed: " + e.getMessage());
        }
    }
    
    // File operation: Loads system state from file
    private void handleLoadSystem() {
        System.out.print("\nEnter filename to load from: ");
        String filename = scanner.nextLine().trim();
        
        try {
            CampusSystem loaded = CampusSystem.loadFromFile(filename);
            this.campus = loaded;
            System.out.println("System loaded successfully from: " + filename);
        } catch (DataPersistenceException e) {
            System.out.println("\nLoad failed: " + e.getMessage());
        }
    }
    
    // File operation: Exports data to human-readable text files
    private void handleExportTextFiles() {
        try {
            campus.exportToTextFiles();
            System.out.println("\n" + "=".repeat(50));
            System.out.println("TEXT FILES EXPORTED:");
            System.out.println("=".repeat(50));
            System.out.println("resources.txt    - All campus resources");
            System.out.println("reservations.txt - All reservations (active & cancelled)");
            System.out.println("users.txt        - All system users");
            System.out.println("=".repeat(50));
        } catch (DataPersistenceException e) {
            System.out.println("\nExport failed: " + e.getMessage());
        }
    }
    
    // Helper method: Gets validated number input within specified range
    private int getValidatedNumber(int min, int max) {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                int number = Integer.parseInt(input);
                
                if (number < min || number > max) {
                    System.out.print("Please enter a number between " + min + " and " + max + ": ");
                } else {
                    return number;
                }
            } catch (NumberFormatException e) {
                System.out.print("Invalid number. Please enter a valid number: ");
            }
        }
    }
    
    // Helper method: Displays search results with formatting
    private void displaySearchResults(List<CampusResource> results, String searchType) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("SEARCH RESULTS: " + searchType);
        System.out.println("=".repeat(50));
        System.out.println("Found " + results.size() + " resource(s)");
        System.out.println("-".repeat(50));
        
        if (results.isEmpty()) {
            System.out.println("No resources found matching your search.");
        } else {
            for (CampusResource resource : results) {
                System.out.println(resource);
            }
        }
        System.out.println("=".repeat(50));
    }
    
    // Helper method: Displays filtered study rooms
    private void displayStudyRooms(List<StudyRoom> rooms, String title) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println(title);
        System.out.println("Found: " + rooms.size() + " room(s)");
        System.out.println("=".repeat(50));
        
        if (rooms.isEmpty()) {
            System.out.println("No study rooms meet the requirement.");
        } else {
            for (StudyRoom room : rooms) {
                System.out.println(room);
            }
        }
        System.out.println("=".repeat(50));
    }
    
    // Helper method: Displays filtered lab equipment
    private void displayLabEquipment(List<LabEquipment> equipment, String title) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println(title);
        System.out.println("Found: " + equipment.size() + " item(s)");
        System.out.println("=".repeat(50));
        
        if (equipment.isEmpty()) {
            System.out.println("No lab equipment of that type.");
        } else {
            for (LabEquipment item : equipment) {
                System.out.println(item);
            }
        }
        System.out.println("=".repeat(50));
    }

    // Handles logout process with farewell message
    private void handleLogout() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("Logging out, " + currentUser);
        System.out.println("=".repeat(50));
    }
}
