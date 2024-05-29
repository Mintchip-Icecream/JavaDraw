import javax.swing.*;
import java.awt.event.*;

public class CanvasStart implements ActionListener {
    //starts up asking for the canvas coords
    private static JFrame frame = new JFrame();
    private static JPanel panel = new JPanel();
    private static JLabel askX = new JLabel("Width:");
    private static JLabel askY = new JLabel("Height:");
    public static void startup()
    {

        frame.setSize(500,150);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setTitle("Startup");
        frame.add(panel);
        panel.setLayout(null);

        JButton startbutton = new JButton("Set Canvas Dimensions:");
        startbutton.setBounds(150,10,200, 30);
        startbutton.addActionListener(new CanvasStart());
        panel.add(startbutton);


        askX.setBounds(60,50,100, 25);
        panel.add(askX);
        askY.setBounds(260,50,100, 25);
        panel.add(askY);

        JTextField valueX = new JTextField();
        valueX.setBounds(100,50,100,25);
        JTextField valueY = new JTextField();
        valueY.setBounds(300,50,100,25);
        panel.add(valueX);
        panel.add(valueY);

        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //the inputwidth and inputheight is gonna be our ints for the canvas size
            String answertoaskX = askX.getText();
            String answertoaskY = askY.getText();

    }
}
