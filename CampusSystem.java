import java.io.*;
import java.util.*;

/**
 * Main system controller managing all resources, users, and reservations
 * Maintains transitive ownership between users and resource schedules
 */
public class CampusSystem implements Serializable {
    // List of all resources in the system
    private List<CampusResource> resources;
    // List of all registered users
    private List<User> users;
    // List of all reservations (both active and cancelled)
    private List<Reservation> reservations;
    // List of schedules tracking availability for each resource
    private List<ResourceSchedule> schedules;
    
    // Constructor initializes empty collections and default data
    public CampusSystem() {
        resources = new ArrayList<>();
        users = new ArrayList<>();
        reservations = new ArrayList<>();
        schedules = new ArrayList<>();
        initializeDefaultData();
    }
    
    // Sets up initial sample data for testing and demonstration
    private void initializeDefaultData() {
        // Add three study rooms with different capacities
        resources.add(new StudyRoom("SR101", "Computer Lab", 20));
        resources.add(new StudyRoom("SR102", "Group Study Room", 8));
        resources.add(new StudyRoom("SR103", "Presentation Room", 12));
        
        // Add three lab equipment items from different departments
        resources.add(new LabEquipment("LE201", "Microscopes", "Biology"));
        resources.add(new LabEquipment("LE202", "Bunsen Burners", "Chemistry"));
        resources.add(new LabEquipment("LE203", "3D Printers", "Engineering"));
        
        // Create a schedule for each resource
        for (CampusResource resource : resources) {
            schedules.add(new ResourceSchedule(resource.getId()));
        }
        
        // Add default users: one admin and two students
        users.add(new Administrator("admin"));
        users.add(new Student("student1"));
        users.add(new Student("student2"));
    }
    
    // ========== VALIDATION METHODS ==========
    
    // Ensures username is not empty or null
    private void validateUsername(String username) throws InvalidInputException {
        if (username == null || username.trim().isEmpty()) {
            throw new InvalidInputException("Username cannot be empty.");
        }
    }
    
    // Ensures resource ID is not empty or null
    private void validateResourceId(String resourceId) throws InvalidInputException {
        if (resourceId == null || resourceId.trim().isEmpty()) {
            throw new InvalidInputException("Resource ID cannot be empty.");
        }
    }
    
    // Ensures day index is within valid range (0-4 for Monday-Friday)
    private void validateDayIndex(int day) throws InvalidTimeSlotException {
        if (day < 0 || day >= 5) {
            throw new InvalidTimeSlotException(
                "Invalid day. Must be 0-4 (0=Monday, 1=Tuesday, 2=Wednesday, 3=Thursday, 4=Friday).");
        }
    }
    
    // Ensures time slot index is within valid range (0-7 for 8am-1am)
    private void validateSlotIndex(int slot) throws InvalidTimeSlotException {
        if (slot < 0 || slot >= 8) {
            throw new InvalidTimeSlotException(
                "Invalid time slot. Must be 0-7 (0=8:00-10:00, 1=10:00-12:00, etc.).");
        }
    }
    
    // Generates next unique reservation ID based on existing reservations
    private String getNextReservationId() {
        int maxId = 0;
        for (Reservation r : reservations) {
            String id = r.getReservationId();
            if (id.startsWith("RES-")) {
                try {
                    // Extract numeric portion of ID
                    int num = Integer.parseInt(id.substring(4));
                    if (num > maxId) maxId = num;
                } catch (NumberFormatException e) {
                    // Skip non-numeric IDs or malformed IDs
                }
            }
        }
        return "RES-" + (maxId + 1);
    }
    
    // ========== SEARCH & FIND METHODS ==========
    
    // Finds resource by its unique ID, returns null if not found
    public CampusResource findResource(String resourceId) {
        for (CampusResource resource : resources) {
            if (resource.getId().equals(resourceId)) {
                return resource;
            }
        }
        return null;
    }
    
