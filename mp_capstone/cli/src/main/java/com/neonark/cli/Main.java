package com.neonark.cli;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        menuSelection();
        System.out.println("\n\nend of program.");
    }
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
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while(running) {
            displayHeader();
            displayMainMenu();

            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    System.out.println("\nSelected: List all creatures");
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
                    break;
                default:
                    System.out.println("\nInvalid input. Try again.");
            }
        }
    }
}
