import java.awt.Color;
import java.awt.Graphics;

public class ColorButton extends Buttons { //inherits

    private int r = 0;
    private int g = 0;
    private int b = 0;
    public static Color CurrentColor = new Color(255, 0, 0); //this is for the pen
    Canvas canvas;
    //change color based on moving your mouse while holding after clicking



    public ColorButton(Canvas canvas, int inputx, int inputy, int inputsize, Color inputColor) {
        super(canvas, inputx, inputy, inputsize, inputColor);
    }

    public void draw() {
        super.draw();
        g2.setColor(CurrentColor);
        g2.fillRect(getX()+2,getY()+2,getSize()-2,getSize()-2);
        g2.setColor(Color.black);
        g2.drawString("Color", getX()+2, 50);
    }

    public void activate() {
        changeColor(r, g, b);

    }

    private void changeRGB(int x, int y) { //not using right now
        if (x < Canvas.initialx) {
            r++;
        } else if (x < Canvas.initialx) {
            b++;
        }
        if (y > canvas.initialy) {
            g--;
        } else if (y < canvas.initialy) {
            g++;
        }

    }
        public void changeColor ( int red, int green, int blue){
        CurrentColor = new Color(red, green, blue);
    }
}
