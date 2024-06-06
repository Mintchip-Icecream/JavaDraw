import java.awt.*;

public class ToolBar {
  public enum toolBarType {
    pen, type, eraser, layers, select, line, colorpicker
  }

  Canvas canvas;
  Graphics2D g2;
  toolBarType tBA;
  ColorButton colorbutton;
  AdjustRGBButton rButton, bButton, gButton;
  AdjustValButton addPenSizeButton, subtractPenSizeButton, 
                  penSizeButton, addLayerButton, subtractLayerButton, countingLayerButton;
  Button moveButton;
  EraserButton eraser;
  PenButton pen;
  Color currentColor = new Color(0, 0, 0);
  public int penSize = 5;
  public int LayerCount;
  Color red = new Color(255, 122, 122);
  Color green = new Color(122, 255, 122);
  Color blue = new Color(122, 122, 255);

  public ToolBar(Canvas canvas) {
    int startX = 5; // starting x position to start drawing them
    int size = 100; // size of buttons
    this.canvas = canvas;
    this.g2 = canvas.g2;

    this.moveButton = new Button("Move", canvas, startX, 0, size, Color.WHITE);
    
    moveButton.draw();

    this.pen = new PenButton("Pen", canvas, startX + (size * 1), 0, size, Color.black);
    this.penSizeButton = new AdjustValButton("Size: " + penSize, canvas, startX + (size * 2), 0, size, Color.white);
    this.addPenSizeButton = new AdjustValButton("+", canvas, startX + (size * 3), 0, size/2, Color.white);
    this.subtractPenSizeButton = new AdjustValButton("-", canvas, startX + (size * 3), size/2, size/2, Color.white);
    this.pen.draw();
    this.penSizeButton.draw();
    this.subtractPenSizeButton.draw();
    this.addPenSizeButton.draw();
    this.addPenSizeButton.setValue(this.penSize);
    
    this.eraser = new EraserButton("Erase", canvas, startX + (size * 4), 0, size, Color.WHITE);
    this.eraser.draw();

    this.colorbutton = new ColorButton("Color", canvas, startX + (size * 5), 0, size, Color.black);
    this.rButton = new AdjustRGBButton("R: 0", canvas, startX + (size * 6), 0, size, red);
    this.rButton.labelColor = Color.WHITE;
    this.gButton = new AdjustRGBButton("G: 0", canvas, startX + (size * 7), 0, size, green);
    this.gButton.labelColor = Color.WHITE;
    this.bButton = new AdjustRGBButton("B: 0", canvas, startX + (size * 8), 0, size, blue);
    this.bButton.labelColor = Color.WHITE;
    this.colorbutton.draw();
    this.bButton.draw();
    this.gButton.draw();
    this.rButton.draw();

    this.countingLayerButton = new AdjustValButton("Layer: 1", canvas, startX + (size * 9), 0, size, Color.white); // displays which layer we're on
    this.addLayerButton = new AdjustValButton("+", canvas, startX + (size * 10), 0, size/2, Color.white);
    this.subtractLayerButton = new AdjustValButton("-", canvas, startX + (size * 10), size/2, size/2, Color.white);
    this.addLayerButton.draw();
    this.subtractLayerButton.draw();
    this.countingLayerButton.draw();
  }
  // toolbar gets updated, which looks at the boolean if the mouse is on the
  // taskbar, if it's on the taskbar it'll update all of the buttons (this is to
  // reduce the amount of memory it consumes per second)

  public void update() {
    System.out.println(this.canvas.activebutton);
    this.deactivateAll();
    this.determineButtonPress();

    switch (this.canvas.activebutton) {
      case "Move":
        this.canvas.lh.selectedLayer.drawBoundary();
        this.canvas.lh.drawSelectedLayer();
        break;

      case "Pen":
        System.out.println("Pen clicked");
        this.canvas.lh.selectedLayer.hideBoundary();
        this.canvas.lh.refreshCanvas();

        // here incase we add more functionality other than what's on canvas an in button itself
        break;
      
      case "Eraser":
        this.canvas.lh.selectedLayer.hideBoundary();
        this.canvas.lh.drawSelectedLayer();
        // here incase we add more functionality other than what's on canvas an in button itself
        break;
    }
      
      
      if (this.addPenSizeButton.isPressed()) {
        //addPenSizeButton.addValue();
        this.refreshPenSize();
      } 
      else if (this.addLayerButton.isPressed()) {
        //addLayerButton.addValue();
        this.refreshLayerCount();
        this.canvas.lh.selectNextLayer();
      } 
      else if (this.subtractPenSizeButton.isPressed()) {
        //subtractPenSizeButton.addValue();
        this.refreshPenSize();
      } 
      else if (this.subtractLayerButton.isPressed()) {
        //subtractLayerButton.addValue();
        this.refreshLayerCount();
        this.canvas.lh.selectPrevLayer();
      }
      
    /* 
    if (this.canvas.mousePressed) {
      moveButton.update();
      pen.update();
      eraser.update();
      addLayerButton.addValue();
      subtractLayerButton.addValue();
      addPenSizeButton.addValue();
      subtractPenSizeButton.addValue();
      clickChange();
    }
      */
  }

  public void dragUpdate() {
    if (this.canvas.mousePressed) {
      rButton.update();
      gButton.update();
      bButton.update();
      //penSizeButton.update();
      change();
    }
  }

  public void change() {
    this.currentColor = new Color(rButton.getValue(), gButton.getValue(), bButton.getValue());
    System.out.println(currentColor.toString());
    
    /*penSize = penSizeButton.getValue();
    if (penSize < 5) {
      penSize = 5;
    }*/

    colorbutton.changeColor(currentColor);
   
    //g2.setColor(Color.white);
    //g2.drawString("R: " + String.valueOf(rButton.getValue()), 425, 45);
    //g2.drawString("G: " + String.valueOf(gButton.getValue()), 425, 65);
    //g2.drawString("B: " + String.valueOf(bButton.getValue()), 425, 85);
    
    //penSizeButton.setName("Size: " + String.valueOf(penSize));
    //penSizeButton.draw();
  }

  public void clickChange() {
    
    
    
  
    
    //addPenSizeButton.setValue(1);
    //subtractPenSizeButton.setValue(1);
    

    pen.checkActive();
    eraser.checkActive();
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
    this.eraser.deactivate();
  }

  private void determineButtonPress() {
    // all buttons have their own "update" method which checks if mouse position
    // is within the button's coord, and if it is, activates the button
    
    moveButton.update();
    pen.update();
    eraser.update();
    addLayerButton.update();
    subtractLayerButton.update();
    addPenSizeButton.update();
    subtractPenSizeButton.update();
    
  }

}
