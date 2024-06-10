import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JPanel;


/**
 * Handler for JDLayer instances.  Handles mouse events meant to draw on the layers created from JDLayers.  
 * Stores all layers into a TreeMap.  Acts as a mediator between Canvas and JDLayer.
 */
public class LayerHandler {
 
  Map<String, JDLayer> layers = new TreeMap<>();
  Graphics2D canvasG2;
  JDLayer selectedLayer;
  Rectangle canvasBounds;
  

  /**
   * Constructs a Layer Handler to faciliate handling all the layers in the Java Draw App. Requires the Drawing Panel
   * instance and the boundary of the canvas.  
   * @param dp The instance of Drawing Panel that everything draw to.  It is only used to attach mouse event handling
   * @param canvasBoundary A Rectangle representing the coordinates, widht, and height of the canvas.
   */
  public LayerHandler(DrawingPanel dp, Rectangle canvasBoundary) {
    this.canvasG2 = dp.getGraphics();
    this.canvasBounds = canvasBoundary;
    this.addListeners(dp);
  }

  /**
   * Adds the handlers for when mouse events occur
   * @param dp The DrawingPanel instance to attached the mouse listeners to
   */
  private void addListeners(DrawingPanel dp) {
    dp.onMouseDown((x, y) -> this.handleMouseDown(x, y));
    dp.onMouseUp((x, y) -> this.handleMouseUp(x, y));
    dp.onMouseDrag((x, y) -> this.handleMouseDrag(x, y));
    dp.onMouseMove((x, y) -> this.handleMouseMove(x, y, dp));
    //dp.onKeyUp(key -> this.handleKeyPress(key));

  }

  /**
   * Handles mousedown event.  If mousedown happens inside the selected layer, the coords are saved so the mousedrag
   * listener can tell if a layet should be dragged and in which direction.
   * @param x x value of when the mouse button was pressed
   * @param y y value of when the mouse button was pressed
   */
  private void handleMouseDown(int x, int y) {
    if (this.selectedLayer == null || !this.selectedLayer.isBoundaryOn || !this.selectedLayer.boundaryContainsXY(x, y)) return;
    
    JDLayer l = this.selectedLayer;
    
    l.mouseX = x;
    l.mouseY = y;
    l.canDrag = true;

  }

  /**
   * Handles mouseup event.  Makes sure that the selected layer's canDrag flag is turned off.
   * @param x x value of when the mouse button was released
   * @param y y value of when the mouse button was released
   */
  private void handleMouseUp(int x, int y) {
    if (this.selectedLayer == null || !this.selectedLayer.isSelected) return;

    this.selectedLayer.canDrag = false;
  }

  /**
   * Handles mousedrag event.  If mouse was pressed inside the selected layer, and the mouse is dragged,
   * the layer moves with the mouse.
   * @param x x value of the current mouse position
   * @param y y value of the current mouse position
   */
  private void handleMouseDrag(int x, int y) {
    if (this.selectedLayer == null || !this.selectedLayer.canDrag || !this.canvasContainsLayer(x, y)) return;
  
    JDLayer l = this.selectedLayer;
    int newX = l.x + (x - l.mouseX);
    int newY = l.y + (y - l.mouseY);
    Integer[] cb = this.rectangleDimToArr(this.canvasBounds);

    this.canvasG2.setBackground(Color.WHITE);
    this.canvasG2.clearRect(cb[0], cb[1], cb[2], cb[3]);
    l.x = newX;
    l.y = newY;
    l.mouseX = x;
    l.mouseY = y;
    l.detectBoundary();
    this.refreshCanvas();
  }

