import java.awt.*;
public class PenButton extends Button { //inherits
    Canvas canvas;
    public PenButton(String label, Canvas canvas, int inputx, int inputy, int inputsize, Color inputColor) {
        super("Pen", canvas, inputx, inputy, inputsize, inputColor);
        //this.labelColor = Color.WHITE;
    }
   
}