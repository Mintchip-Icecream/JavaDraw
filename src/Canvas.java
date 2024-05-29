import java.awt.*;

public class Canvas {
    public static int width = Main.canvasWidth;
    public static int height = Main.canvasHeight;
    DrawingPanel panel = new DrawingPanel(1600, 1000);
    Graphics g = panel.getGraphics();
    private int mouseX;
    private int mouseY;

    public Canvas(){
        panel.setBackground(Color.darkGray);
        g.setColor(Color.WHITE);
        g.fillRect(0,100,width,height);

    }
    public void update(){
    }

}
