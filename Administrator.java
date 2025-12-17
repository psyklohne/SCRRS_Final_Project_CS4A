/**
 * Administrator user with full permissions for system management
 */
public class Administrator extends User {
    // Constructor calls parent User class with provided username
    public Administrator(String username) { 
        super(username); 
    }
    
    @Override
    public boolean canManageResources() { 
        // Administrators can always manage resources (add/edit/remove)
        return true;
    }
    
    @Override
    public String getUserRole() { 
        // Returns the role name for display purposes
        return "Administrator"; 
    }
    
    @Override
    public String toString() { 
        // Provides formatted string representation for printing
        return "ADMIN: " + getUsername() + " [ID: " + getUserId() + "]"; 
    }
}
