//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.print("\n~*~ Neon Ark's Monster Database by Jessa K. West ~*~\n\n");

        String m1_name = "Mossmuncher";
        String m1_type = "Planet/Earth";
        String m1_description = "A fuzzy, round creature covered in springy moss and tiny white flowers.";

        Monster m1 = new Monster(m1_name, m1_type, m1_description);
        System.out.println("New monster has been added:");
        System.out.println(m1);
    }
}