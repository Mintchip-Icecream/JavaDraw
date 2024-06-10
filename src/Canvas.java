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
  public String activebutton = "pen";
  public Graphics2D g2;
  public LayerHandler lh;         
  public ToolBar tb;
  public Rectangle boundary;
  int padding = 5;
  boolean isDragging = false;

  public Canvas(int width, int height, DrawingPanel dp) {
    this.width = width;
    this.height = height;
    this.g2 = dp.getGraphics();
    this.tb = new ToolBar(this, dp.getWidth(), 100);
    this.render();
    this.lh = new LayerHandler(dp, this.boundary);
    
    this.lh.addLayer();

    // add mouse handlers                                
    dp.onMouseDown((x, y) -> changeInitialCoords(x, y));
    dp.onDrag((x, y) -> penDraw(x, y));                              
    dp.onMouseUp((x, y) -> mouseRelease());
    dp.onMouseMove((x, y) -> handleMouseMove(x,y));
  }

  public void render() {
    int y = (int) this.tb.bounds.getHeight() + (this.tb.padding*2) + this.padding; // the +5 is padding between toolbar and canvas
    this.boundary = new Rectangle(this.padding + 1, y, this.width, this.height);
    
    // shadow effect
    this.g2.setColor(Color.DARK_GRAY);
    this.g2.drawLine(this.padding, y, this.padding, y + this.height); // left shadow
    this.g2.drawLine(this.padding, y - 1, this.padding + this.width + 2, y - 1); // top shadow
    this.g2.drawLine(this.padding + this.width + 1, y - 1, this.padding + this.width + 1, y + this.height); // right inner shadow
    this.g2.drawLine(this.padding, y + this.height + 1, this.padding + this.width + 2, y + this.height + 1); // bottom inner shadow
    this.g2.setColor(Color.GRAY);
    this.g2.drawLine(this.padding, y + this.height + 2, this.padding + this.width + 2, y + this.height + 2); // bottom outer shadow
    this.g2.drawLine(this.padding + this.width + 2, y - 1, this.padding + this.width + 2, y + this.height); // right outer shadow

    // actual paint area
    this.g2.setColor(DEFAULT_CANVAS_COLOR);
    this.g2.fillRect(this.padding + 1, y, this.width, this.height); // this is the canvas, art must fall inside
    
  }

  public void changeInitialCoords(int x, int y) {
    initialx = x;
    mouseX = x;
    initialy = y;
    mouseY = y;
    mousePressed = true;
    if (y < 100) {
      tb.update(mousePressed);
    } // VERY IMPORTANT, will update all the buttons to see if they're pressed, and if
      // they're pressed they'll activate
  }

  public void mouseRelease() {
    mousePressed = false;
    isDragging = false;
    tb.update(mousePressed);
  }


  public void penDraw(int x, int y) { 
                           
    mousePressed = true;
    mouseX = x;
    mouseY = y;
    
    if (tb.bounds.contains(x, y)) {
      tb.dragUpdate();
    } else if (this.boundary.contains(x, y)) {
        // get selected layer, calculate correct x,y coords and apply which ever tool is selected
        JDLayer sl = this.lh.selectedLayer;
        int offsetX =  x - (int) this.boundary.getX() - tb.penSize/2; // offset our values by the position of the canvas
        int offsetY =  y - (int) this.boundary.getY() - tb.penSize/2;

        // hide tool indicator (without this, it would 'stick' to it's last postition before drag started)
        if(!this.isDragging) this.lh.refreshCanvas();

        switch (activebutton) {
          case "Pen":
            int d = tb.penSize;  // circle diameter

            sl.g2.setPaint(tb.currentColor);
            sl.g2.fillOval(offsetX, offsetY, d, d);
            lh.drawSelectedLayer();
            break;
            
          case "Eraser":
            int slen = tb.penSize;  // square side length
           
            sl.g2.setBackground(new Color(0,0,0,0));
            sl.g2.clearRect(offsetX, offsetY, slen, slen);    
            lh.drawSelectedLayer();
            lh.refreshCanvas(); // needed for alpha values to be applied immediately
           
            break;
      }
      this.isDragging = true;
    }

  }

  public void handleMouseMove(int x, int y) {
    if (this.tb.bounds.contains(x, y)) this.tb.determineButtonHover(x, y);
    this.moveToolIndicators(x, y);
  }

  public void moveToolIndicators(int x, int y) {
    if (!this.boundary.contains(x - tb.penSize/2, y - tb.penSize/2) || !this.boundary.contains(x + tb.penSize/2, y + tb.penSize/2)) return; 

    switch (this.activebutton) {
      case "Pen":
        this.lh.refreshCanvas();
        this.g2.setColor(Color.BLACK);
        this.g2.drawOval(x - (tb.penSize/2), y - (tb.penSize/2), tb.penSize, tb.penSize);
        break;

      case "Eraser":
        this.lh.refreshCanvas();
        this.g2.setColor(Color.BLACK);
        this.g2.drawRect(x - (tb.penSize/2), y - (tb.penSize/2), tb.penSize, tb.penSize);
        break;

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
