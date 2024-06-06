import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Button {
  private int x;
  private int y;
  private int size;
  protected String name = "";
  protected Color color, 
                  inactiveColor = Color.DARK_GRAY, 
                  labelColor = Color.BLACK;
  public Rectangle bounds;
  Canvas canvas;
  Graphics g2;
  // for Mr. Forkner, the code for this specific button references this video
  // (this class is the only thing borrowed from outside sources as far as I
  // know): https://youtu.be/MHhFTqAHiOA?si=c74su5JJdWTdQWH2

  public Button(String label, Canvas canvas, int inputx, int inputy, int inputsize, Color inputColor) {
    this.canvas = canvas;
    this.g2 = canvas.g2;
    this.name = label;
    x = inputx;
    y = inputy;
    size = inputsize;
    color = inputColor;
    this.bounds = new Rectangle(x, y, size, size);
  }

  public void draw() {
    g2.setColor(Color.black);  
    g2.drawRect(x, y, size, size);  // border
    g2.setColor(color);
    g2.fillRect(x + 2, y + 2, size - 2, size - 2);
    this.drawLabel();
  }

  public void drawLabel() {
    g2.setColor(labelColor);
    g2.setFont(new Font("Arial", Font.BOLD,15));

    // https://stackoverflow.com/questions/258486/calculate-the-display-width-of-a-string-in-java
    Rectangle2D stringSize = this.g2.getFontMetrics().getStringBounds(name, g2);
    
    int textWidth = (int) stringSize.getWidth();
    //int textHeight = (int) stringSize.getHeight();
    int xPos = this.getX() + (this.getSize() - textWidth)/2;
    int yPos = this.getY() + this.getSize()/2 + 4;

    g2.drawString(this.name, xPos, yPos); // centers label
  }


  public boolean isPressed() {
    return this.canvas.mousePressed && this.bounds.contains(this.canvas.mouseX, this.canvas.mouseY);
    //return this.canvas.mousePressed && this.canvas.mouseX >= x && this.canvas.mouseX <= x + size && this.canvas.mouseY >= y
    //    && this.canvas.mouseY <= y + size;
  }

  public void update() {
    if (isPressed()) {
      activate();
    }
  }

  public void changeButtonColor (Color color){
    this.color = color;
    draw();
}

  public void activate() {
    if (!(this.canvas.activebutton == this.name)) {
      System.out.println(this.name + " activated");
      changeButtonColor(color);
      this.canvas.activebutton = this.name;
    }
    else {
      this.deactivate();
    }
  }

  public void checkActive(){ 
    if (!(this.canvas.activebutton == this.name)){
        changeButtonColor(color);
    } else {
        changeButtonColor(inactiveColor);
    }
  }

  public void deactivate(){
    System.out.println(this.name + " deactivate");
    changeButtonColor(inactiveColor);
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

  public void setName(String name) {
    this.name = name;
  }
}
