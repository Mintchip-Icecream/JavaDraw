import java.awt.*;

public class ToolBar {
  Canvas canvas;
  Graphics2D g2;
  ColorButton colorbutton;
  AdjustRGBButton rButton, bButton, gButton;
  AdjustValButton addPenSizeButton, subtractPenSizeButton, 
                  penSizeButton, addLayerButton, subtractLayerButton, countingLayerButton;
  Button moveButton, eraserButton;
  PenButton pen;
  Color currentColor = new Color(0, 0, 0);
  public int penSize = 5;
  public int LayerCount;
  Rectangle bounds;
  int padding = 5; // toolbar padding around buttons/controls  
  int size = 100;  // button/control size
  Color red = new Color(255, 122, 122);
  Color green = new Color(122, 255, 122);
  Color blue = new Color(122, 122, 255);
  Color mediumGray = new Color(150, 150, 150);
  Color darkerMediumGray = new Color(115, 115, 115);

  public ToolBar(Canvas canvas, int width, int height) {
    this.canvas = canvas;
    this.g2 = canvas.g2;
    this.bounds = new Rectangle(0, 0, width, height + (this.padding * 2));

    int h = (int) this.bounds.getHeight();
    this.g2.setColor(Color.BLACK);
    this.g2.drawLine(0, h - 1, width, h - 1);
    this.g2.setColor(Color.WHITE);
    this.g2.drawLine(0, h, width, h);

    
    this.moveButton = new Button("Move", canvas, padding, padding, size, mediumGray);
    moveButton.draw();

    this.pen = new PenButton("Pen", canvas, (padding* 2) + (size * 1), padding, size, mediumGray);
    this.penSizeButton = new AdjustValButton("Size: " + penSize, canvas, (padding* 3)  + (size * 2), padding, size, darkerMediumGray);
    this.penSizeButton.currentLabelColor = Color.LIGHT_GRAY;
    this.addPenSizeButton = new AdjustValButton("+", canvas, (padding* 3) + (size * 3), padding, size/2, mediumGray);
    this.subtractPenSizeButton = new AdjustValButton("-", canvas, (padding* 3) + (size * 3), (size/2) + padding, size/2, mediumGray);
    this.pen.draw();
    this.penSizeButton.draw();
    this.subtractPenSizeButton.draw();
    this.addPenSizeButton.draw();
    this.addPenSizeButton.setValue(this.penSize);
    
    // note to team: I ended up moving a lot duplcated functionality between the button sublcasses 
    // to the parent Button class. after doing so, the EraserButton class didn't have any added functionality 
    // than the Button class, so I changed eraserButton to type Button -Jason
    this.eraserButton = new Button("Eraser", canvas, (padding* 4) + (size * 4) - size/2, padding, size, mediumGray);
    this.eraserButton.draw();

    this.colorbutton = new ColorButton("Color", canvas, (padding* 5) + (size * 5) - size/2, padding, size, Color.black);
    this.colorbutton.currentBgColor = Color.BLACK;
    this.colorbutton.currentLabelColor = Color.WHITE;
    this.rButton = new AdjustRGBButton("R: 0", canvas, (padding* 5)+ (size * 6) - size/2, padding, size, red, 'r');
    //this.rButton.activeLabelColor = Color.WHITE;
    this.rButton.currentLabelColor = Color.WHITE;
    this.gButton = new AdjustRGBButton("G: 0", canvas, (padding* 5) + (size * 7) - size/2, padding, size, green, 'g');
    //this.gButton.activeLabelColor = Color.WHITE;
    this.gButton.currentLabelColor = Color.WHITE;
    this.bButton = new AdjustRGBButton("B: 0", canvas, (padding* 5) + (size * 8) - size/2, padding, size, blue, 'b');
    //this.bButton.activeLabelColor = Color.WHITE;
    this.bButton.currentLabelColor = Color.WHITE;
    this.colorbutton.draw();
    this.rButton.draw();
    this.gButton.draw();
    this.bButton.draw();
    
   

    this.countingLayerButton = new AdjustValButton("Layer: 1", canvas, (padding* 6) + (size * 9) - size/2, padding, size, darkerMediumGray); // displays which layer we're on
    this.addLayerButton = new AdjustValButton("+", canvas, (padding* 6)+ (size * 10) - size/2, padding, size/2, mediumGray);
    this.subtractLayerButton = new AdjustValButton("-", canvas, (padding* 6) + (size * 10) - size/2, (size/2) + padding, size/2, mediumGray);
    this.addLayerButton.setValue(1);
    this.addLayerButton.draw();
    this.subtractLayerButton.draw();
    this.countingLayerButton.draw();
  }
  // toolbar gets updated, which looks at the boolean if the mouse is on the
  // taskbar, if it's on the taskbar it'll update all of the buttons (this is to
  // reduce the amount of memory it consumes per second)

