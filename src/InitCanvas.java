import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.util.*;


public class InitCanvas {

    private static int width = 1600;
    private static int height = 900;
    
    public InitCanvas(){
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

            this.width = Integer.parseInt(heightInput);
            this.height = Integer.parseInt(widthInput);
        }

    }
    
    public static int getHeight(){
      return width; 
    }
    
    public static int getWidth(){
      return height;
    }
    
}