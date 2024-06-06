import java.awt.*;

public class AdjustValButton extends Buttons {
    private int Value= 0; //When adjusting, it increases or decreases the values, and toolbar updates the rgb values to create the new currentcolor
    public AdjustValButton(Canvas canvas, int inputx, int inputy, int inputsize, Color inputColor) {
        super(canvas, inputx, inputy, inputsize, inputColor);
    }
    public void activate(){
        changeRGB(Canvas.mouseX, Canvas.mouseY);
    }


    public void changeRGB(int x, int y) {
        if (y > canvas.initialy && Value >0) {
            Value--;

        } else if (y < canvas.initialy && Value <255) {
            Value++;
        }
    }

    public void addValue(){ // adds value when clicked, used for regular updates rather than DragUpdates
        if (isPressed()){
            Value++;
        }
    }

    public void setValue(int inputVal){
        Value = inputVal;
    }

    public int getValue(){
        return Value;
    }


}