  public void update(boolean mousePressed) {
    if (mousePressed) {
      this.canvas.lh.selectedLayer.hideBoundary();  // make sure isn't showing when any other button is pressed
      this.canvas.lh.refreshCanvas();
      this.deactivateAll();
      this.determineButtonPress();

      if (this.addPenSizeButton.isPressed()) {
        this.refreshPenSize();
        addPenSizeButton.changeButtonColor(addPenSizeButton.inactiveBgColor);
      } 
      else if (this.addLayerButton.isPressed()) {
        this.refreshLayerCount();
        addLayerButton.changeButtonColor(addLayerButton.inactiveBgColor);
        this.canvas.lh.selectNextLayer();
      } 
      else if (this.subtractPenSizeButton.isPressed()) {
        this.refreshPenSize();
        subtractPenSizeButton.changeButtonColor(subtractPenSizeButton.inactiveBgColor);
      } 
      else if (this.subtractLayerButton.isPressed()) {
        this.refreshLayerCount();
        subtractLayerButton.changeButtonColor(subtractLayerButton.inactiveBgColor);
        this.canvas.lh.selectPrevLayer();
      } else {
        
        switch (this.canvas.activebutton) {
          case "Move":
            this.canvas.lh.selectedLayer.drawBoundary();
            this.canvas.lh.drawSelectedLayer();
            break;

          case "Pen":
          case "Eraser":
            this.canvas.lh.selectedLayer.hideBoundary();
            this.canvas.lh.refreshCanvas();
            break;
        }
      }
    } else {  // mousePressed == false
      addPenSizeButton.changeButtonColor(addPenSizeButton.activeBgColor);
      addLayerButton.changeButtonColor(addLayerButton.activeBgColor);
      subtractPenSizeButton.changeButtonColor(subtractPenSizeButton.activeBgColor);
      subtractLayerButton.changeButtonColor(subtractLayerButton.activeBgColor);
    }
  }

  public void dragUpdate() {
    if (this.canvas.mousePressed) {
      rButton.update();
      gButton.update();
      bButton.update();
      change();
    }
  }

  public void change() {
    this.currentColor = new Color(rButton.getValue(), gButton.getValue(), bButton.getValue());  
    colorbutton.changeColor(currentColor);
  }

  
  private void refreshPenSize() {
    this.penSize = addPenSizeButton.getValue() - subtractPenSizeButton.getValue();
        if (this.penSize < 5) {
          this.penSize = 5;
        }

      penSizeButton.setName("Size: " + this.penSize);
      penSizeButton.draw();
  }

  private void refreshLayerCount() {
    this.LayerCount = addLayerButton.getValue() - subtractLayerButton.getValue();

    if (LayerCount <= 0) {
      LayerCount = 1;
      subtractLayerButton.setValue(0);
    }
    countingLayerButton.setName("Layer: " + LayerCount);
    countingLayerButton.draw();
  }

  private void deactivateAll() {
    this.moveButton.deactivate();
    this.pen.deactivate();
    this.eraserButton.deactivate();
  }

  private void determineButtonPress() {
    // all buttons have their own "update" method which checks if mouse position
    // is within the button's coordinates, and if it is, activates the button
    
    moveButton.update();
    pen.update();
    eraserButton.update();
    addLayerButton.update();
    subtractLayerButton.update();
    addPenSizeButton.update();
    subtractPenSizeButton.update();
    
  }

  public void determineButtonHover(int x, int y) {
    moveButton.checkHover(x, y);
    pen.checkHover(x, y);
    eraserButton.checkHover(x, y);
    addLayerButton.checkHover(x, y);
    subtractLayerButton.checkHover(x, y);
    addPenSizeButton.checkHover(x, y);
    subtractPenSizeButton.checkHover(x, y);
  }

}
