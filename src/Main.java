import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.util.*;


public class Main {

    public static int canvasWidth = 1600;
    public static int canvasHeight = 900;
    
    public Main(){
        String[] messages = {
            "Enter the height of the Canvas:",
            "Enter the width of the Canvas:"
        };

        JPanel inputPanel = new JPanel(new GridLayout(messages.length, 2, 5, 5));

        JTextField[] inputFields = new JTextField[messages.length];

        for (int i = 0; i < messages.length; i++) {
            JLabel label = new JLabel(messages[i]);
            inputFields[i] = new JTextField();
            inputPanel.add(label);
            inputPanel.add(inputFields[i]);
        }

        int result = JOptionPane.showOptionDialog(null, inputPanel, "Enter Canvas Dimensions",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);

        if (result == JOptionPane.OK_OPTION) {
            String heightInput = inputFields[0].getText();
            String widthInput = inputFields[1].getText();

            canvasWidth = Integer.parseInt(heightInput);
            canvasHeight = Integer.parseInt(widthInput);
        }
        
        
        //Scanner scanner = new Scanner(System.in);
        //System.out.println("Insert width: ");
        //canvasWidth = scanner.nextInt();
        //System.out.println("Insert height: ");
        //canvasHeight = scanner.nextInt();

        Canvas canvas = new Canvas();

    }
 
    public static void main(String[] args) {
        new Main();


    }
}