    // Finds user by username, returns null if not found
    public User findUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }
    
    // Finds reservation by its unique ID, returns null if not found
    public Reservation findReservation(String reservationId) {
        for (Reservation reservation : reservations) {
            if (reservation.getReservationId().equals(reservationId)) {
                return reservation;
            }
        }
        return null;
    }
    
    // Gets the schedule for a specific resource by ID
    public ResourceSchedule getSchedule(String resourceId) {
        for (ResourceSchedule schedule : schedules) {
            if (schedule.getResourceId().equals(resourceId)) {
                return schedule;
            }
        }
        return null;
    }
    
    // Gets existing schedule or creates new one if resource has no schedule yet
    private ResourceSchedule getOrCreateSchedule(String resourceId) {
        ResourceSchedule schedule = getSchedule(resourceId);
        if (schedule == null) {
            schedule = new ResourceSchedule(resourceId);
            schedules.add(schedule);
        }
        return schedule;
    }
    
    // ========== USER MANAGEMENT ==========
    
    // Checks if user has administrator privileges
    public boolean isUserAdmin(String username) {
        User user = findUser(username);
        return user != null && user.canManageResources();
    }
    
    // Adds new user to system with specified role
    public User addUser(String username, boolean isAdmin) 
        throws DuplicateUserException, InvalidInputException {
        
        validateUsername(username);
        
        // Check for duplicate username
        if (findUser(username) != null) {
            throw new DuplicateUserException("Username '" + username + "' already exists.");
        }
        
        // Create appropriate user type based on isAdmin flag
        User newUser = isAdmin ? new Administrator(username) : new Student(username);
        users.add(newUser);
        return newUser;
    }
    
    // Convenience method for testing that handles exceptions internally
    public User addTestUser(String username, boolean isAdmin) {
        try {
            return addUser(username, isAdmin);
        } catch (Exception e) {
            System.out.println("Error adding user: " + e.getMessage());
            return null;
        }
    }
    
    // ========== RESOURCE MANAGEMENT ==========
    
    // Adds new resource to system (admin only)
    public CampusResource addResource(CampusResource resource, String username) 
        throws UnauthorizedAccessException, DuplicateResourceException, InvalidInputException {
        
        // Verify user has administrator privileges
        if (!isUserAdmin(username)) {
            throw new UnauthorizedAccessException(
                "Only administrators can add resources.");
        }
        
        validateResourceId(resource.getId());
        
        // Check for duplicate resource ID
        if (findResource(resource.getId()) != null) {
            throw new DuplicateResourceException(
                "Resource ID '" + resource.getId() + "' already exists.");
        }
        
        // Add resource and create corresponding schedule
        resources.add(resource);
        schedules.add(new ResourceSchedule(resource.getId()));
        return resource;
    }
    
    // Edits basic resource properties (name only)
    public boolean editResource(String resourceId, String newName, String username) 
        throws UnauthorizedAccessException, ResourceNotFoundException, InvalidInputException {
        
        if (!isUserAdmin(username)) {
            throw new UnauthorizedAccessException(
                "Only administrators can edit resources.");
        }
        
        validateResourceId(resourceId);
        
        CampusResource resource = findResource(resourceId);
        if (resource == null) {
            throw new ResourceNotFoundException("Resource not found: " + resourceId);
        }
        
        // Update name if provided and not empty
        if (newName != null && !newName.trim().isEmpty()) {
            resource.setName(newName.trim());
        }
        
        return true;
    }
    
    // Specialized editing for StudyRoom resources
    public boolean editStudyRoom(String roomId, String newName, 
                                Integer newCapacity, String username)
        throws UnauthorizedAccessException, ResourceNotFoundException, InvalidInputException {
        
        if (!isUserAdmin(username)) {
            throw new UnauthorizedAccessException(
                "Only administrators can edit resources.");
        }
        
        validateResourceId(roomId);
        
        CampusResource resource = findResource(roomId);
        if (resource == null) {
            throw new ResourceNotFoundException("Resource not found: " + roomId);
        }
        
        // Verify resource is actually a StudyRoom
        if (!(resource instanceof StudyRoom)) {
            throw new InvalidInputException("Resource is not a StudyRoom: " + roomId);
        }
        
        StudyRoom room = (StudyRoom) resource;
        
        try {
            // Update name if provided
            if (newName != null && !newName.trim().isEmpty()) {
                room.setName(newName.trim());
            }
            // Update capacity if provided and valid
            if (newCapacity != null && newCapacity > 0) {
                room.setCapacity(newCapacity);
            }
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Invalid value: " + e.getMessage());
        }
        
        return true;
    }
    
    // Specialized editing for LabEquipment resources
    public boolean editLabEquipment(String equipId, String newName, 
                                   String newType, String username)
        throws UnauthorizedAccessException, ResourceNotFoundException, InvalidInputException {
        
        if (!isUserAdmin(username)) {
            throw new UnauthorizedAccessException(
                "Only administrators can edit resources.");
        }
        
        validateResourceId(equipId);
        
        CampusResource resource = findResource(equipId);
        if (resource == null) {
            throw new ResourceNotFoundException("Resource not found: " + equipId);
        }
        
        // Verify resource is actually LabEquipment
        if (!(resource instanceof LabEquipment)) {
            throw new InvalidInputException("Resource is not LabEquipment: " + equipId);
        }
        
        LabEquipment equipment = (LabEquipment) resource;
        
        try {
            // Update name if provided
            if (newName != null && !newName.trim().isEmpty()) {
                equipment.setName(newName.trim());
            }
            // Update equipment type if provided
            if (newType != null && !newType.trim().isEmpty()) {
                equipment.setEquipmentType(newType.trim());
            }
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Invalid value: " + e.getMessage());
        }
        
        return true;
    }
    
    // Removes resource from system (admin only, with checks)
    public boolean removeResource(String resourceId, String username) 
        throws UnauthorizedAccessException, ResourceNotFoundException, InvalidInputException {
        
        if (!isUserAdmin(username)) {
            throw new UnauthorizedAccessException(
                "Only administrators can remove resources.");
        }
        
        validateResourceId(resourceId);
        
        CampusResource resource = findResource(resourceId);
        if (resource == null) {
            throw new ResourceNotFoundException("Resource not found: " + resourceId);
        }
        
        // Prevent removal if resource has active reservations
        if (hasActiveReservations(resourceId)) {
            throw new InvalidInputException(
                "Cannot remove resource '" + resource.getName() + 
                "'. It has active reservations. Cancel them first.");
        }
        
        // Remove resource and its schedule
        resources.remove(resource);
        ResourceSchedule schedule = getSchedule(resourceId);
        if (schedule != null) schedules.remove(schedule);
        
        return true;
    }
    
    // Checks if resource has any active (non-cancelled) reservations
    private boolean hasActiveReservations(String resourceId) {
        for (Reservation reservation : reservations) {
            if (reservation.getResourceId().equals(resourceId) && 
                reservation.isActive()) {
                return true;
            }
        }
        return false;
    }
    
    // ========== RESERVATION MANAGEMENT ==========
    
    // Creates new reservation with conflict checking
    public Reservation makeReservation(String resourceId, String username,
                                      int dayIndex, int slotIndex)
        throws InvalidTimeSlotException, ResourceNotFoundException,
               ReservationConflictException, InvalidInputException,
               DuplicateReservationException {
        
        // Validate all inputs
        validateUsername(username);
        validateResourceId(resourceId);
        validateDayIndex(dayIndex);
        validateSlotIndex(slotIndex);
        
        // Auto-create user if they don't exist yet
        if (findUser(username) == null) {
            try { 
                boolean isAdmin = username.equalsIgnoreCase("admin") || 
                                username.equalsIgnoreCase("administrator");
                addUser(username, isAdmin); // Will create the user
            }
            catch (DuplicateUserException e) {
                // Should never happen since we just checked
            } catch (InvalidInputException e) {
                throw new InvalidInputException("Invalid username for reservation: " + e.getMessage());
            }
        }
        
        // Verify resource exists
        CampusResource resource = findResource(resourceId);
        if (resource == null) {
            throw new ResourceNotFoundException("Resource '" + resourceId + "' not found.");
        }
        
        // Get or create schedule for this resource
        ResourceSchedule schedule = getOrCreateSchedule(resourceId);
        
        // Check for time slot conflict
        if (schedule.hasReservationAt(dayIndex, slotIndex)) {
            throw new ReservationConflictException(
                "Time slot already reserved for " + resource.getName());
        }
        
        // Generate unique reservation ID
        String reservationId = getNextReservationId();
        
        // Double-check generated ID doesn't already exist
        if (findReservation(reservationId) != null) {
            throw new DuplicateReservationException(
                "Generated reservation ID '" + reservationId + "' already exists.");
        }
        
        // Create reservation object
        Reservation reservation = new Reservation(reservationId, resource, 
                                                 username, dayIndex, slotIndex);
        
        // Add to schedule and reservation list
        schedule.addReservation(dayIndex, slotIndex, reservation);
        reservations.add(reservation);
        
        return reservation;
    }
    
    // Cancels existing reservation with permission checking
    public Reservation cancelReservation(String reservationId, String username) 
        throws ResourceNotFoundException, UnauthorizedAccessException, 
               InvalidInputException {
        
        if (reservationId == null || reservationId.trim().isEmpty()) {
            throw new InvalidInputException("Reservation ID cannot be empty.");
        }
        
        validateUsername(username);
        
        // Find the reservation
        Reservation reservation = findReservation(reservationId);
        if (reservation == null) {
            throw new ResourceNotFoundException("Reservation not found: " + reservationId);
        }
        
        // Check permissions: owner or admin can cancel
        boolean isOwner = reservation.getUsername().equals(username);
        boolean isAdmin = isUserAdmin(username);
        if (!isOwner && !isAdmin) {
            throw new UnauthorizedAccessException(
                "You can only cancel your own reservations.");
        }
        
        // Remove from schedule and mark as cancelled
        ResourceSchedule schedule = getSchedule(reservation.getResourceId());
        if (schedule != null) {
            schedule.removeReservation(reservation.getDayIndex(), 
                                     reservation.getSlotIndex());
        }
        
        reservation.cancel();
        
        return reservation;
    }
    
    // ========== AVAILABILITY & SEARCH ==========
    
    // Checks if resource has any active reservations at all
    public boolean isResourceAvailable(String resourceId) {
        ResourceSchedule schedule = getSchedule(resourceId);
        return schedule == null || !schedule.hasActiveReservations();
    }
    
    // Checks if specific time slot is available for a resource
    public boolean isTimeSlotAvailable(String resourceId, int day, int slot) {
        ResourceSchedule schedule = getSchedule(resourceId);
        return schedule == null || !schedule.hasReservationAt(day, slot);
    }
    
    // Gets all active reservations for a specific user
    public List<Reservation> getUserReservations(String username) {
        List<Reservation> userReservations = new ArrayList<>();
        for (Reservation reservation : reservations) {
            if (reservation.getUsername().equals(username) && 
                reservation.isActive()) {
                userReservations.add(reservation);
            }
        }
        return userReservations;
    }
    
    // Gets all active reservations for a specific resource
    public List<Reservation> getResourceReservations(String resourceId) {
        List<Reservation> resourceReservations = new ArrayList<>();
        for (Reservation reservation : reservations) {
            if (reservation.getResourceId().equals(resourceId) && 
                reservation.isActive()) {
                resourceReservations.add(reservation);
            }
        }
        return resourceReservations;
    }
    
    // ========== SEARCH & FILTER METHODS ==========
    
    // Searches resources by name (partial match, case-insensitive)
    public List<CampusResource> searchByName(String nameQuery) {
        List<CampusResource> results = new ArrayList<>();
        if (nameQuery == null || nameQuery.trim().isEmpty()) return results;
        
        String lowerQuery = nameQuery.toLowerCase().trim();
        for (CampusResource resource : resources) {
            if (resource.getName().toLowerCase().contains(lowerQuery)) {
                results.add(resource);
            }
        }
        return results;
    }
    
    // Filters resources by type (Study Room or Lab Equipment)
    public List<CampusResource> filterByType(String type) {
        List<CampusResource> results = new ArrayList<>();
        if (type == null || type.trim().isEmpty()) return results;
        
        String lowerType = type.toLowerCase().trim();
        for (CampusResource resource : resources) {
            if (resource.getResourceType().toLowerCase().equals(lowerType)) {
                results.add(resource);
            }
        }
        return results;
    }
    
    // Filters resources by availability status
    public List<CampusResource> filterByAvailability(boolean available) {
        List<CampusResource> results = new ArrayList<>();
        for (CampusResource resource : resources) {
            if (isResourceAvailable(resource.getId()) == available) {
                results.add(resource);
            }
        }
        return results;
    }
    
    // Searches resources by partial ID match (case-insensitive)
    public List<CampusResource> searchByPartialId(String partialId) {
        List<CampusResource> results = new ArrayList<>();
        if (partialId == null || partialId.trim().isEmpty()) return results;
        
        String lowerPartial = partialId.toLowerCase().trim();
        for (CampusResource resource : resources) {
            if (resource.getId().toLowerCase().contains(lowerPartial)) {
                results.add(resource);
            }
        }
        return results;
    }
    
    // Filters study rooms by minimum capacity requirement
    public List<StudyRoom> filterStudyRoomsByMinCapacity(int minCapacity) {
        List<StudyRoom> results = new ArrayList<>();
        for (CampusResource resource : resources) {
            if (resource.getResourceType().equals("Study Room")) {
                StudyRoom room = (StudyRoom) resource;
                if (room.getCapacity() >= minCapacity) {
                    results.add(room);
                }
            }
        }
        return results;
    }
    
    // Filters lab equipment by equipment type (partial match, case-insensitive)
    public List<LabEquipment> filterLabEquipmentByType(String equipmentType) {
        List<LabEquipment> results = new ArrayList<>();
        if (equipmentType == null || equipmentType.trim().isEmpty()) return results;
        
        String lowerType = equipmentType.toLowerCase().trim();
        for (CampusResource resource : resources) {
            if (resource.getResourceType().equals("Lab Equipment")) {
                LabEquipment equipment = (LabEquipment) resource;
                if (equipment.getEquipmentType().toLowerCase().contains(lowerType)) {
                    results.add(equipment);
                }
            }
        }
        return results;
    }
    
    // ========== DISPLAY METHODS ==========
    
    // Prints all resources in system with count
    public void printAllResources() {
        System.out.println("\n=== ALL RESOURCES (" + resources.size() + ") ===");
        if (resources.isEmpty()) {
            System.out.println("No resources in system.");
        } else {
            for (CampusResource resource : resources) {
                System.out.println(resource);
            }
        }
    }
    
    // Prints only resources with no active reservations
    public void printAvailableResources() {
        System.out.println("\n=== ENTIRELY AVAILABLE RESOURCES ===");
        int availableCount = 0;
        for (CampusResource resource : resources) {
            if (isResourceAvailable(resource.getId())) {
                System.out.println(resource);
                availableCount++;
            }
        }
        if (availableCount == 0) {
            System.out.println("No resources currently available.");
        } else {
            System.out.println("Total available: " + availableCount);
        }
    }
    
    // Prints all reservations with active/cancelled counts
    public void printAllReservations() {
        System.out.println("\n=== ALL RESERVATIONS (" + reservations.size() + ") ===");
        if (reservations.isEmpty()) {
            System.out.println("No reservations in system.");
        } else {
            int activeCount = 0;
            for (Reservation reservation : reservations) {
                System.out.println(reservation);
                if (reservation.isActive()) activeCount++;
            }
            System.out.println("Active: " + activeCount + " | Cancelled: " + 
                             (reservations.size() - activeCount));
        }
    }
    
    // Prints active reservations for specific user
    public void printUserReservations(String username) {
        List<Reservation> userRes = getUserReservations(username);
        System.out.println("\n=== YOUR RESERVATIONS (" + userRes.size() + ") ===");
        if (userRes.isEmpty()) {
            System.out.println("You have no active reservations.");
        } else {
            for (Reservation reservation : userRes) {
                System.out.println(reservation);
            }
        }
    }
    
    // Prints weekly schedule for specific resource
    public void printResourceSchedule(String resourceId) {
        ResourceSchedule schedule = getSchedule(resourceId);
        if (schedule == null) {
            System.out.println("No schedule found for " + resourceId);
            return;
        }
        
        System.out.println("\n=== SCHEDULE FOR " + resourceId + " ===");
        List<String> contents = schedule.getScheduleContents();
        if (contents.isEmpty()) {
            System.out.println("No bookings this week.");
        } else {
            for (String booking : contents) {
                System.out.println(booking);
            }
        }
    }
    
    // ========== PERSISTENCE METHODS ==========
    
    // Saves entire system state to file using object serialization
    public void saveToFile(String filename) throws DataPersistenceException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filename))) {
            oos.writeObject(this);
            System.out.println("System saved to: " + filename);
        } catch (IOException e) {
            throw new DataPersistenceException(
                "Failed to save system: " + e.getMessage());
        }
    }
    
    // Loads entire system state from file
    public static CampusSystem loadFromFile(String filename) throws DataPersistenceException {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filename))) {
            CampusSystem loaded = (CampusSystem) ois.readObject();
            // Update user ID tracking from loaded users
            User.updateFromLoadedUsers(loaded.getUsers());
            System.out.println("System loaded from: " + filename);
            return loaded;
        } catch (FileNotFoundException e) {
            throw new DataPersistenceException("File not found: " + filename);
        } catch (IOException | ClassNotFoundException e) {
            throw new DataPersistenceException("Error loading system: " + e.getMessage());
        }
    }
    
    // Exports system data to human-readable text files
    public void exportToTextFiles() throws DataPersistenceException {
        try {
            exportResourcesToText("resources.txt");
            exportReservationsToText("reservations.txt");
            exportUsersToText("users.txt");
            System.out.println("Data exported to text files");
        } catch (IOException e) {
            throw new DataPersistenceException(
                "Failed to export text files: " + e.getMessage());
        }
    }
    
    // Exports resources data to text file with headers and formatting
    private void exportResourcesToText(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("# Campus Resources - Export Date: " + new java.util.Date());
            writer.println("# Format: ID | Name | Type | Available | Details");
            writer.println("#" + "=".repeat(60));
            
            for (CampusResource resource : resources) {
                boolean available = isResourceAvailable(resource.getId());
                writer.printf("%s | %s | %s | %b | %s%n",
                    resource.getId(),
                    resource.getName(),
                    resource.getResourceType(),
                    available,
                    resource.getSpecificDetails());
            }
        }
    }
    
    // Exports reservations data to text file
    private void exportReservationsToText(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("# Reservations - Export Date: " + new java.util.Date());
            writer.println("# Format: ReservationID | ResourceID | Username | Day | Slot | Status");
            writer.println("#" + "=".repeat(60));
            
            for (Reservation reservation : reservations) {
                String status = reservation.isActive() ? "ACTIVE" : "CANCELLED";
                writer.printf("%s | %s | %s | %d | %d | %s%n",
                    reservation.getReservationId(),
                    reservation.getResourceId(),
                    reservation.getUsername(),
                    reservation.getDayIndex(),
                    reservation.getSlotIndex(),
                    status);
            }
        }
    }
    
    // Exports users data to text file
    private void exportUsersToText(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("# System Users - Export Date: " + new java.util.Date());
            writer.println("# Format: Username | Role | UserID");
            writer.println("#" + "=".repeat(60));
            
            for (User user : users) {
                writer.printf("%s | %s | %s%n",
                    user.getUsername(),
                    user.getUserRole(),
                    user.getUserId());
            }
        }
    }
    
    // ========== GETTERS FOR TESTING ==========
    
    // Returns copy of resources list for testing
    public List<CampusResource> getResources() { return new ArrayList<>(resources); }
    // Returns copy of users list for testing
    public List<User> getUsers() { return new ArrayList<>(users); }
    // Returns copy of reservations list for testing
    public List<Reservation> getReservations() { return new ArrayList<>(reservations); }
    // Returns copy of schedules list for testing
    public List<ResourceSchedule> getSchedules() { return new ArrayList<>(schedules); }
}
