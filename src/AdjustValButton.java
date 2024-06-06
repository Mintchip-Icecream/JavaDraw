import java.awt.Color;

public class AdjustValButton extends Button {
    private int value= 0; //When adjusting, it increases or decreases the values, and toolbar updates the rgb values to create the new currentcolor
    public AdjustValButton(Canvas canvas, int inputx, int inputy, int inputsize, Color inputColor) {
        super(canvas, inputx, inputy, inputsize, inputColor);
    }
    public void activate(){
        //changeRGB(super.canvas.mouseX, super.canvas.mouseY);
    }


    public void changeRGB(int x, int y) {
        if (y > super.canvas.initialy && value >0) {
            value--;

        } else if (y < super.canvas.initialy && value <255) {
            value++;
        }
    }

    public void addValue(){ // adds value when clicked, used for regular updates rather than DragUpdates
        if (isPressed()){
            value++;
        }
    }

    public void setValue(int inputVal){
        value = inputVal;
    }

    public int getValue(){
        return value;
    }


}
