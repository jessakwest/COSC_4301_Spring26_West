package ui;

import service.WardenService;
import domain.Warden;
import java.util.*;

public class ConsoleUI {
    private final WardenService service;
    private final Menu menu;
    private final Scanner scanner;

    //constructor
    public ConsoleUI(WardenService service) {
        this.service = service;
        this.menu = new Menu();
        this.scanner = new Scanner(System.in);
    }

    //entry point for UI
    public void start() {
        boolean running = true;

        while (running) {
            menu.displayMainMenu();
            int choice = scanner.nextInt();
            scanner.nextLine(); // eat empty newline

            switch (choice) {
                case 1:
                    handleAddWarden();
                    break;
                case 2:
                    handleViewWarden();
                    break;
                case 3:
                    handleUpdateWarden();
                    break;
                case 4:
                    handleManageCertifications();
                    break;
                case 5:
                    handleTerminateWarden();
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            } // end switch(choice)
        } // end while(running)
    }
    private void displayUpdateMenu() {
        boolean running = true;

        while (running) {
            menu.displayUpdateMenu();
            int choice = scanner.nextInt();
            scanner.nextLine(); // eat empty newline

            switch (choice) {
                case 1:
                    handleUpdateRole();
                    break;
                case 2:
                    handleUpdateStatus();
                    break;
                case 0:
                    running = false;
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            } // end switch(choice)
        } // end while(running)
    }
    private void displayCertificationMenu() {
        boolean running = true;

        while (running) {
            menu.displayCertificationMenu();
            int choice = scanner.nextInt();
            scanner.nextLine(); // eat empty newline

            switch (choice) {
                case 1:
                    handleAddCertification();
                    break;
                case 2:
                    handleViewCertifications();
                    break;
                case 0:
                    running = false;
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            } // end switch(choice)
        } // end while(running)
    }

    //handlers for main menu selections
    private void handleAddWarden(){
        System.out.println("Selected: Add New Warden");

        String firstName = promptRequired("Enter first name: ");
        String lastName = promptOptional("Enter (optional) last name: ");
        String role = promptRequired("Enter role: ");
        String status = promptRequired("Enter status: ");
        String clearance = promptRequired("Enter clearance: ");

        System.out.print("\nConfirm Creations? [Y/N]: ");
        String confirm = scanner.nextLine();

        if (confirm.equalsIgnoreCase("Y")) {
            String result = service.addWarden(firstName.trim().toLowerCase(), lastName.trim().toLowerCase(), role.trim().toLowerCase(), status.trim().toLowerCase(),clearance.trim().toLowerCase());
            System.out.println(result);
        } else {
            System.out.println("Creation cancelled.");
        }
    }
    private void handleViewWarden(){
        System.out.println("Selected: View All Wardens");

        List<Warden> wardens = service.getAllWardens();
        System.out.println("\nList of All Wardens:");
        if (!wardens.isEmpty()) {
            System.out.printf("%-5s %-12s %-12s %-18s %-12s %-12s%n", "ID", "FIRST NAME", "LAST NAME", "ROLE", "STATUS", "CLEARANCE");
            for (int i = 0; i < 75; i++) {
                System.out.print("-");
            }
            System.out.println();
            for (Warden w : wardens) {
                System.out.printf("%-5d %-12s %-12s %-18s %-12s %-12s%n", w.getId(), w.getFirstName(), w.getLastName(), w.getRole(), w.getStatus(), w.getClearance());
            }
            System.out.println("Total Records: " + wardens.size());
        } else {
            System.out.println("No wardens found");
        }
    }
    private void handleUpdateWarden(){
        System.out.println("Selected: Update Warden");
        displayUpdateMenu();
    }
    private void handleManageCertifications(){
        System.out.println("Selected: Manage Certifications");
        displayCertificationMenu();
    }
    private void handleTerminateWarden(){
        System.out.println("Selected: Terminate Warden");

        int id = promptRequiredInt("Enter ID#: ");

        System.out.print("\nConfirm Termination? [Y/N]: ");
        String confirm = scanner.nextLine();

        if (confirm.equalsIgnoreCase("Y")) {
            String result = service.terminateWarden(id);
            System.out.println(result);
        } else {
            System.out.println("Termination cancelled.");
        }
    }

    //handlers for update menu selections
    private void handleUpdateRole() {
        System.out.println("Selected: Update Role");

        int id = promptRequiredInt("Enter ID#: ");
        String newRole = promptRequired("New Role: ");

        System.out.print("\nConfirm Changes? [Y/N]: ");
        String confirm = scanner.nextLine();

        if (confirm.equalsIgnoreCase("Y")) {
            String result = service.updateWardenRole(id, newRole.trim().toLowerCase());
            System.out.println(result);
        } else {
            System.out.println("Update role cancelled.");
        }
    }
    private void handleUpdateStatus() {
        System.out.println("Selected: Update Status");

        int id = promptRequiredInt("Enter ID#: ");
        String newStatus = promptRequired("New Status: ");

        System.out.print("\nConfirm Changes? [Y/N]: ");
        String confirm = scanner.nextLine();

        if (confirm.equalsIgnoreCase("Y")) {
            String result = service.updateWardenStatus(id, newStatus.toLowerCase());
            System.out.println(result);
        } else {
            System.out.println("Update status cancelled.");
        }
    }

    //handlers for certifications menu selections
    private void handleAddCertification() {
        System.out.println("Selected: Add Certification");

        int id = promptRequiredInt("Enter ID#: ");
        String certificationName = promptRequired("Enter certification name: ");
        String earnedDate = promptRequired("Enter earned date (YYYY-MM-DD): ");
        String expirationDate = promptOptional("Enter (optional) expiration date (YYYY-MM-DD): ");

        System.out.print("\nConfirm Changes? [Y/N]: ");
        String confirm = scanner.nextLine();

        if (confirm.equalsIgnoreCase("Y")) {
            String result = service.addCertification(id, certificationName.toLowerCase(), earnedDate, expirationDate);
            System.out.println(result);
        } else {
            System.out.println("Adding certification cancelled.");
        }

        System.out.println();
    }
    private void handleViewCertifications() {
        System.out.println("Selected: View Certifications of a Warden");
        int id = promptRequiredInt("Enter their ID#: ");
        String result = service.viewCertifications(id);
        System.out.println(result);
        System.out.println();
    }

    //helper prompt validation methods
    private String promptRequired(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();

            if (!input.isEmpty()) {
                return input;
            }

            System.out.println("This field is required.");
        }
    }
    private int promptRequiredInt(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("This field is required. Use only numbers.");
                continue;
            }
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("This field is required. Use only numbers.");
            }
        }
    }
    private String promptOptional (String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }
}
