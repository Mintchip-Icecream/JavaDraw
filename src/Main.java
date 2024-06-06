import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Main {
  final static Color DEFAULT_PANEL_BG = Color.darkGray;
  final static int PANEL_WIDTH = 1600;
  final static int PANEL_HEIGHT = 900;

  private static int canvasWidth = 800;  // defaults
  private static int canvasHeight = 800;


  private static void requestCanvasSize(){
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
  }

  public static void main(String[] args) {
      //requestCanvasSize();

      DrawingPanel dp = new DrawingPanel(PANEL_WIDTH, PANEL_HEIGHT);
      Canvas canvas = new Canvas(canvasWidth, canvasHeight, dp);

      dp.setBackground(DEFAULT_PANEL_BG);
    

      

  }
}