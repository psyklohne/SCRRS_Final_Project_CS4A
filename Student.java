/**
 * Student user with limited permissions
 * Can only view resources and manage own reservations
 */
public class Student extends User {
    // Constructor calls parent User class with provided username
    public Student(String username) { 
        super(username); 
    }
    
    @Override
    // Students cannot manage resources (add/edit/remove)
    public boolean canManageResources() { 
        return false;
    }
    
    @Override
    // Returns the role name for display purposes
    public String getUserRole() { 
        return "Student"; 
    }
    
    @Override
    // Provides formatted string representation for printing
    public String toString() { 
        return "STUDENT: " + getUsername() + " [ID: " + getUserId() + "]"; 
    }
}
