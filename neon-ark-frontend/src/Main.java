//Main.java
import service.WardenService;
import repository.WardenRepository;
import ui.ConsoleUI;

public class Main {
    public static void main(String[] args) {
        String filePath = "data/wardens.csv";
        WardenRepository repository = new WardenRepository(filePath);
        WardenService service = new WardenService(repository);
        ConsoleUI ui = new ConsoleUI(service);
        displayHeader();
        ui.start();
    }

    public static void displayHeader(){
        System.out.println();
        for(int i=0; i < 5; i++) { System.out.print("~*"); }
        System.out.print(" NEON ARK: ADMIN WARDEN ONBOARDING CONSOLE ");
        for(int i=0; i < 5; i++) { System.out.print("*~");}
        System.out.println();
    }
}