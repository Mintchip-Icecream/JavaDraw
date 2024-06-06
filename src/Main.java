import javax.swing.*;
import java.util.*;

public class Main {
    public static int canvasWidth=1600;
    public static int canvasHeight = 900;
    public Main(){
        Scanner scanner = new Scanner(System.in); //
        System.out.println("Insert width: ");
        canvasWidth = scanner.nextInt();
        System.out.println("Insert height: ");
        canvasHeight = scanner.nextInt();


        Canvas canvas = new Canvas();

    }

    public static void main(String[] args) {
        new Main();


    }
}