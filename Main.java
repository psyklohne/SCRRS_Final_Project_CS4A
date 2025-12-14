import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CampusSystem system = new CampusSystem();

        // Load saved data
        system.loadData();

        System.out.println("=== Smart Campus Resource Reservation System ===");

        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Are you an administrator? (y/n): ");
        boolean isAdmin = scanner.nextLine().equalsIgnoreCase("y");

        User currentUser;
        if (isAdmin) {
            currentUser = new Administrator(username);
        } else {
            currentUser = new Student(username);
        }

        boolean running = true;
        while (running) {
            if (currentUser instanceof Administrator) {
                running = adminMenu(scanner, system);
            } else {
                running = studentMenu(scanner, system, currentUser);
            }
        }

        // Save data before exiting
        system.saveData();
        System.out.println("Goodbye!");
        scanner.close();
    }

    // ---------------- ADMIN MENU ----------------
    private static boolean adminMenu(Scanner scanner, CampusSystem system) {
        System.out.println("\n--- Administrator Menu ---");
        System.out.println("1. Add study room");
        System.out.println("2. View all resources");
        System.out.println("3. Remove resource");
        System.out.println("0. Exit");

        System.out.print("Choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        switch (choice) {
            case 1:
                System.out.print("Room ID: ");
                String id = scanner.nextLine();
                System.out.print("Name: ");
                String name = scanner.nextLine();
                System.out.print("Capacity: ");
                int capacity = scanner.nextInt();
                scanner.nextLine();

                system.addStudyRoom(id, name, capacity);
                break;

            case 2:
                system.listAllResources();
                break;

            case 3:
                System.out.print("Enter resource ID to remove: ");
                String removeId = scanner.nextLine();
                system.removeResource(removeId);
                break;

            case 0:
                return false;

            default:
                System.out.println("Invalid option.");
        }
        return true;
    }

    // ---------------- STUDENT MENU ----------------
    private static boolean studentMenu(Scanner scanner, CampusSystem system, User user) {
        System.out.println("\n--- Student Menu ---");
        System.out.println("1. View all resources");
        System.out.println("2. Make reservation");
        System.out.println("3. View my reservations");
        System.out.println("4. Cancel reservation");
        System.out.println("0. Exit");

        System.out.print("Choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                system.listAllResources();
                break;

            case 2:
                System.out.print("Resource ID: ");
                String resourceId = scanner.nextLine();
                System.out.print("Start time (yyyy-MM-ddTHH:mm): ");
                String start = scanner.nextLine();
                System.out.print("End time (yyyy-MM-ddTHH:mm): ");
                String end = scanner.nextLine();

                system.createReservation(user.getUsername(), resourceId, start, end);
                break;

            case 3:
                system.viewUserReservations(user.getUsername());
                break;

            case 4:
                System.out.print("Reservation ID: ");
                String resId = scanner.nextLine();
                system.cancelReservation(resId);
                break;

            case 0:
                return false;

            default:
                System.out.println("Invalid option.");
        }
        return true;
    }
}
