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

/**
 * JDLayer - Extends BufferedImage.  Acts as a "layer" that can be painted on to in the Java Draw app. 
 */
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

  /**
   * Constructs a layer, which is a buffered image.  Full acccess is available to the Graphics2D object, plus
   * all other methods that comes with a buffered image.  In short, the returned layer can be treated just like
   * another other buffered image, with additional methods to used my the layer handler.  
   * @param layerName name to give the layer.  (At the momement Layer Handler assigns the name 'Layer' followed
   * by a number.)
   * @param canvasBoundary a rectangle object the same size and positin as the canvas being drawn to.
   */
  public JDLayer(String layerName, Rectangle canvasBoundary) {
    super((int) canvasBoundary.getWidth(), (int) canvasBoundary.getHeight(), BufferedImage.TYPE_INT_ARGB);
    this.name = layerName;
    this.g2 = createGraphics();
    this.x = (int) canvasBoundary.getX();
    this.y = (int) canvasBoundary.getY();
    this.buffer = new BufferedImage((int) canvasBoundary.getWidth(), (int) canvasBoundary.getHeight(), BufferedImage.TYPE_INT_ARGB); 
    this.g2Buffer = this.buffer.createGraphics();
    this.g2.setBackground(new Color(0,0,0,0));  //ensures layer is transparent (except for the area being painted)
  }

  
  /**
   * Detects the highest and lowest x and y values on the layer that do not have transparent pixels.  Cycles through each 
   * pixel and checks for transparancy.  The highest and lowest values are converted into a Rectangle object that is used
   * to draw a borderd around layer.
   */
  public void detectBoundary() {
    int layerW = this.getWidth();
    int layerH = this.getHeight();
    int upperX = 0;
    int upperY = 0;
    int lowerX = layerW;
    int lowerY = layerH;
    Boolean isEmpty = true;

    this.boundary = new Rectangle(upperX, upperY, lowerX, lowerY);  // first set boundary to size of layer

    // loop through all the pixels in the layer to check for non transarent pixels
    for (int x = 0; x < layerW; x++) {
      for (int y = 0; y < layerH; y++) {
        if (this.getPixelAlpha(x, y) != 0) {
          isEmpty = false;
          lowerX = x < lowerX ? x : lowerX;
          lowerY = y < lowerY ? y : lowerY;
          upperX = x > upperX ? x : upperX;
          upperY = y > upperY ? y : upperY;
        }
      }
    }

    // pad boundary and ensure boundary the canvas's boundary
    lowerX = lowerX >= SELECT_BOX_PADDING ? lowerX - SELECT_BOX_PADDING : 0;
    lowerY = lowerY >= SELECT_BOX_PADDING ? lowerY - SELECT_BOX_PADDING : 0;
    upperX = upperX <= (layerW - SELECT_BOX_PADDING) ? upperX + SELECT_BOX_PADDING : layerW;
    upperY = upperY <= (layerH - SELECT_BOX_PADDING) ? upperY + SELECT_BOX_PADDING : layerH;

    // if layer isn't empty, shrink boundary to correct size 
    // (if it is empty, it will just remain the same size as the whole layer)
    if (!isEmpty) this.boundary = new Rectangle(lowerX, lowerY, upperX - lowerX, upperY - lowerY);


    // build resize handles at corners
    // note: not currently in use
    int  // positions for handles
      x2 = upperX - SELECT_BOX_HANDLE_SIZE,
      y2 = upperY - SELECT_BOX_HANDLE_SIZE;

    this.handleNW = new Rectangle(lowerX, lowerY, SELECT_BOX_HANDLE_SIZE, SELECT_BOX_HANDLE_SIZE); // top left corner
    this.handleNE = new Rectangle(x2, lowerY, SELECT_BOX_HANDLE_SIZE, SELECT_BOX_HANDLE_SIZE);     // top right corner
    this.handleSE = new Rectangle(x2, y2, SELECT_BOX_HANDLE_SIZE, SELECT_BOX_HANDLE_SIZE);         // bottom right corner
    this.handleSW = new Rectangle(lowerX, y2, SELECT_BOX_HANDLE_SIZE, SELECT_BOX_HANDLE_SIZE);     // bottom left corner

  }

  /**
   * returns whether or not the provided coords fall within the area of the boundary.
   * @param x x value of the coord to check
   * @param y y value of the coord to check
   * @return boolean
   */
  public Boolean boundaryContainsXY(int x, int y) {
    if (this.getBoundary() == null) return false;

    // a layer's boundary coords is relative to itself, not the canvas, so we need account for the difference
    int offsetX = x - this.getX();
    int offsetY = y - this.getY();

    return this.getBoundary().contains(offsetX , offsetY);
  }

  /**
   * Returns the name of the element found within the layer's bounds, if any.  Was useful when detecting
   * mouse hover or click of one of the handles used to resize the layer, but that functionality has been 
   * abandoned
   * @param x x value of the coord to check
   * @param y y value of the coord to check
   * @return returns a String reprresenting the name of the element.  handles are named based on compass values
   */
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

  /**
   * Draws a dashed border around the layer.  Used to help distinguish between layers and faciliate the user moving
   * the layer around the canvas.
   */
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

  /**
   * Hides the boundary. i.e. the border around the layer.
   */
  public void hideBoundary() {
    if (!this.isBoundaryOn) return;

    this.clear();
    this.g2.drawImage(this.buffer, 0, 0, null);
    this.isBoundaryOn = false;
  }

  /**
   * Gets alpha value of the pixel at the given coords
   * @param x x value of the pixel location
   * @param y y value of the pixel location
   * @return 8 bit integer of the alpha value
   */
  private int getPixelAlpha(int x, int y) {
    if (x == 0 && y ==0) {
      System.err.println(this.getRGB(x, y));
 
    }
    return ((this.getRGB(x, y) >> 24) & 0xff);
  }

  /**
   * "Selects" the layer
   * @param showBoundary whether or not to show the boundary of the layer after being selected 
   */
  public void select(Boolean showBoundary) {
    if (this.isSelected) return;

    if (showBoundary) this.drawBoundary();
    this.isSelected = true;
  }

  /**
   * "Deselect" the layer
   */
  public void deselect() {
    if (!this.isSelected) return;

    this.hideBoundary();
    this.isSelected = false;
  }

  /**
   * ABAONDONED.  Resizes layer to specified size.
   * Abandoned because G2 graphics do not scale well so layers look too distored 
   * @param x x value of new location of layer (for resizing the left side of the layer)
   * @param y y value of new location of layer (for resizing the top side of the layer)
   * @param w new width of layer
   * @param h new height of layer
   */
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

  /**
   * Saves a snapshot of the layer to a buffered image to act as a buffer.  Useful to redraw what they layer
   * looked like before drawing the boundary border.
   * @param bf Buffered Image to save to the buffer, almost exclusively, the layer itself.
   */
  private void setBuffer(BufferedImage bf) {
    this.buffer = new BufferedImage(bf.getWidth(), bf.getHeight(), BufferedImage.TYPE_INT_ARGB);
    this.g2Buffer = this.buffer.createGraphics();
    this.g2Buffer.drawImage(bf, null, null);
  }

  /**
   * Refreshes the layer.  Not currently used.
   */
  public void refresh() {
    int x = (int) getBoundary().getX();
    int y = (int) getBoundary().getY();
    int w = (int) getBoundary().getWidth();
    int h = (int) getBoundary().getHeight();
    Boolean wasBoundaryOn = false;

    
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

  /**
   * Not currently used.  Get's the portion of the layer that falls only within the boundary.  Was useful when building
   * the functionality to resize, but that functionality has been abandoned.  
   * @return buffered image
   */
  private BufferedImage getCroppedLayer() {
    int x = (int) getBoundary().getX() - this.getX();
    int y = (int) getBoundary().getY() - this.getY();
    int w = (int) getBoundary().getWidth();
    int h = (int) getBoundary().getHeight();

    System.out.print("Cropping at: ");
    printVars("", x, y, w, h);

    return this.getSubimage(x, y, w, h);
  }

  
  /**
   * Saves the provided buffered image to disk.  Was used for testing purposes mainly when building in buffer functionality.
   * Can be useful if other functionality is built into the app
   * @param img buffered image to save
   * @param fileName file name to give image.  must include .png extension
   */
  private void saveImageToFile(BufferedImage img, String fileName ) {
    try {
        File outputfile = new File(fileName);
        ImageIO.write(img, "png", outputfile);
        System.out.println("Buffer saved to " + fileName);
    } catch (IOException e) {
        System.out.println("Error saving buffer to file: " + e.getMessage());
    }
  }

  // my attempt to print multiple given vars at once (got tired of typing 'var + " " + var').  
  // it isn't pefect, but it has it's purposes
  private void printVars(String delim, String ... vars) {

    for (String var : vars) {
        System.out.print(var + " " + delim + " ");
    }
    System.out.println();
  }

  // my attempt to print multiple given vars at once (got tired of typing 'var + " " + var').  
  // it isn't pefect, but it has it's purposes
  private void printVars(String delim, Integer ... vars) {

    for (Integer var : vars) {
        System.out.print(var + " " + delim + " ");
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
