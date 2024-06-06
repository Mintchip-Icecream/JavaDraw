import java.awt.*;
public class Eraser extends Buttons { //inherits
    private int r = 128;
    private int g = 128;
    private int b = 128;
    public static Color currentColor = new Color(255, 255, 255, 255);
    Canvas canvas;
    public Eraser(Canvas canvas, int inputx, int inputy, int inputsize, Color inputColor) {
        super(canvas, inputx, inputy, inputsize, inputColor);
    }
    public void draw() {
        super.draw();
        g2.setColor(currentColor);
        g2.fillRect(getX()+2,getY()+2,getSize()-2,getSize()-2);
        g2.setColor(Color.black);
        g2.setFont(new Font("Arial", Font.BOLD,15));
        g2.drawString("Eraser", getX()+30, 50);
    }
    public void activate() {
        if (!(Canvas.activebutton == "eraser")){
        changeButtonColor(r,g,b);
        Canvas.activebutton="eraser";
        } else {
            changeButtonColor(255, 255, 255);
        }
    }
    public void checkActive(){
        if (!(Canvas.activebutton == "eraser")){
            changeButtonColor(r,g,b);
        } else {
            changeButtonColor(255, 255, 255);
        }
    }
    public void updateGraphics(Graphics2D panel) {
        panel.setColor(Color.WHITE);
    }
    public void changeButtonColor ( int red, int green, int blue){
        currentColor = new Color(red, green, blue);
        draw();
    }
}
