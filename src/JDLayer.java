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
  final static int SELECT_BOX_PADDING = 6;
  final static int SELECT_BOX_HANDLE_SIZE = SELECT_BOX_PADDING - 2;
  final static Color SELECT_BOX_BORDER_COLOR = Color.GRAY;
  final static Color SELECT_BOX_HANDLE_COLOR = Color.RED;
  
  private String name = "Layer";
  int x = 0, y = 0, mouseX, mouseY;
  Graphics2D g2, g2Buffer, canvasG2;
  Rectangle boundary, handleNW, handleNE, handleSW, handleSE;
  BufferedImage buffer;  // buffer to save version of layer to before drawing border for select method
  Boolean isSelected = false, canDrag = false;
  
  public JDLayer(String layerName, int width, int height) {
    super(width, height, BufferedImage.TYPE_INT_ARGB);
    this.name = layerName;
    this.boundary = new Rectangle(0, 0, width, height);
    this.g2 = createGraphics();
    this.buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB); 
    this.g2Buffer = this.buffer.createGraphics();
  }

  public void detectBoundary() {
    int layerW = this.getWidth();
    int layerH = this.getHeight();
    int upperX = 0;
    int upperY = 0;
    int lowerX = layerW;
    int lowerY = layerH;

    // loop through all the pixels in the layer to check for non transarent pixels
    for (int x = 0; x < layerW; x++) {
      for (int y = 0; y < layerH; y++) {
        if (this.getPixelAlpha(x, y) != 0) {
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
    this.boundary.setBounds(lowerX, lowerY, upperX - lowerX, upperY - lowerY);

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

    return this.getBoundary().contains(x, y);
  }

  // checks if x, y is within the layer's bound and returns the element name
  public String getBoundaryElAtXY(int x, int y) {
    String elName = null;
    // a layer's boundary coords is relative to itself, not the canvas, so we need account for the difference
    int offsetX = x - this.getX();
    int offsetY = y - this.getY();

    if (!this.boundaryContainsXY(offsetX, offsetY)) return null;
    if (this.handleNW.contains(offsetX, offsetY)) elName = "NW";
    else 
      if (this.handleNE.contains(offsetX, offsetY)) elName = "NE";
    else 
      if (this.handleSE.contains(offsetX, offsetY)) elName = "SE";
    else 
      if (this.handleSW.contains(offsetX, offsetY)) elName = "SW";
    else 
      elName = "inside";

    return elName;
  }

  private int getPixelAlpha(int x, int y) {
    return ((this.getRGB(x, y) >> 24) & 0xff);
  }

  public void select() {
    if (this.isSelected) return;

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
    

    // save the state of the layer before drawing border to easily remove the border in the deselect method
    this.g2Buffer.drawImage(this, 0, 0, null); 
    //this.saveImageToFile(this, "layer_before_draw.png");
    //this.saveImageToFile(this.buffer, "buffer_before_draw.png");
    
    // draw dashed border
    this.g2.setStroke(dash);
    this.g2.setColor(SELECT_BOX_BORDER_COLOR);
    this.g2.draw(this.boundary);
    this.g2.setStroke(currentStroke);
    // draw resize handles
    this.g2.setColor(Color.RED);
    this.g2.fill(this.handleNW); // top left corner
    this.g2.fill(this.handleNE); // top right corner
    this.g2.fill(this.handleSE); // bottom right corner
    this.g2.fill(this.handleSW); // bottom left corner
     
    this.g2.setColor(currentColor);

    //this.saveImageToFile(this, "layer_after_draw.png");
    //this.saveImageToFile(this.buffer, "buffer_afer_draw.png");

    this.isSelected = true;
  }

  public void deselect() {
    if (!this.isSelected) return;
    //this.saveImageToFile(this, "layer_before_deselect.png");
    this.clear();
    
    this.g2.drawImage(this.buffer, 0, 0, null);
    //this.saveImageToFile(this, "layer_afer_deselect.png");
    this.isSelected = false;
  }

  public void resize(int x, int y, int w, int h) {    
    System.out.println("resizing");
    int newX = (int) getBoundary().getX();
    int newY = (int) getBoundary().getY();
    int newW = (int) getBoundary().getWidth();
    int newH = (int) getBoundary().getHeight();
    Boolean wasSelected = false;

    if (this.isSelected) {
      wasSelected = true;
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
    if (wasSelected) this.select();
  
  }

  public void clear() {
    Color bg = new Color(255, 255, 255, 0);

    this.g2.setBackground(bg);
    this.g2.clearRect(0, 0, this.getWidth(), this.getHeight());
  }

  private void setBuffer(BufferedImage bf) {
    this.buffer = new BufferedImage(bf.getWidth(), bf.getHeight(), BufferedImage.TYPE_INT_ARGB);
    this.g2Buffer = this.buffer.createGraphics();
    this.g2Buffer.drawImage(bf, null, null);
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

// getters
public String getName() { return this.name; }
public Rectangle getBoundary() { return this.boundary; }
public int getX() { return this.x; }
public int getY() { return this.y; }

 

  public static void main(String[] args) {
    
    
  }
}
