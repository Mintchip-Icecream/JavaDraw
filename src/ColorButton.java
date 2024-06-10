import java.awt.Color;

public class ColorButton extends Button {
  // change color based on moving your mouse while holding after clicking

  public ColorButton(String label, Canvas canvas, int inputx, int inputy, int inputsize, Color inputColor) {
    super("Color", canvas, inputx, inputy, inputsize, inputColor);
    this.activeLabelColor = Color.WHITE;
  }


  public void changeColor(Color color) {
    this.currentBgColor = color;
    this.draw();
 

  }
}
