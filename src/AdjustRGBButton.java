import java.awt.Color;

public class AdjustRGBButton extends Button {
    private int value = 0; //When adjusting, it increases or decreases the values, and toolbar updates the rgb values to create the new currentcolor
    public AdjustRGBButton(String label, Canvas canvas, int inputx, int inputy, int inputsize, Color inputColor) {
        super(label, canvas, inputx, inputy, inputsize, inputColor);
    }
    public void activate(){
        changeRGB(this.canvas.mouseX, this.canvas.mouseY);
    }


    public void changeRGB(int x, int y) {

      //System.out.println(this.canvas.initialy + " " + y);
     
        if (y < this.canvas.initialy && value >0) {
            value--;

        } else if (y > this.canvas.initialy && value <255) {
            value++;
        }
        System.out.println(this.value);
        this.setName(this.name.split(" ")[0] + " " + this.value);
        System.out.println(this.name.split(" ")[0] + " " + this.value);
        this.draw();
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