  /**
   * Handles mousemove events.  Currently is used only to detect if mouse cursoe is hovering over the layer, 
   * and if so, display a different cursor. Was also used to change cursor when handles were shown in the border
   * (for resize functionality), but that feature was abandoned.
   * @param x x value of the current mouse position
   * @param y y value of the current mouse position
   * @param dp the DrawingPanel instance  required in order to change the mouse cursor.
   */
  private void handleMouseMove(int x, int y, DrawingPanel dp)  {
    JPanel imgPnl = this.getImagePanel(dp);

    if (this.selectedLayer == null || imgPnl == null) return; 
    
    if (this.selectedLayer.isSelected && this.selectedLayer.getBoundaryElAtXY(x, y) != null) {
      String boundaryEl = this.selectedLayer.getBoundaryElAtXY(x, y);

      // https://stackoverflow.com/questions/7359189/how-to-change-the-mouse-cursor-in-java
      // NOTE: since resizing has been abandoned, changing the cursor on the corners has been 
      // commented out
      switch (boundaryEl) {
        case "NW":
          //imgPnl.setCursor(new Cursor(Cursor.NW_RESIZE_CURSOR)); 
          break;
        case "NE":
          //imgPnl.setCursor(new Cursor(Cursor.NE_RESIZE_CURSOR));
          break;
        case "SE":
          //imgPnl.setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
          break;
        case "SW":
          //imgPnl.setCursor(new Cursor(Cursor.SW_RESIZE_CURSOR));
          break;
        default:
          imgPnl.setCursor(new Cursor(Cursor.HAND_CURSOR));
          break;
      }
    }
    else
      imgPnl.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); 
  }

  /**
   * NOT CURRENTLY IN USE.  Handles keypress events.  Was used during testing of functionality meant for mouseevents, while 
   * waiting for the Canvas code.
   * @param key a char value representing the key that was pressed
   */
  private void handleKeyPress(char key) {
    if ("sdp".indexOf(key) == -1 || this.selectedLayer == null) return;
      
      JDLayer l = this.selectedLayer;
     
      if (key == 's') { this.selectedLayer.drawBoundary(); this.canvasG2.drawImage(l, l.getX(), l.getY(), null); }
      if (key == 'd') { this.selectedLayer.hideBoundary(); this.refreshCanvas(); }
      if (key == 'r') {  // this functionality was for testing purposes only
        Rectangle b = l.getBoundary();
        int newX = (int) b.getX();
        int newY = (int) b.getY();
        int newW = (int) b.getWidth() + 50;
        int newH = (int) b.getHeight() + 25;

        System.out.println(newX + " " + newY + " " + newW + " " + newH);
        l.resize(
          (int) b.getX(), 
          (int) b.getY(), 
          (int) b.getWidth() + 50, 
          (int) b.getHeight() + 25);
        this.refreshCanvas();
      }
      if (key == 'p') {
        Rectangle b = l.getBoundary();
        System.out.println("Boundary: " + b.getX() + " " + b.getY() + " " + b.getWidth() + " " + b.getHeight());
      }


  }

  /**
   * Detects if the current layer is complely within the canvas's boundary.
   * @param mouseX
   * @param mouseY
   * @return
   */
  public boolean canvasContainsLayer(int mouseX, int mouseY) {
    JDLayer l = this.selectedLayer;
    Rectangle b = l.getBoundary();
    int newX = l.x + (mouseX - l.mouseX);
    int newY = l.y + (mouseY - l.mouseY);
    double cx1 = this.canvasBounds.getMinX() - 2; // -2 comes from testing, seems to need the adjustment to get right to the edge
    double cy1 = this.canvasBounds.getMinY() - 2; // of the canvase
    double cx2 = this.canvasBounds.getMaxX() + 2;
    double cy2 = this.canvasBounds.getMaxY() + 2;
    double lx1 = b.getMinX() + newX;
    double ly1 = b.getMinY() + newY;
    double lx2 = b.getMaxX() + newX;
    double ly2 = b.getMaxY() + newY;

    return ((lx1 > cx1 && lx2 < cx2) && (ly1 > cy1 && ly2 < cy2));
  }

  /**
   * Creates a new layer and adds it to the TreeMap.  Does not draw it to the canvas.
   * @return A copy of the layer just created
   */
  public JDLayer addLayer() {
    String layerName = "Layer " + (this.layers.size() + 1);

    if (layerExists(layerName)) 
      throw new IllegalArgumentException("Layer with name " + layerName + " already exists.");
    else {
      JDLayer l = new JDLayer(layerName, this.canvasBounds); 
      
      this.layers.put(layerName, l);
      this.selectLayer(layerName, false);

      return this.selectedLayer;  // return newly added layer for convience
    }
  }

  /**
   * Deletes layer with specified name.  Calls renameLayer to reaname the rest of the layers in order to mainain
   * continuity between layer numbers.   For example, if there are 10 layers, and 'Layer 6' gets deleted, 'Layer 7'
   * becomes the new 'Layer 6', 'Layer 8' becomes the new 'Layer 7', and so on.
   * 
   * NOTE: Uses double recursion with renameLayer.  i.e. recursively calls renameLayer until all layers are renamed.
   * which recursilvely calls this method to delete the layer  that was copied to a new name.
   * @param layerName name of layer to be deleted
   */
  public void deleteLayer(String layerName) {

    if (!layerExists(layerName)) 
      throw new IllegalArgumentException("Layer with name " + layerName + " does not exist.");
    else {
      int layerNum = this.parseLayerInt(layerName);
      System.out.println("Deleting " + layerName);
      this.layers.remove(layerName);
      this.renameLayer("Layer " + (layerNum +1), layerName);
    }
  }

  /**
   * Renames layer with the specificed name to the new specified name.
   * 
   * NOTE: Uses double recursion with deleteLayer
   * @param layerName name of the layer to rename
   * @param newName new name of the layer
   */
  public void renameLayer(String layerName, String newName) {
    int layerNum = this.parseLayerInt(layerName);

    if (this.parseLayerInt(newName) > this.layers.size()) return;
    if 
      (!this.layerExists(layerName)) this.renameLayer("Layer " + (layerNum + 1), layerName);
    else {
      JDLayer l = this.layers.get(layerName);
      System.out.println("Renaming " + layerName + " to " + newName);
      l.setName(newName);
      this.layers.put(newName, l);
      this.selectLayer(newName, true);
      this.deleteLayer(layerName);
    }
  }


