import java.awt.*;
public class PenButton extends Button { //inherits
    private int r = 128;
    public Color colorOfPen = new Color(0,0,0);
    private int g = 128;
    private int b = 128;
    public static Color currentColor = new Color(0, 0, 0, 255);

    Canvas canvas;
    public PenButton(Canvas canvas, int inputx, int inputy, int inputsize, Color inputColor) {
        super(canvas, inputx, inputy, inputsize, inputColor);
        System.out.println("pen constructor");
    }
    public void draw() {
        System.out.println("pen draw");
        super.draw();
        g2.setColor(currentColor);
        g2.fillRect(getX()+2,getY()+2,getSize()-2,getSize()-2);
        g2.setColor(Color.white);
        g2.setFont(new Font("Arial", Font.BOLD,15));
        g2.drawString("Pen", getX()+30, 50);
    }
    public void activate() {
      super.activate();
        if (!(super.canvas.activebutton == "pen")) {
            System.out.println("pen activate");
            changeButtonColor(r, g, b);
            super.canvas.activebutton = "pen";
      
        }
        else {
            changeButtonColor(0, 0, 0);
        }
    }
    public void checkActive(){
        if (!(super.canvas.activebutton == "pen")){
            changeButtonColor(r,g,b);
        } else {
            changeButtonColor(0, 0, 0);
        }
    }
    public void deactivate(){
        System.out.println("pen deactivate");
        changeButtonColor(0,0,0);
    }
    //public void updateGraphics(Graphics2D panel, Color color) {
    //    panel.setColor(color);
    //}
    public void changeButtonColor ( int red, int green, int blue){
        currentColor = new Color(red, green, blue);
        draw();
    }
    public void changePenColor(int redFromAdjuster, int greenFromAdjuster, int blueFromAdjuster){
        colorOfPen = new Color(redFromAdjuster,greenFromAdjuster,blueFromAdjuster);
    }
}