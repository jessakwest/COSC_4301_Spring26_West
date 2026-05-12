package com.neonark.cli;

import com.neonark.cli.dto.CreatureResponse;
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
    public static void displayHeader(){
        System.out.println();
        for(int i=0; i < 27; i++) { System.out.print("="); }
        System.out.print("\n\tNEON ARK CLI SYSTEM\n");
        for(int i=0; i < 27; i++) { System.out.print("=");}
        System.out.println();
    }
    public static void displayMainMenu(){
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
    public static void menuSelection(){
        boolean running = true;

        while(running) {
            displayMainMenu();

            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    System.out.println("\nSelected: List all creatures");
                    listAllCreatures();
                    break;
                case "2":
                    System.out.println("\nSelected: View creature by ID");
                    break;
                case "3":
                    System.out.println("\nSelected: Register new creature");
                    break;
                case "4":
                    System.out.println("\nSelected: Rename creature");
                    break;
                case "5":
                    System.out.println("\nSelected: Remove creature");
                    break;
                case "6":
                    System.out.println("\nSelected: View creature observations/notes");
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

            if (input.equalsIgnoreCase("y")) { return true; }
            if (input.equalsIgnoreCase("n")) { return false; }

            System.out.println("Invalid input. Enter y or n.");
        }
    }

    //route 1.a and 1.b: GET /api/creatures -- all creatures, including removed or only active
    public static void listAllCreatures(){
        try {
            boolean includeRemoved = promptYesNo("\nInclude removed creatures? (y/n): ") ;

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
}