/**
 * Sets the layer with the specified name as the selected (AKA current) layer
 * @param layerName name of the layer to select
 * @param showBoundary boolean determining if the layer should show the boundary after selection
 */
  public void selectLayer(String layerName, Boolean showBoundary) {
    JDLayer layer = this.getLayer(layerName);
    layer.select(showBoundary);
    this.selectedLayer = layer;
    if (showBoundary) this.refreshCanvas();
  }

  /**
   * Selects the previous layer in the TreeMap.  If current layer is the first layer, the method does nothing.
   */
  public void selectPrevLayer() {
    int layerNumber = this.parseLayerInt(this.selectedLayer.getName());
    
    if (layerNumber <= 1) return;
    layerNumber--;
    String layerName = "Layer " + layerNumber;

    if (this.layerExists(layerName)) {
      this.deselectLayer(layerName);
      this.selectLayer(layerName, true);
    } 
  }

  /**
   * Selects the next layer in the TreeMap.  If current layer is the last layer, the method does nothing.
   */
  public void selectNextLayer() {
    int layerNumber = this.parseLayerInt(this.selectedLayer.getName());
    
    //if (layerNumber >= this.layers.size()) return;
    if (layerNumber >= this.layers.size()) this.addLayer();   // not fond of implementing it this way, but it will have 
                                                              // to do unless an 'add layer' button is added to the toolbar
    layerNumber++;
    String layerName = "Layer " + layerNumber;

    if (this.layerExists(layerName)) {
      this.deselectLayer(layerName);
      this.selectLayer(layerName, true);
    } 
  }

  /**
   * Deselects the specified layer
   * @param layerName name of layer to deselect
   */
  public void deselectLayer(String layerName) {
    this.selectedLayer.deselect();
    this.refreshCanvas();
  }

  /**
   * Draws the specified layer to the canvas
   * @param layer the name of the layer to draw
   */
  private void drawLayer(JDLayer layer) {
    this.canvasG2.drawImage(layer, layer.x, layer.y, null);
  }

  /**
   * Draws the current selected layer to the canvas
   */
  public void drawSelectedLayer() {
    this.drawLayer(selectedLayer);
  }

  /**
   * Refreshed the canvas by setting the backgound to WHITE and doing a clearRect on the entire canvas
   */
  public void refreshCanvas() {
    Integer[] cb = this.rectangleDimToArr(this.canvasBounds);

    this.canvasG2.setBackground(Color.WHITE);
    this.canvasG2.clearRect(cb[0], cb[1], cb[2], cb[3]);
    this.layers.forEach((name, layer) -> this.drawLayer(layer));
  }

  /**
   * Takes a rectangle and converts it's x, y, width, and height properties to an array
   * @param r a Rectangle instance to get properties of
   * @return returns an array of Integers
   */
  private Integer[] rectangleDimToArr(Rectangle r) {
    Integer[] dims = new Integer[4];

    dims[0] = (int) r.getX();
    dims[1] = (int) r.getY();
    dims[2] = (int) r.getWidth();
    dims[3] = (int) r.getHeight();

    return dims;
  }

  
  // getters
  public JDLayer getLayer(String layerName) {
    if (layerExists(layerName)) 
      return this.layers.get(layerName);
    else
      throw new IllegalArgumentException("Layer with name " + layerName + " does not exist.");
  }

  private JPanel getImagePanel(DrawingPanel dp) {
    // CHEATER, CHEATER, PUMPKIN EATER
    // gets the private field 'imagePanel' from the Drawing Panel instance
    // https://stackoverflow.com/questions/1196192/how-to-read-the-value-of-a-private-field-from-a-different-class-in-java
    
    Field f;
    try {
      f = DrawingPanel.class.getDeclaredField("imagePanel");
      f.setAccessible(true);
      return (JPanel) f.get(dp);
    } catch 
      (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
        e.printStackTrace();
      }
    
    return null;
  }

  private int parseLayerInt(String layerName) { return Integer.parseInt(layerName.split(" ")[1]); }
  public Boolean layerExists(String layerName) { return this.layers.containsKey(layerName); }

  public static void main(String[] args)  {  }
}
