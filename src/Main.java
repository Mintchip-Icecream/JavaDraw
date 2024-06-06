import javax.swing.*;
import java.util.*;

public class Main {
    public static int canvasWidth=1600;
    public static int canvasHeight = 900;
    public Main(){
        InitCanvas init = new InitCanvas();
        canvasWidth = InitCanvas.getWidth();
        canvasHeight = InitCanvas.getHeight();

        Canvas canvas = new Canvas();

    }

    public static void main(String[] args) {
        new Main();


    }
}