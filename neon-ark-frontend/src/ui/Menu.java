package ui;

public class Menu {
    public void displayMainMenu(){
            System.out.println("\nMAIN MENU");
        for(int i=0; i < 15; i++) { System.out.print("• "); }
        System.out.print("""
                
                1. Add New Warden
                2. View All Wardens
                3. Update Warden
                4. Manage Certifications
                5. Terminate Warden
                0. Exit Program
                """);
        for(int i=0; i < 15; i++) { System.out.print("• "); }
        System.out.print("\nEnter Selection: ");
    }

    public void displayUpdateMenu(){
        System.out.println("\nMain > Update Menu");
        for(int i=0; i < 20; i++) { System.out.print("• "); }
        System.out.print("""
                
                1. Update Role
                2. Update Status
                0. Return to Main Menu
                """);
        for(int i=0; i < 20; i++) { System.out.print("• "); }
        System.out.print("\nEnter Selection: ");
    }

    public void displayCertificationMenu(){
        System.out.println("\nMain > Certification Menu");
        for(int i=0; i < 20; i++) { System.out.print("• "); }
        System.out.print("""
                
                1. Add Certification
                2. View All Certifications
                0. Return to Main Menu
                """);
        for(int i=0; i < 20; i++) { System.out.print("• "); }
        System.out.print("\nEnter Selection: ");
    }
}