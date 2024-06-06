import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Canvas {
  final static Color DEFAULT_CANVAS_COLOR = Color.WHITE;

  private int width;
  private  int height;
  public int initialx = 0; // used for mousedown events to set the initial coords for starting shapes (for buyan)                   
  public int initialy = 0;
  public int mouseX = 0; // main purpose for detecting when mouse is clicking buttons or is on toolbar
  public int mouseY = 0;
  public Color canvasColor = new Color(255, 255, 255);
  public boolean mousePressed = false; // checked by button class to tell whether to activate or not
  public String activebutton = "colorbutton";
  public Graphics2D g2;
  public LayerHandler lh;         
  public ToolBar tb;
  public Rectangle boundary;

  public Canvas(int width, int height, DrawingPanel dp) {
    this.width = width;
    this.height = height;
    this.g2 = dp.getGraphics();
    this.tb = new ToolBar(this);
    this.render();
    this.lh = new LayerHandler(dp, this.boundary);
    
    this.lh.addLayer();

    // add mouse handlers                                
    dp.onMouseDown((x, y) -> changeInitialCoords(x, y));
    dp.onDrag((x, y) -> penDraw(x, y)); // on penDraw just for testing purposes, will add a check for the tool we're on later                              
    dp.onMouseUp((x, y) -> mouseRelease());
    dp.onMouseClick((x,y) -> handleMouseClick(x,y));

  }

  public void render() {
    this.g2.setColor(DEFAULT_CANVAS_COLOR);
    this.g2.fillRect(0, 102, width, height); // this is the canvas, art must fall between (x=0), (y=100) and (x=width), (y=height)
    this.boundary = new Rectangle(0, 102, width, height);
  }

  public void changeInitialCoords(int x, int y) {
    initialx = x;
    mouseX = x;
    initialy = y;
    mouseY = y;
    mousePressed = true;
    if (y < 100) {
      tb.update();
    } // VERY IMPORTANT, will update all the buttons to see if they're pressed, and if
      // they're pressed they'll activate
  }

  public void mouseRelease() {
    mousePressed = false;
  }

  private void handleMouseClick(int x, int y) {

  }

  private void handleMouseDrag(int x, int y) {
    System.out.println("mouse drag handler");
    switch(this.activebutton) {
      case "pen":
        this.penDraw(x, y);
        break;  

      
    }
  }

  public void penDraw(int x, int y) { // this method is mainly just for testing tool functionality, all methods will
                                      // fall under one large onDrag update method
    mousePressed = true;
    mouseX = x;
    mouseY = y;
    
    if (y < 100) {
      System.out.println("tb dragging");
      tb.dragUpdate();
    } else if (this.boundary.contains(x, y)) {
      System.out.println("pendraw method");
      switch (activebutton) {
        case "Pen":
        case "Eraser":
          System.out.println("pen/eraser");
          System.out.println(this.activebutton);
          JDLayer sl = this.lh.selectedLayer;
          sl.hideBoundary();
          Color color = activebutton.equals("Pen") ? tb.currentColor : Color.WHITE;// new Color(255, 255, 255, 0);
          sl.g2.setColor(color);
          int offsetX =  x - (int) this.boundary.getX();
          int offsetY =  y - (int) this.boundary.getY();
          sl.g2.fillOval(offsetX, offsetY, tb.penSize, tb.penSize);
          //sl.g2.fillOval(x, y, tb.penSize, tb.penSize);
          System.out.println("drawing to layer at: " + offsetX + ". " + offsetY);
          lh.drawSelectedLayer();
       
          break;
      }
    }

  }

  public void makeRect(int x, int y) { // not in use, just an experiment for a tool to make a rectangle
    g2.setColor(Color.red);
    if (x < initialx) {
      if (y < initialy) {
        g2.fillRect(x, y, initialx - x, initialy - y);
        System.out.println("opt 1");
      } else {
        g2.fillRect(x, initialy, initialx - x, y - initialy);
        System.out.println("opt 2");
      }
    } else if (y < initialy) {
      if (x < initialx) {
        g2.fillRect(x, y, initialx - x, initialy - y);
        System.out.println("opt 3");
      } else {
        g2.fillRect(initialx, y, x - initialx, initialy - y);
        System.out.println("opt 4");
      }
    } else {
      if (initialy != 0) {
        g2.fillRect(initialx, initialy, x - initialx, y - initialy);
      }
    }
  }

  // getters
  public int getWidth() { return this.width; }
  public int getHeight() { return height; }

  // setters
  public void setWidth(int newWidth) {
    this.width = newWidth;
    this.render();
  }

  public void setHeight(int newHeight) {
    this.height = newHeight;
    this.render();
  }

}
