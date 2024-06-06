import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.image.BufferedImage;


import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class JDLayer extends BufferedImage {
  final static int SELECT_BOX_PADDING = 2;
  final static int SELECT_BOX_HANDLE_SIZE = SELECT_BOX_PADDING - 2;
  final static Color SELECT_BOX_BORDER_COLOR = Color.GRAY;
  final static Color SELECT_BOX_HANDLE_COLOR = Color.RED;
  
  private String name = "Layer";
  int x = 0, y = 0, mouseX, mouseY;
  Graphics2D g2, g2Buffer, canvasG2;
  Rectangle boundary, handleNW, handleNE, handleSW, handleSE;  // note: handles have been abondoned
  BufferedImage buffer;  // buffer to save version of layer to before drawing border for select method
  Boolean isSelected = false, canDrag = false, isBoundaryOn = false;
  
  public JDLayer(String layerName, Rectangle canvasBoundary) {
    super((int) canvasBoundary.getWidth(), (int) canvasBoundary.getHeight(), BufferedImage.TYPE_INT_ARGB);
    this.name = layerName;
    this.g2 = createGraphics();
    this.x = (int) canvasBoundary.getX();
    this.y = (int) canvasBoundary.getY();
    this.buffer = new BufferedImage((int) canvasBoundary.getWidth(), (int) canvasBoundary.getHeight(), BufferedImage.TYPE_INT_ARGB); 
    this.g2Buffer = this.buffer.createGraphics();
  }

  public void detectBoundary() {
    int layerW = this.getWidth();
    int layerH = this.getHeight();
    int upperX = 0;
    int upperY = 0;
    int lowerX = layerW;
    int lowerY = layerH;
    Boolean isEmpty = true;

    System.out.println("Detecting Boundary...");
    printVars("", "Layer: ", this.getX()+"", this.getY()+"", layerW+"", layerH+"");

    this.boundary = new Rectangle(upperX, upperY, lowerX, lowerY);  // first set boundary to size of layer
    int a = 0;
    // loop through all the pixels in the layer to check for non transarent pixels
    for (int x = 0; x < layerW; x++) {
      for (int y = 0; y < layerH; y++) {
        if (this.getPixelAlpha(x, y) != 0) {
          a++;
          isEmpty = false;
          //System.out.print(x + ", " + y + " ");
          lowerX = x < lowerX ? x : lowerX;
          lowerY = y < lowerY ? y : lowerY;
          upperX = x > upperX ? x : upperX;
          upperY = y > upperY ? y : upperY;
        }
      }
    }
    System.out.println("alphas: "+a);
    printVars("", "Boundary bounds Before Padding: ", lowerX+"", lowerY+"", upperX+"", upperY+"");

    // pad boundary and ensure boundary the canvas's boundary
    lowerX = lowerX >= SELECT_BOX_PADDING ? lowerX - SELECT_BOX_PADDING : 0;
    lowerY = lowerY >= SELECT_BOX_PADDING ? lowerY - SELECT_BOX_PADDING : 0;
    upperX = upperX <= (layerW - SELECT_BOX_PADDING) ? upperX + SELECT_BOX_PADDING : layerW;
    upperY = upperY <= (layerH - SELECT_BOX_PADDING) ? upperY + SELECT_BOX_PADDING : layerH;

    // if layer isn't empty, shrink boundary to correct size
    if (!isEmpty) this.boundary = new Rectangle(lowerX, lowerY, upperX - lowerX, upperY - lowerY);

    printVars("", "Boundary x,y,w,h After Padding: ", lowerX+"", lowerY+"", (upperX - lowerX)+"", (upperY - lowerY)+"");

    // build resize handles at corners
    int  // positions for handles
      x2 = upperX - SELECT_BOX_HANDLE_SIZE,
      y2 = upperY - SELECT_BOX_HANDLE_SIZE;

    this.handleNW = new Rectangle(lowerX, lowerY, SELECT_BOX_HANDLE_SIZE, SELECT_BOX_HANDLE_SIZE); // top left corner
    this.handleNE = new Rectangle(x2, lowerY, SELECT_BOX_HANDLE_SIZE, SELECT_BOX_HANDLE_SIZE);     // top right corner
    this.handleSE = new Rectangle(x2, y2, SELECT_BOX_HANDLE_SIZE, SELECT_BOX_HANDLE_SIZE);         // bottom right corner
    this.handleSW = new Rectangle(lowerX, y2, SELECT_BOX_HANDLE_SIZE, SELECT_BOX_HANDLE_SIZE);     // bottom left corner

  }

  // checks if x, y coords are within the layer's bounds
  public Boolean boundaryContainsXY(int x, int y) {
    if (this.getBoundary() == null) return false;

    // a layer's boundary coords is relative to itself, not the canvas, so we need account for the difference
    int offsetX = x - this.getX();
    int offsetY = y - this.getY();

    return this.getBoundary().contains(offsetX , offsetY);
  }

  // checks if x, y is within the layer's bound and returns the element name
  public String getBoundaryElAtXY(int x, int y) {
    String elName = null;
    

    if (!this.boundaryContainsXY(x, y) || this.boundary == null) return null;
    if (this.handleNW.contains(x, y)) elName = "NW";
    else 
      if (this.handleNE.contains(x, y)) elName = "NE";
    else 
      if (this.handleSE.contains(x, y)) elName = "SE";
    else 
      if (this.handleSW.contains(x, y)) elName = "SW";
    else 
      elName = "inside";

    return elName;
  }

  public void drawBoundary() {
    //if (this.isBoundaryOn) return;
    

    Stroke currentStroke = this.g2.getStroke();
    Stroke dash = new BasicStroke( //https://stackoverflow.com/questions/21989082/drawing-dashed-line-in-java
      1, 
      BasicStroke.CAP_BUTT, 
      BasicStroke.JOIN_MITER,
      1.0f,
      new float[]{9},
      0
    );
    Color currentColor = this.g2.getColor();

    this.detectBoundary();
    System.out.println("Drawing boundary");
    // save the state of the layer before drawing border to easily remove the border in the deselect method
    this.setBuffer(this);
    //this.setBuffer(this.getCroppedLayer());
    saveImageToFile(this, "layer_before_boundary_draw.png");
    saveImageToFile(this.buffer, "buffer_before_boundary_draw.png"); 
      
    // draw dashed border
    this.g2.setStroke(dash);
    this.g2.setColor(SELECT_BOX_BORDER_COLOR);
    this.g2.draw(this.boundary);
    this.g2.setStroke(currentStroke);
    
    /* draw resize handles (abandoned) 
    this.g2.setColor(Color.RED);
    this.g2.fill(this.handleNW); // top left corner
    this.g2.fill(this.handleNE); // top right corner
    this.g2.fill(this.handleSE); // bottom right corner
    this.g2.fill(this.handleSW); // bottom left corner
    */ 

    this.g2.setColor(currentColor);
    this.isBoundaryOn = true;
  }

  public void hideBoundary() {
    if (!this.isBoundaryOn) return;

    this.clear();
    this.g2.drawImage(this.buffer, 0, 0, null);
    this.isBoundaryOn = false;
  }

  private int getPixelAlpha(int x, int y) {
    return ((this.getRGB(x, y) >> 24) & 0xff);
  }

  public void select(Boolean showBoundary) {
    if (this.isSelected) return;

    if (showBoundary) this.drawBoundary();
    this.isSelected = true;
  }

  public void deselect() {
    if (!this.isSelected) return;

    this.hideBoundary();
    this.isSelected = false;
  }

  /* this method is abandoned.  G2 graphics do not scale well so layers look too distored */
  public void resize(int x, int y, int w, int h) {    
    System.out.println("resizing");
    int newX = (int) getBoundary().getX() - this.getX();
    int newY = (int) getBoundary().getY() - this.getY();
    int newW = (int) getBoundary().getWidth();
    int newH = (int) getBoundary().getHeight();
    Boolean wasBoundaryOn = false;

    if (this.isBoundaryOn) {
      wasBoundaryOn = true;
      this.deselect();
    }

    this.setBuffer(this.getSubimage(newX, newY, newW, newH));

    //saveImageToFile(this.buffer, "1 buffer_before_scale.png");
    //saveImageToFile(this, "2 layer_before_scale.png");
    //temp.createGraphics().scale(sx, sy);
    //saveImageToFile(temp, "subimage_after_scale.png");
    this.clear();
    this.g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    this.g2.addRenderingHints(new RenderingHints(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE));
    this.g2.drawImage(this.buffer, x, y, w, h, null);
    //saveImageToFile(this, "3 layer_after_scale.png");
    this.detectBoundary();
    if (wasBoundaryOn) this.drawBoundary();
  
  }

  public void clear() {
    Color bg = new Color(255, 255, 255, 0);

    this.g2.setBackground(bg);
    this.g2.clearRect(0, 0, this.getWidth(), this.getHeight());
  }

  private void setBuffer(BufferedImage bf) {
    System.out.println("Setting buffer...");
    saveImageToFile(this, "layer_before_buffer_save.png");
    saveImageToFile(bf, "img_to_buffer_before_buffer_save.png");
    System.out.println("  BF: " + bf.getWidth() + " x " + bf.getHeight());
    this.buffer = new BufferedImage(bf.getWidth(), bf.getHeight(), BufferedImage.TYPE_INT_ARGB);
    this.g2Buffer = this.buffer.createGraphics();
    this.g2Buffer.drawImage(bf, null, null);
    saveImageToFile(buffer, "buffer_after_buffer_save.png");
  }

  public void refresh() {
    int x = (int) getBoundary().getX();
    int y = (int) getBoundary().getY();
    int w = (int) getBoundary().getWidth();
    int h = (int) getBoundary().getHeight();
    int newX = x;// - this.getX();
    int newY = y;// - this.getY();
    Boolean wasBoundaryOn = false;

    System.out.println("Refreshing Layer...");
    System.out.println("Layer: " + this.getX() + " " + this.getY() + " ");
    System.out.println("Boundary: " + x + " " + y + " ");   
    System.out.println("New: " + newX + " " + newY + " ");
    System.out.println("Boundary on: " + isBoundaryOn);
    
    if (this.isBoundaryOn) {
      wasBoundaryOn = true;
      this.hideBoundary();
    }

    this.setBuffer(this);
    //this.setBuffer(this.getCroppedLayer());
    
    //this.clear();
    //this.x = 0;
    //this.y = 0;
    //this.g2.drawImage(buffer, newX,  newY, null);
    //if (wasBoundaryOn) this.drawBoundary();
  }

  private BufferedImage getCroppedLayer() {
    int x = (int) getBoundary().getX() - this.getX();
    int y = (int) getBoundary().getY() - this.getY();
    int w = (int) getBoundary().getWidth();
    int h = (int) getBoundary().getHeight();

    System.out.print("Cropping at: ");
    printVars("", x, y, w, h);

    return this.getSubimage(x, y, w, h);
  }

  
  // used for testing purposes
  public void saveImageToFile(BufferedImage img, String fileName ) {
    try {
        File outputfile = new File(fileName);
        ImageIO.write(img, "png", outputfile);
        System.out.println("Buffer saved to " + fileName);
    } catch (IOException e) {
        System.out.println("Error saving buffer to file: " + e.getMessage());
    }
  }

  public void printVars(String delim, String ... vars) {

    for (int i = 0; i < vars.length; i++) {
      System.out.print(vars[i] + " " + delim + " ");
    }
    System.out.println();
  }

  public void printVars(String delim, Integer ... vars) {

    for (int i = 0; i < vars.length; i++) {
      System.out.print(vars[i] + " " + delim + " ");
    }
    System.out.println();
  }

  // getters
  public String getName() { return this.name; }
  public Rectangle getBoundary() { return this.boundary; }
  public int getX() { return this.x; }
  public int getY() { return this.y; }

  // setters
  public void setName(String name) {
    this.name = name;
  }

 

  public static void main(String[] args) {
    
    
  }
}
