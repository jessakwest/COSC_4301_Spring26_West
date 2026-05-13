package com.neonark.cli;

import com.neonark.cli.dto.*;

import java.util.Scanner;

public class Main {
    //private global variables
    private static final Scanner scanner = new Scanner(System.in);
    private static final ApiClient apiClient = new ApiClient();
    private static final String DIVIDER = "-------------------------------------------------------------------";

    public static void main(String[] args) {
        menuSelection();
        System.out.println("\nend of program.");
    }

    //menu related
    public static void displayHeader() {
        System.out.println();
        for (int i = 0; i < 27; i++) {
            System.out.print("=");
        }
        System.out.print("\n\tNEON ARK CLI SYSTEM\n");
        for (int i = 0; i < 27; i++) {
            System.out.print("=");
        }
        System.out.println();
    }
    public static void displayMainMenu() {
        System.out.print("""
                
                1. List all creatures
                2. View creature by ID
                3. Register new creature
                4. Rename creature
                5. Remove creature
                6. View creature observations/notes
                7. Find creatures by feeding time
                
                --- Admin Only ---
                8. View all system users
                
                0. Exit
                --------------------------
                """);
        System.out.print("Select an option: ");
    }
    public static void menuSelection() {
        boolean running = true;

        while (running) {
            displayHeader();
            displayMainMenu();

            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    System.out.println("\nSelected: List all creatures");
                    listAllCreatures();
                    break;
                case "2":
                    System.out.println("\nSelected: View creature by ID");
                    viewCreatureById();
                    break;
                case "3":
                    System.out.println("\nSelected: Register new creature");
                    registerCreature();
                    break;
                case "4":
                    System.out.println("\nSelected: Rename creature");
                    renameCreature();
                    break;
                case "5":
                    System.out.println("\nSelected: Remove creature");
                    removeCreature();
                    break;
                case "6":
                    System.out.println("\nSelected: View creature observations/notes");
                    viewCreatureObservations();
                    break;
                case "7":
                    System.out.println("\nSelected: Find creatures by feeding time");
                    findCreaturesByFeedingTime();
                    break;
                case "8":
                    System.out.println("\nSelected: View all system users (Admin Only)");
                    System.out.println("Signing in as ADMIN....\n");
                    viewAllUsers();
                    break;
                case "0":
                    System.out.println("\nSelected: Exit");
                    //confirm exit
                    boolean confirmed = promptYesNo("Confirm exit? (y/n)");
                    if(confirmed) {
                        running = false;
                        scanner.close();
                        System.out.println("\nExiting Neon Ark CLI...");
                    }
                    break;
                default:
                    System.out.println("\nInvalid input. Try again.");
                    //pause();
            }
        }
    }

    //route 1.a and 1.b: GET /api/creatures -- all creatures, including removed or only active
    public static void listAllCreatures() {
        try {
            //action confirmation: all or just active creatures
            boolean includeRemoved = promptYesNo("\nInclude removed creatures? (y/n): ");

            if (includeRemoved) {
                System.out.println(("\nCREATURE REGISTRY: all, including removed"));
            } else {
                System.out.println(("\nCREATURE REGISTRY: active only"));
            }

            CreatureResponse[] creatures = apiClient.getAllCreatures(includeRemoved);
            System.out.println(DIVIDER);

            //edge case: none found
            if (creatures.length == 0) {
                System.out.println(("No creatures found\n"));
                return;
            }

            //output header
            System.out.printf("%-5s %-10s %-25s %-15s %-10s %-10s %n%s%n",
                    "ID", "NAME", "HABITAT", "STATUS", "ACTIVE", "REMOVED AT", DIVIDER);
            //output body
            for (CreatureResponse c : creatures) {
                System.out.printf(
                        "%-5d %-10s %-25s %-15s %-10s %-10s %n",
                        c.getId(),
                        c.getName(),
                        c.getHabitatName(),
                        c.getStatus(),
                        c.getRemovedAt() == null ? "Yes" : "No",
                        c.getRemovedAt()
                );
            }
        } catch (Exception e) {
            System.out.println("API Error: " + e.getMessage());
        }
    }

    //route 2: GET /api/creaturess/{id}
    public static void viewCreatureById() {
        try {
            Long id = promptLong("\nEnter creature id:");
            CreatureResponse creature = apiClient.getCreatureById(id);
            //output header
            System.out.println("\nCREATURE DETAILS");
            System.out.println(DIVIDER);
            //output body
            System.out.printf("%-15s %s%n", "ID:", creature.getId());
            System.out.printf("%-15s %s%n", "NAME:", creature.getName());
            System.out.printf("%-15s %s%n", "HABITAT:", creature.getHabitatName());
            System.out.printf("%-15s %s%n", "STATUS:", creature.getStatus());
            System.out.printf("%-15s %s%n", "CREATED AT:", creature.getCreatedAt());
            System.out.printf("%-15s %s%n", "UPDATED AT:", creature.getUpdatedAt());
            System.out.printf("%-15s %s%n", "ACTIVE:", creature.getRemovedAt() == null ? "Yes" : "No");
            System.out.printf("%-15s %s%n", "REMOVED AT:", creature.getRemovedAt());
            System.out.println(DIVIDER);

        } catch (Exception e) {
            System.out.println("API Error: " + e.getMessage());
        }
    }

    //route 3: POST /api/creatures
    public static void registerCreature() {
        try {
            //output header
            System.out.println("\nREGISTER NEW CREATURE");
;
            // gather info
            String name = promptString("Name: ");
            String status = promptString("Status: ");
            Long habitatId = promptLong("Habitat ID: ");

            //create creature
            CreateCreatureRequest request = new CreateCreatureRequest(name, status, habitatId);
            CreatureResponse creature = apiClient.createCreature(request);

            System.out.println(DIVIDER);
            //output new creature's info
            System.out.println("Creature registered successfully.");
            System.out.printf("%-15s %s%n", "Registered Creature's ID:", creature.getId());
            System.out.printf("%-15s %s%n", "Name:", creature.getName());
            System.out.printf("%-15s %s%n", "Status:", creature.getStatus());
            System.out.printf("%-15s %s%n", "Habitat:", creature.getHabitatName());

        } catch (Exception e) {
            System.out.println("API Error: " + e.getMessage());
        }
    }

    //route 4: PUT /api/creatures/{id}/name
    public static void renameCreature() {
        try {
            System.out.println("\nRENAME CREATURE");
            // gather info
            Long creatureId = promptLong("Creature ID: ");
            String name = promptString("New name: ");
            //action confirmation
            boolean confirmed = promptYesNo("\nConfirm name change to: " + name + " ? (y/n): ");
            if (!confirmed) {
                System.out.println(("\nCreature rename cancelled.\n" + DIVIDER));
                return;
            }

            //proceed: rename creature
            RenameCreatureRequest request = new RenameCreatureRequest(name);
            CreatureResponse creature = apiClient.renameCreature(creatureId, request);

            System.out.println(DIVIDER);
            //output new name
            System.out.println("Creature renamed successfully.");
            System.out.printf("%-15s %s%n", "Creature's new name:", creature.getName());

        } catch (Exception e) {
            System.out.println("API Error: " + e.getMessage());
        }
    }

    //route 5: DELETE /api/creatures/{id}
    public static void removeCreature() {
        try {
            System.out.println("\nREMOVE CREATURE");
            Long id = promptLong("Creature ID: ");

            //action confirmation
            boolean confirmed = promptYesNo("Confirm removal? (y/n): ");
            if (!confirmed) {
                System.out.println(("\nCreature removal cancelled.\n" + DIVIDER));
                return;
            }

            //proceed to delete
            apiClient.deleteCreature(id);
            System.out.println(("\nCreature removal successful.\n" + DIVIDER));


        } catch (Exception e) {
            System.out.println("API Error: " + e.getMessage());
        }
    }

    //route 6: GET /api/creatures/{id}/observations
    public static void viewCreatureObservations() {
        try {
            Long id = promptLong("Creature ID: ");
            CreatureObservationsResponse response = apiClient.getCreatureObservations(id);

            System.out.println("\nCREATURE " + id + "'s OBSERVATIONS");
            System.out.println(DIVIDER);

            //edge case: none found
            if (response.getObservations().isEmpty()) {
                System.out.println(("No observations found\n"));
                return;
            }
            //output header
            System.out.printf("%-5s %-20s %-30s%n%s%n",
                    "ID", "AUTHOR", "NOTE", DIVIDER);
            //output body
            for (ObservationResponse o : response.getObservations()) {
                System.out.printf(
                        "%-5d %-20s %-30s%n",
                        o.getId(),
                        o.getAuthor(),
                        o.getNote()
                );
            } // end enhanced for
        } catch (Exception e) {
            System.out.println("API Error: " + e.getMessage());
            } // end catch block
        }

    //route 7: GET /api/feedings?time={HH:MM} -- feeding schedules by id
    public static void findCreaturesByFeedingTime() {
        try {
            String feedingTime = promptTime("Enter feeding time (HH:MM): ");
            FeedingResponse[] feedings = apiClient.getFeedingsByTime(feedingTime);

            System.out.println("\nCREATURES SCHEDULED FOR FEEDING at  " + feedingTime);
            System.out.println(DIVIDER);

            //edge case: none found
            if (feedings.length == 0) {
                System.out.println(("No creatures scheduled for feedings at that time.\n"));
                return;
            }
            //output header
            System.out.printf("%-5s %-20s %-15s%n%s%n",
                    "ID", "CREATURE", "TIME", DIVIDER);
            //output body
            for (FeedingResponse f : feedings) {
                System.out.printf(
                        "%-5d %-20s %-15s%n",
                        f.getCreatureId(),
                        f.getCreatureName(),
                        f.getFeedingTime()
                );
            }

        } catch (Exception e) {
            System.out.println("API Error: " + e.getMessage());
        }
    }

    //route 8: GET /api/admin/users -- lists all users
    public static void viewAllUsers() {
        try {

            System.out.println("\nSYSTEM USERS\n" + DIVIDER);
            UserResponse[] users = apiClient.getAllUsers();

            //edge case: none found
            if (users.length == 0) {
                System.out.println(("No users found.\n"));
                return;
            }
            //output header
            System.out.printf("%-5s %-20s %-15s %-25s %-15s%n%s%n",
                    "ID", "NAME", "ROLE", "EMAIL", "PHONE", DIVIDER);
            //output body
            for (UserResponse u : users) {
                System.out.printf(
                        "%-5s %-20s %-15s %-25s %-15s%n",
                        u.getId(),
                        u.getFullName(),
                        u.getRole(),
                        u.getEmail(),
                        u.getPhone()
                );
            }
        } catch (Exception e) {
            System.out.println("API Error: " + e.getMessage());
        }
    }

    //helper methods to verify input
    public static boolean promptYesNo(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("y")) {
                return true;
            }
            if (input.equalsIgnoreCase("n")) {
                return false;
            }

            System.out.println("Invalid input. Enter y or n.");
        }
    }
    public static Long promptLong(String message) {
        while (true) {
            try {
                System.out.print(message);
                return Long.parseLong(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Try again.");
            }
        }
    }
    public static String promptString(String message) {
        while (true) {
            try {
                System.out.print(message);
                String input = scanner.nextLine().trim();
                if (!input.isBlank()) {
                    return input;
                }
            } catch (Exception e) {
                System.out.println("Invalid. Try again.");
            }
        }
    }
    public static String promptTime(String message) {
        while (true) {
                System.out.print(message);
                String input = scanner.nextLine().trim();

                if (input.matches("^([01]\\d|2[0-3]):([0-5]\\d)$")) {
                    return input;
                }

                System.out.println(
                        "Invalid time format. Use HH:MM."
                );
        } // end while
    }
}