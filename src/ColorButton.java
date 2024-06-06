import java.awt.Color;

public class ColorButton extends Button {
  public static Color CurrentColor = new Color(0, 0, 0); // this is for the pen
  Canvas canvas;
  ToolBar tb;
  // change color based on moving your mouse while holding after clicking

  public ColorButton(Canvas canvas, int inputx, int inputy, int inputsize, Color inputColor) {
    super(canvas, inputx, inputy, inputsize, inputColor);

  }

  @Override
  public void draw() {
    super.draw();
    g2.setColor(Color.white);
    g2.drawString("Color", getX() + 25, getY() + 25);

  }

  public void changeColor(Color color) {
    g2.setColor(color);
    g2.fillRect(getX() + 2, getY() + 2, getSize() - 2, getSize() - 2);
    g2.setColor(Color.white);
    g2.drawString("Color", getX() + 25, 25);

  }
}
