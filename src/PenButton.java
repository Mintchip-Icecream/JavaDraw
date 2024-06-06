import java.awt.*;
public class PenButton extends Button { //inherits
    //private int r = 128;
    //public Color colorOfPen = new Color(0,0,0);
    //private int g = 128;
    //private int b = 128;
    //public static Color currentColor = new Color(0, 0, 0, 255);

    Canvas canvas;
    public PenButton(String label, Canvas canvas, int inputx, int inputy, int inputsize, Color inputColor) {
        super("Pen", canvas, inputx, inputy, inputsize, inputColor);
        this.labelColor = Color.WHITE;
    }
    public void draw() {
        super.draw();
        //g2.setColor(color);
        //g2.fillRect(getX()+2,getY()+2,getSize()-2,getSize()-2);
        //g2.setColor(Color.white);
        //g2.setFont(new Font("Arial", Font.BOLD,15));
        //g2.drawString("Pen", getX()+30, 50);
    }
    
    
    //public void updateGraphics(Graphics2D panel, Color color) {
    //    panel.setColor(color);
    //}
    public void changeButtonColor_ ( int red, int green, int blue){
        //currentColor = new Color(red, green, blue);
        //draw();
    }
    public void changfePenColor_(int redFromAdjuster, int greenFromAdjuster, int blueFromAdjuster){
        //colorOfPen = new Color(redFromAdjuster,greenFromAdjuster,blueFromAdjuster);
    }
}