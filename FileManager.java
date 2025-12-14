import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    private static final String RESOURCE_FILE = "resources.txt";
    private static final String RESERVATION_FILE = "reservations.txt";

    // ---------- RESOURCES ----------
    public static void saveResources(List<CampusResource> resources) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RESOURCE_FILE))) {
            for (CampusResource r : resources) {
                writer.write(r.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving resources.");
        }
    }

    public static List<CampusResource> loadResources() {
        List<CampusResource> resources = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(RESOURCE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts[0].equals("STUDYROOM")) {
                    String id = parts[1];
                    String name = parts[2];
                    int capacity = Integer.parseInt(parts[3]);
                    resources.add(new StudyRoom(id, name, capacity));
                }
            }
        } catch (IOException e) {
            // File may not exist yet — acceptable
        }
        return resources;
    }

    // ---------- RESERVATIONS ----------
    public static void saveReservations(List<Reservation> reservations) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RESERVATION_FILE))) {
            for (Reservation r : reservations) {
                writer.write(r.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving reservations.");
        }
    }

    public static List<Reservation> loadReservations() {
        List<Reservation> reservations = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(RESERVATION_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                reservations.add(Reservation.fromFileString(line));
            }
        } catch (IOException e) {
            // File may not exist yet
        }
        return reservations;
    }
}
