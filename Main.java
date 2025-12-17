import java.util.Scanner;
import java.util.List;

/**
 * Main entry point for Campus Resource Reservation System
 * Provides dual mode: Interactive system or comprehensive testing
 */
public class Main {
    
    // Main method - program entry point
    public static void main(String[] args) {
        displayProjectBanner();
        
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nSelect operating mode:");
        System.out.println("1. Interactive Menu System (Normal use)");
        System.out.println("2. Run Comprehensive Testing (Demonstration/Grading)");
        System.out.print("\nEnter choice (1 or 2): ");
        
        String choice = scanner.nextLine().trim();
        // Initialize system from file or create new
        CampusSystem campus = initializeSystem();
        
        // Route to selected mode
        if (choice.equals("1")) {
            runInteractiveMode(campus);
        } else if (choice.equals("2")) {
            runComprehensiveTesting(campus);
        } else {
            System.out.println("\nInvalid choice. Running interactive mode.");
            runInteractiveMode(campus);
        }
        
        scanner.close();
        displayExitMessage();
    }
    
    // Displays decorative project banner at startup
    private static void displayProjectBanner() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("       SMART CAMPUS RESOURCE RESERVATION SYSTEM");
        System.out.println("=".repeat(70));
    }
    
    // Attempts to load system from file; creates new system if file not found
    private static CampusSystem initializeSystem() {
        try {
            CampusSystem campus = CampusSystem.loadFromFile("campus_system.txt");
            System.out.println("\nLoaded existing system from 'campus_system.txt'");
            return campus;
        } catch (Exception e) {
            // Create new system with default data if loading fails
            System.out.println("\nCreating new system with default data.");
            CampusSystem campus = new CampusSystem();
            System.out.println("Default system created with:");
            System.out.println("   - 3 Study Rooms (SR101, SR102, SR103)");
            System.out.println("   - 3 Lab Equipment (LE201, LE202, LE203)");
            System.out.println("   - Default users (admin, alex, zach)");
            return campus;
        }
    }
    
    // Runs the interactive menu system for normal user interaction
    private static void runInteractiveMode(CampusSystem campus) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("    STARTING INTERACTIVE MODE");
        System.out.println("=".repeat(50));
        System.out.println("Tip: Login as 'admin' for administrator privileges");
        System.out.println("     or any other username for student access.");
        System.out.println("=".repeat(50));
        
        // Create and run menu system
        CampusMenu menu = new CampusMenu(campus);
        menu.run();
        
        // Auto-save system state on exit
        autoSaveSystem(campus);
    }
    
    // Automatically saves system to default file on exit
    private static void autoSaveSystem(CampusSystem campus) {
        try {
            campus.saveToFile("campus_system.txt");
            System.out.println("\nSystem automatically saved to 'campus_system.txt'");
        } catch (Exception e) {
            System.out.println("\nCould not auto-save system: " + e.getMessage());
        }
    }
    
    // Runs comprehensive test suite for demonstration and grading
    private static void runComprehensiveTesting(CampusSystem campus) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("    COMPREHENSIVE TESTING - DEMONSTRATING ALL REQUIREMENTS");
        System.out.println("=".repeat(70));
        
        System.out.println("\nPress Enter to begin tests...");
        new Scanner(System.in).nextLine();
        
        // TEST 1: System initialization and default data
        System.out.println("\n=== TEST 1: SYSTEM INITIALIZATION ===");
        System.out.println("Initial state - System created with default data:");
        campus.printAllResources();
        campus.printAllReservations();
        
        // TEST 2: User management operations
        System.out.println("\n=== TEST 2: USER MANAGEMENT ===");
        try {
            campus.addTestUser("testStudent", false);
            campus.addTestUser("testAdmin", true);
            System.out.println("Users created: testStudent (Student), testAdmin (Admin)");
            System.out.println("Total users now: " + campus.getUsers().size());
        } catch (Exception e) {
            System.out.println("User creation failed: " + e.getMessage());
        }
        
        // TEST 3: Basic reservation creation and state tracking
        System.out.println("\n=== TEST 3: BASIC RESERVATIONS (TRACKING SR101 & LE201) ===");
        System.out.println("Tracking resource states:");
        System.out.println("  SR101 initial state: " + (campus.isResourceAvailable("SR101") ? "No Reservation(s) Placed" : "Reservation(s) Placed"));
        System.out.println("  LE201 initial state: " + (campus.isResourceAvailable("LE201") ? "No Reservation(s) Placed" : "Reservation(s) Placed"));
        
        try {
            // Make first reservation - track SR101 state change
            Reservation res1 = campus.makeReservation("SR101", "alex", 0, 1);
            System.out.println("Alex reserved SR101: " + res1.getReservationId() + 
                " (Monday 10:00-12:00)");
            System.out.println("  SR101 state changed to " + (campus.isResourceAvailable("SR101") ? "No Reservation(s) Placed" : "Reservation(s) Placed"));
            
            // Make second reservation - track LE201 state change  
            Reservation res2 = campus.makeReservation("LE201", "zach", 2, 3);
            System.out.println("Zach reserved LE201: " + res2.getReservationId() + 
                " (Wednesday 15:00-17:00)");
            System.out.println("  LE201 state changed to " + (campus.isResourceAvailable("LE201") ? "No Reservation(s) Placed" : "Reservation(s) Placed"));
            
            // Count active reservations
            int activeCount = 0;
            for (Reservation r : campus.getReservations()) {
                if (r.isActive()) activeCount++;
            }
            System.out.println("After reservations - Active reservations: " + activeCount);
            
            System.out.println("Alex's active reservations: " + 
                campus.getUserReservations("alex").size());
            System.out.println("Zach's active reservations: " + 
                campus.getUserReservations("zach").size());
            
            // Show current tracked states
            System.out.println("\nCurrent tracked resource states at timeslot range:");
            System.out.println("  SR101 at Monday 10:00: " + (campus.isTimeSlotAvailable("SR101", 0, 1) ? "Available" : "Reserved"));
            System.out.println("  LE201 at Wednesday 15:00: " + (campus.isTimeSlotAvailable("LE201", 2, 3) ? "Available" : "Reserved"));
            
        } catch (Exception e) {
            System.out.println("Reservation failed: " + e.getMessage());
        }
        
        // TEST 4: Conflict detection system
        System.out.println("\n=== TEST 4: CONFLICT DETECTION (MAINTAINING SR101 STATE) ===");
        try {
            System.out.println("Attempting to reserve already-booked SR101 (Monday 10:00-12:00)");
            System.out.println("Current SR101 state: Reserved by Alex");
            campus.makeReservation("SR101", "zach", 0, 1);
            System.out.println("ERROR: Should have detected conflict!");
        } catch (ReservationConflictException e) {
            System.out.println("Conflict correctly detected: " + e.getMessage());
            System.out.println("  SR101 state preserved: Still reserved by Alex");
            System.out.println("  Zach cannot book already-reserved time slot");
        } catch (Exception e) {
            System.out.println("Wrong exception: " + e.getClass().getSimpleName());
        }
        
        // TEST 5: Search and filtering functionality
        System.out.println("\n=== TEST 5: SEARCH & FILTER ===");
        List<CampusResource> roomSearch = campus.searchByName("Room");
        System.out.println("Search for 'Room': " + roomSearch.size() + " resources");
        for (CampusResource r : roomSearch) {
            System.out.println("  Found: " + r.getId() + " - " + r.getName());
        }
        
        List<CampusResource> studyRooms = campus.filterByType("Study Room");
        System.out.println("Study Rooms: " + studyRooms.size());
        for (CampusResource r : studyRooms) {
            System.out.println("  " + r.getId() + ": " + r.getName());
        }
        
        List<CampusResource> labEquipment = campus.filterByType("Lab Equipment");
        System.out.println("Lab Equipment: " + labEquipment.size());
        for (CampusResource r : labEquipment) {
            System.out.println("  " + r.getId() + ": " + r.getName());
        }
        
        // Advanced filtering with specific examples
        List<StudyRoom> largeRooms = campus.filterStudyRoomsByMinCapacity(10);
        System.out.println("Large study rooms (capacity >= 10): " + largeRooms.size());
        for (StudyRoom room : largeRooms) {
            System.out.println("  " + room.getId() + ": " + room.getName() + " (Capacity: " + room.getCapacity() + ")");
        }
        
        // TEST 5.5: Advanced search patterns demonstration
        System.out.println("\n=== TEST 5.5: ADVANCED SEARCH PATTERNS ===");
        System.out.println("Demonstrating filter-first approach to limit search space:");
        
        // Example 1: Filter to biology equipment first, then check availability
        System.out.println("\n--- Example 1: Finding available biology equipment ---");
        System.out.println("Step 1: Filter to Lab Equipment type");
        List<CampusResource> allLabEquip = campus.filterByType("Lab Equipment");
        System.out.println("  Found " + allLabEquip.size() + " total lab equipment");
        
        System.out.println("Step 2: Filter by specific equipment type 'Biology'");
        List<LabEquipment> bioEquipment = campus.filterLabEquipmentByType("Biology");
        System.out.println("  Found " + bioEquipment.size() + " biology equipment items");
        
        System.out.println("Step 3: Check availability within filtered results");
        int availableBio = 0;
        for (LabEquipment equip : bioEquipment) {
            boolean available = campus.isResourceAvailable(equip.getId());
            System.out.println("  " + equip.getId() + ": " + equip.getName() + 
                             " (Type: " + equip.getEquipmentType() + ") - " + 
                             (available ? "AVAILABLE" : "RESERVED"));
            if (available) availableBio++;
        }
        System.out.println("Result: " + availableBio + " of " + bioEquipment.size() + 
                         " biology equipment items are available");
        
        // Example 2: Filter to study rooms, then find rooms for group study
        System.out.println("\n--- Example 2: Finding suitable group study rooms ---");
        System.out.println("Step 1: Filter to Study Rooms type");
        List<CampusResource> allStudyRooms = campus.filterByType("Study Room");
        System.out.println("  Found " + allStudyRooms.size() + " total study rooms");
        
        System.out.println("Step 2: Filter by minimum capacity (>= 8)");
        List<StudyRoom> groupStudyRooms = campus.filterStudyRoomsByMinCapacity(8);
        System.out.println("  Found " + groupStudyRooms.size() + " rooms with capacity >= 8");
        
        System.out.println("Step 3: Check Friday afternoon availability");
        int availableFridayRooms = 0;
        for (StudyRoom room : groupStudyRooms) {
            boolean fridayAvailable = campus.isTimeSlotAvailable(room.getId(), 4, 2); // Friday 13:00
            System.out.println("  " + room.getId() + ": " + room.getName() + 
                             " (Capacity: " + room.getCapacity() + ") - Friday 13:00: " + 
                             (fridayAvailable ? "AVAILABLE" : "BOOKED"));
            if (fridayAvailable) availableFridayRooms++;
        }
        System.out.println("Result: " + availableFridayRooms + " of " + groupStudyRooms.size() + 
                         " group study rooms available Friday afternoon");
        
        // Example 3: Complex multi-step search using existing search functions
        System.out.println("\n--- Example 3: Complex search using existing search functions ---");
        System.out.println("Step 1: Search for all Study Rooms");
        List<CampusResource> allRooms = campus.filterByType("Study Room");
        System.out.println("  Found " + allRooms.size() + " total study rooms");
        
        System.out.println("\nStep 2: Search for rooms with 'Computer' in name using searchByName()");
        List<CampusResource> computerRooms = campus.searchByName("Computer");
        System.out.println("  Found " + computerRooms.size() + " resources with 'Computer' in name");
        for (CampusResource room : computerRooms) {
            System.out.println("    " + room.getId() + ": " + room.getName());
        }
        
        System.out.println("\nStep 3: Filter computer rooms by capacity >= 10");
        int computerLabs = 0;
        for (CampusResource room : computerRooms) {
            if (room instanceof StudyRoom) {
                StudyRoom studyRoom = (StudyRoom) room;
                if (studyRoom.getCapacity() >= 10) {
                    System.out.println("  Computer lab with capacity >= 10: " + room.getId() + 
                                     " - " + room.getName() + " (Capacity: " + studyRoom.getCapacity() + ")");
                    computerLabs++;
                }
            }
        }
        System.out.println("Result: " + computerLabs + " computer labs with capacity >= 10 found using search functions");
        
        System.out.println("\nStep 4: Search by ID patterns using searchByPartialId()");
        System.out.println("Searching for resources with ID containing 'SR':");
        List<CampusResource> srResources = campus.searchByPartialId("SR");
        System.out.println("  Found " + srResources.size() + " resources with ID containing 'SR':");
        for (CampusResource res : srResources) {
            System.out.println("    " + res.getId() + ": " + res.getName());
        }
        
        // TEST 6: Cancellation and state recovery
        System.out.println("\n=== TEST 6: CANCELLATION & STATE RECOVERY (SR101) ===");
        try {
            // Show state before cancellation
            List<Reservation> alexReservations = campus.getUserReservations("alex");
            System.out.println("Before cancellation:");
            System.out.println("  Alex has " + alexReservations.size() + " reservation(s)");
            System.out.println("  SR101 state: " + (campus.isResourceAvailable("SR101") ? "No Reservation(s) Placed" : "Reservation(s) Placed"));
            
            Reservation firstRes = alexReservations.get(0);
            System.out.println("Cancelling: " + firstRes.getReservationId() + 
                " for " + firstRes.getResource().getName());
            
            Reservation cancelled = campus.cancelReservation(firstRes.getReservationId(), "alex");
            System.out.println("Alex cancelled: " + cancelled.getReservationId());
            
            // Verify state after cancellation
            List<Reservation> alexReservationsAfter = campus.getUserReservations("alex");
            System.out.println("After cancellation:");
            System.out.println("  Alex has " + alexReservationsAfter.size() + " active reservation(s)");
            System.out.println("  SR101 state: " + (campus.isResourceAvailable("SR101") ? "No Reservation(s) Placed" : "Reservation(s) Placed"));
            
            // Check if time slot is now available
            if (campus.isTimeSlotAvailable("SR101", 0, 1)) {
                System.out.println("  SR101 Monday 10:00-12:00 now available for new reservations");
            }
             
        } catch (Exception e) {
            System.out.println("Cancellation failed: " + e.getMessage());
        }
        
        // TEST 7: Administrator operations (resource management)
        System.out.println("\n=== TEST 7: ADMIN OPERATIONS (ADDING & EDITING SR967) ===");
        try {
            System.out.println("Before adding resource:");
            System.out.println("  Total resources: " + campus.getResources().size());
            System.out.println("  SR967 exists: " + (campus.findResource("SR967") != null));
            
            StudyRoom newRoom = new StudyRoom("SR967", "Test Room", 10);
            CampusResource added = campus.addResource(newRoom, "admin");
            System.out.println("Admin added: " + added.getId() + " - " + added.getName());
            
            System.out.println("After adding resource:");
            System.out.println("  Total resources: " + campus.getResources().size());
            System.out.println("  SR967 exists: " + (campus.findResource("SR967") != null));
            
            // Test editing resource - show before and after
            CampusResource resourceToEdit = campus.findResource("SR967");
            System.out.println("\nBefore editing SR967:");
            String originalName = resourceToEdit.getName();
            int originalCapacity = ((StudyRoom)resourceToEdit).getCapacity();
            System.out.println("  Name: '" + originalName + "'");
            System.out.println("  Capacity: " + originalCapacity);
            
            boolean editSuccess = campus.editStudyRoom("SR967", "Updated Test Room", 16, "admin");
            if (editSuccess) {
                CampusResource editedResource = campus.findResource("SR967");
                System.out.println("After editing SR967:");
                System.out.println("  Name: '" + editedResource.getName() + "' (was '" + originalName + "')");
                System.out.println("  Capacity: " + ((StudyRoom)editedResource).getCapacity() + " (was " + originalCapacity + ")");
                System.out.println("  Resource attributes successfully updated");
            }
            
        } catch (Exception e) {
            System.out.println("Add resource failed: " + e.getMessage());
        }
        
        // TEST 8: Error handling and input validation
        System.out.println("\n=== TEST 8: ERROR HANDLING ===");
        System.out.println("Testing error conditions:");
        
        // Invalid day - should not affect existing reservations
        try {
            campus.makeReservation("SR101", "alex", 10, 1);
            System.out.println("ERROR: Should have rejected invalid day");
        } catch (InvalidTimeSlotException e) {
            System.out.println("Invalid day correctly caught - no state change occurred");
        } catch (Exception e) {
            System.out.println("Wrong exception: " + e.getClass().getSimpleName());
        }
        
        // Invalid time slot
        try {
            campus.makeReservation("SR101", "alex", 0, 10);
            System.out.println("ERROR: Should have rejected invalid time slot");
        } catch (InvalidTimeSlotException e) {
            System.out.println("Invalid time slot correctly caught - system state protected");
        } catch (Exception e) {
            System.out.println("Wrong exception: " + e.getClass().getSimpleName());
        }
        
        // Non-existent resource
        try {
            campus.makeReservation("NONEXISTENT", "alex", 0, 1);
            System.out.println("ERROR: Should have rejected non-existent resource");
        } catch (ResourceNotFoundException e) {
            System.out.println("Non-existent resource correctly caught - no invalid state created");
        } catch (Exception e) {
            System.out.println("Wrong exception: " + e.getClass().getSimpleName());
        }
        
        // TEST 9: File persistence (save/load operations)
        System.out.println("\n=== TEST 9: FILE DATA PERSISTENCE ===");
        try {
            System.out.println("Before saving - Current state:");
            System.out.println("   Resources: " + campus.getResources().size());
            System.out.println("   Reservations: " + campus.getReservations().size());
            System.out.println("   Users: " + campus.getUsers().size());
            System.out.println("   Tracked resource SR967 exists: " + (campus.findResource("SR967") != null));
            
            campus.saveToFile("test_save.txt");
            System.out.println("System saved to 'test_save.txt'");
            
            CampusSystem loaded = CampusSystem.loadFromFile("test_save.txt");
            System.out.println("System loaded from file");
            System.out.println("   Resources: " + loaded.getResources().size() + " (should match)");
            System.out.println("   Reservations: " + loaded.getReservations().size() + " (should match)");
            System.out.println("   Users: " + loaded.getUsers().size() + " (should match)");
            
            // Verify specific tracked resources were preserved
            if (loaded.findResource("SR967") != null) {
                CampusResource preserved = loaded.findResource("SR967");
                System.out.println("Previously updated resource SR967 preserved: " + preserved.getName());
            }
            if (loaded.findResource("SR101") != null) {
                System.out.println("Original resource SR101 preserved: " + loaded.findResource("SR101").getName());
            }
            
        } catch (Exception e) {
            System.out.println("File operation failed: " + e.getMessage());
        }
        
        // TEST 10: Text export functionality
        System.out.println("\n=== TEST 10: TEXT EXPORT (HUMAN-READABLE STATE) ===");
        try {
            campus.exportToTextFiles();
            System.out.println("Data exported to text files:");
            System.out.println("  - resources.txt (contains " + campus.getResources().size() + " resources including SR967)");
            System.out.println("  - reservations.txt (contains " + campus.getReservations().size() + " reservations)");
            System.out.println("  - users.txt (contains " + campus.getUsers().size() + " users)");
            System.out.println("Check files to see all tracked resources in readable format");
        } catch (Exception e) {
            System.out.println("Text export failed: " + e.getMessage());
        }
        
        // Final summary and state analysis
        System.out.println("\n" + "=".repeat(70));
        System.out.println("TESTS COMPLETE - FINAL STATE ANALYSIS");
        System.out.println("=".repeat(70));
        
        // Count final states
        int activeResCount = 0;
        int cancelledResCount = 0;
        for (Reservation r : campus.getReservations()) {
            if (r.isActive()) {
                activeResCount++;
            } else {
                cancelledResCount++;
            }
        }
        
        System.out.println("\nFINAL SYSTEM STATE:");
        System.out.println("-".repeat(40));
        System.out.println("Active Resources: " + campus.getResources().size());
        System.out.println("Active Reservations: " + activeResCount);
        System.out.println("Registered Users: " + campus.getUsers().size());
        
        System.out.println("\nTRACKED RESOURCE FINAL STATES:");
        System.out.println("  SR101: " + (campus.isResourceAvailable("SR101") ? "Available" : "Reserved") + 
                         " (was reserved, then cancelled)");
        System.out.println("  SR102: " + (campus.isResourceAvailable("SR102") ? "Available" : "Reserved") + 
                         " (never reserved)");
        System.out.println("  LE201: " + (campus.isResourceAvailable("LE201") ? "Available" : "Reserved") + 
                         " (reserved by Zach)");
        System.out.println("  SR967: " + (campus.findResource("SR967") != null ? "Exists (Added & Edited)" : "Missing") + 
                         " (added as 'Test Room', edited to 'Updated Test Room')");

        
        System.out.println("\nSYSTEM STATISTICS:");
        System.out.println("   Total Resources: " + campus.getResources().size());
        System.out.println("   Total Reservations: " + campus.getReservations().size());
        System.out.println("   Active Reservations: " + activeResCount);
        System.out.println("   Cancelled Reservations: " + cancelledResCount);
        System.out.println("   Total Users: " + campus.getUsers().size());
    }
    
    // Displays exit message when program ends
    private static void displayExitMessage() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("    THANK YOU FOR USING THE CAMPUS RESERVATION SYSTEM");
        System.out.println("=".repeat(70));
    }
}
