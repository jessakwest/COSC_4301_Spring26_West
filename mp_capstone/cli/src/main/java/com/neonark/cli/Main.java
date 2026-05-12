package com.neonark.cli;

import com.neonark.cli.dto.*;

import java.util.Scanner;

public class Main {
    //private global variables
    private static final Scanner scanner = new Scanner(System.in);
    private static final ApiClient apiClient = new ApiClient();

    public static void main(String[] args) {
        displayHeader();
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
                    break;
                case "8":
                    System.out.println("\nSelected: View all system users (Admin Only)");
                    break;
                case "0":
                    System.out.println("\nSelected: Exit");
                    running = false;
                    scanner.close();
                    break;
                default:
                    System.out.println("\nInvalid input. Try again.");
                    //pause();
            }
        }
    }

    //helper methods
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
                return scanner.nextLine();
            } catch (Exception e) {
                System.out.println("Invalid. Try again.");
            }
        }
    }

    //route 1.a and 1.b: GET /api/creatures -- all creatures, including removed or only active
    public static void listAllCreatures() {
        try {
            boolean includeRemoved = promptYesNo("\nInclude removed creatures? (y/n): ");

            if (includeRemoved) {
                System.out.println(("\nCREATURE REGISTRY: all, including removed"));
            } else {
                System.out.println(("\nCREATURE REGISTRY: active only"));
            }

            String divider = "------------------------------------------------";
            CreatureResponse[] creatures = apiClient.getAllCreatures(includeRemoved);

            System.out.println(divider);

            if (creatures.length == 0) {
                System.out.println(("No creatures found\n"));
                return;
            }

            System.out.printf("%-5s %-10s %-15s %-25s%n%s%n",
                    "ID", "NAME", "STATUS", "HABITAT", divider);

            for (CreatureResponse c : creatures) {
                System.out.printf(
                        "%-5d %-10s %-15s %-25s%n",
                        c.getId(),
                        c.getName(),
                        c.getStatus(),
                        c.getHabitatName()
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
            String divider = "------------------------------------------------";
            System.out.println("\nCREATURE " + id + "'s DETAILS");
            System.out.println(divider);
            System.out.printf("%-15s %s%n", "ID:", creature.getId());
            System.out.printf("%-15s %s%n", "NAME:", creature.getName());
            System.out.printf("%-15s %s%n", "STATUS:", creature.getStatus());
            System.out.printf("%-15s %s%n", "HABITAT:", creature.getHabitatName());
            System.out.printf("%-15s %s%n", "REMOVED:", creature.isRemoved() ? "Yes" : "No");
            System.out.println(divider);

        } catch (Exception e) {
            System.out.println("API Error: " + e.getMessage());
        }
    }

    //route 3: POST /api/creatures
    public static void registerCreature() {
        try {
            System.out.println("\nREGISTER NEW CREATURE");
            String divider = "------------------------------------------------";
            // gather info
            String name = promptString("Name: ");
            String status = promptString("Status: ");
            Long habitatId = promptLong("Habitat ID: ");

            //create creature
            CreateCreatureRequest request = new CreateCreatureRequest(name, status, habitatId);
            CreatureResponse creature = apiClient.createCreature(request);

            System.out.println(divider);
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
            String divider = "------------------------------------------------";
            // gather info
            Long creatureId = promptLong("Creature ID: ");
            String name = promptString("New name: ");
            boolean confirmed = promptYesNo("\nConfirm name change to: " + name + " ? (y/n): ");

            if (!confirmed) {
                System.out.println(("\nCreature rename cancelled.\n" + divider));
                return;
            }

            //rename creature
            RenameCreatureRequest request = new RenameCreatureRequest(name);
            CreatureResponse creature = apiClient.renameCreature(creatureId, request);

            System.out.println(divider);
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
            String divider = "------------------------------------------------";
            Long id = promptLong("Creature ID: ");
            boolean confirmed = promptYesNo("Confirm removal? (y/n): ");

            if (!confirmed) {
                System.out.println(("\nCreature removal cancelled.\n" + divider));
                return;
            }

            apiClient.deleteCreature(id);
            System.out.println(("\nCreature removal successful.\n" + divider));


        } catch (Exception e) {
            System.out.println("API Error: " + e.getMessage());
        }
    }

    //route 6: GET /api/creatures/{id}/observations
    public static void viewCreatureObservations() {
        try {
            String divider = "------------------------------------------------";
            Long id = promptLong("Creature ID: ");
            CreatureObservationsResponse response = apiClient.getCreatureObservations(id);
            System.out.println("\nCREATURE " + id + "'s OBSERVATIONS");
            System.out.println(divider);

            if (response.getObservations().isEmpty()) {
                System.out.println(("No observations found\n"));
                return;
            }

            System.out.printf("%-5s %-20s %-30s%n%s%n",
                    "ID", "AUTHOR", "NOTE", divider);

            for (ObservationResponse o : response.getObservations()) {
                System.out.printf(
                        "%-5d %-20s %-30s%n",
                        o.getId(),
                        o.getAuthor(),
                        o.getNote()
                );
            } // end enhanced for
        }// end try block
        catch (Exception e) {
            System.out.println("API Error: " + e.getMessage());
        } // end catch block
    }
}


    //route 7: GET /api/feedings?time={HH:MM} -- feeding schedules by id
    //route 8: GET /api/admin/users -- lists all users