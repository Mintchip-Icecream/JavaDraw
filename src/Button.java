import java.awt.*;

public class Button {
  private int x;
  private int y;
  private int size;
  private Color color;
  public Rectangle bounds;
  Canvas canvas;
  Graphics g2;
  // for Mr. Forkner, the code for this specific button references this video
  // (this class is the only thing borrowed from outside sources as far as I
  // know): https://youtu.be/MHhFTqAHiOA?si=c74su5JJdWTdQWH2

  public Button(Canvas canvas, int inputx, int inputy, int inputsize, Color inputColor) {
    this.canvas = canvas;
    this.g2 = canvas.g2;
    x = inputx;
    y = inputy;
    size = inputsize;
    color = inputColor;
    this.bounds = new Rectangle(x, y, size, size);
  }

  public void draw() {
    g2.setColor(Color.black);
    g2.drawRect(x, y, size, size);
    g2.setColor(color);
    g2.fillRect(x + 2, y + 2, size - 2, size - 2);
  }

  public void draw(String text, int yValue, Color color2) {
    draw();
    g2.setColor(color2);
    g2.drawString(text, getX() + 25, yValue);
  }

  public boolean isPressed() {
    return this.canvas.mousePressed && this.canvas.mouseX >= x && this.canvas.mouseX <= x + size && this.canvas.mouseY >= y
        && this.canvas.mouseY <= y + size;
  }

  public void update() {
    if (isPressed()) {
      activate();
    }
  }

  public void activate() {
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public Color getColor() {
    return color;
  }

  public int getSize() {
    return size;
  }
}
