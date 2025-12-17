import java.io.Serializable;
import java.util.List;

/**
 * Abstract user class defining role-based permissions
 * Base class for Administrator and Student
 */
public abstract class User implements Serializable {
    // User's login name
    protected String username;
    // Unique system-generated user ID
    protected String userId;
    // Static counter for generating unique user IDs
    private static int userCounter = 1000; 
    // Tracks highest loaded user ID to prevent duplicates
    private static int lastSavedMaxId = 1000;

    // Constructor initializes user with username and generates ID
    public User(String username) {
        this.username = username;
        this.userId = generateUserId();
    }
    
    // Returns the user's login name
    public String getUsername() { return username; }
    // Returns the user's unique system ID
    public String getUserId() { return userId; }
    
    // Generates unique user ID ensuring no duplicates after save/load
    private String generateUserId() {
        // Ensure new IDs start above highest loaded ID
        if (lastSavedMaxId >= userCounter) {
            userCounter = lastSavedMaxId;
        }
        userCounter++;
        return "USER-" + userCounter;
    }
    
    // Updates ID tracking from loaded users to prevent duplicates
    public static void updateFromLoadedUsers(List<User> users) {
        for (User user : users) {
            String id = user.getUserId();
            if (id.startsWith("USER-")) {
                try {
                    // Extract numeric portion of ID
                    int num = Integer.parseInt(id.substring(5));
                    if (num > lastSavedMaxId) {
                        lastSavedMaxId = num;
                    }
                } catch (Exception e) {
                    // Skip invalid IDs
                }
            }
        }
    }
    
    // Abstract method: Determines if user can manage resources
    public abstract boolean canManageResources();
    // Abstract method: Returns user's role for display
    public abstract String getUserRole();
    
    @Override
    // Provides formatted string representation
    public String toString() {
        return username + " (" + getUserRole() + ") [ID: " + userId + "]";
    }
}
