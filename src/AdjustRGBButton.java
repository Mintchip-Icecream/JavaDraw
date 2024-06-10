import java.awt.Color;
import java.awt.GradientPaint;

public class AdjustRGBButton extends Button {
    private int value = 0; //When adjusting, it increases or decreases the values, and toolbar updates the rgb values to create the new currentcolor
    private GradientPaint gradientBg;
    private char channel = 'r';  // default is red channel

    public AdjustRGBButton(String label, Canvas canvas, int inputx, int inputy, int inputsize, Color inputColor, char channel) {
      super(label, canvas, inputx, inputy, inputsize, inputColor);
      this.channel = channel;  
      this.setGradient(0, 255);
    }

    @Override
    public void draw() {
      super.draw();
      if (this.gradientBg == null) return;

      this.g2.setPaint(this.gradientBg);
      this.g2.fillRect(this.getX() + 2, this.getY() + 2, this.getSize() - 2, this.getSize()- 2);
      this.drawLabel();
    }
    public void activate(){
        changeRGB(this.canvas.mouseX, this.canvas.mouseY);
    }


    public void changeRGB(int x, int y) {
        if (y < this.canvas.initialy && value >0) {
            value--;
        } else if (y > this.canvas.initialy && value <255) {
            value++;
        }
        this.setName(this.name.split(" ")[0] + " " + this.value);
        this.setGradient(value, 255);
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

    public void setGradient(int startVal, int endVal) {
      Color c1 = null, c2 = null;

      if ("rgb".indexOf(this.channel) < 0) return;

      switch (this.channel) {
        case 'r':
          c1 = new Color(startVal, 0, 0);
          c2 = new Color(endVal, 0, 0);
          break;
        case 'g':
          c1 = new Color(0, startVal, 0);
          c2 = new Color(0, endVal, 0);
          break;
        case 'b':
          c1 = new Color(0, 0, startVal);
          c2 = new Color(0, 0, endVal);
        break;
      }

      float x = (float) this.bounds.getX()/2;
      float y1 = (float) this.bounds.getY();
      float y2 = (float) this.getSize() + y1;

      // https://docs.oracle.com/javase/7/docs/api/java/awt/GradientPaint.html
      if (c1 != null || c2 != null) this.gradientBg = new GradientPaint(x, y1, c1, x, y2, c2);
      this.draw();
    }

    public int getValue(){
        return value;
    }


}
