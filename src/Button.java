import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Button {
  private int x;
  private int y;
  private int size;
  protected String name = "";
  protected Color currentBgColor, activeBgColor, currentLabelColor,
                  inactiveBgColor = new Color(115, 115, 115), 
                  activeLabelColor = Color.WHITE,
                  inactiveLabelColor = Color.BLACK;

  public Rectangle bounds;
  Canvas canvas;
  Graphics2D g2;
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
    this.activeBgColor = inputColor;
    this.currentBgColor = this.inactiveBgColor;
    this.currentLabelColor = this.inactiveLabelColor;
    this.bounds = new Rectangle(x, y, size, size);
  }

  public void draw() {
    g2.setColor(Color.black);  
    g2.drawRect(x, y, size, size);  // border
    g2.setColor(this.currentBgColor);
    g2.fillRect(x + 2, y + 2, size - 2, size - 2);
    this.drawLabel();
  }

  public void drawLabel() {
    g2.setColor(this.currentLabelColor);
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
  }

  public void update() {
    if (isPressed()) {
      activate();
    }
  }

  public void changeButtonColor (Color color){
    this.currentBgColor = color;
    draw();
}

  public void activate() {
    if (!this.canvas.activebutton.equals(this.name)) {
      this.currentLabelColor = this.activeLabelColor;
      changeButtonColor(activeBgColor);
    
      this.canvas.activebutton = this.name;
    }
    else {
      this.deactivate();
    }
  }

  public void checkActive(){ 
    if (!this.canvas.activebutton.equals(this.name)) {
        changeButtonColor(activeBgColor);
    } else {
        changeButtonColor(inactiveBgColor);
    }
  }

  public void deactivate(){
    this.currentLabelColor = this.inactiveLabelColor;
    changeButtonColor(inactiveBgColor);
  }

  public void checkHover(int x, int y) {
    if (this.canvas.activebutton.equals(this.name)) return;

    if (this.bounds.contains(x, y)) {
      this.currentBgColor = this.activeBgColor;
      this.currentLabelColor = this.activeLabelColor;
    } else {
      this.currentBgColor = this.inactiveBgColor;
      this.currentLabelColor = this.inactiveLabelColor;
    }

    this.draw();
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public Color getActiveColor() {
    return activeBgColor;
  }

  public int getSize() {
    return size;
  }

  public void setName(String name) {
    this.name = name;
  }
}
