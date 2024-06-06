import javax.swing.text.*;
import java.awt.*;

public class AdjustRGButton extends Buttons {
    private int Value= 0; //When adjusting, it increases or decreases the values, and toolbar updates the rgb values to create the new currentcolor
    public AdjustRGButton(Canvas canvas, int inputx, int inputy, int inputsize, Color inputColor) {
        super(canvas, inputx, inputy, inputsize, inputColor);
    }
    public void activate(){
        changeRGB(Canvas.mouseX, Canvas.mouseY);
    }

    public void draw(String text){
        super.draw();
        g2.setColor(Color.black);
        g2.drawString(text, getX()+25, 50);
    }
    public void changeRGB(int x, int y) {
        if (y > canvas.initialy && Value >0) {
            Value--;

        } else if (y < canvas.initialy && Value <255) {
            Value++;
        }
    }

    public int getValue(){
        return Value;
    }


}
