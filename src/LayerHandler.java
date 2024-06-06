import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.swing.JPanel;

public class LayerHandler {
 
  Map<String, JDLayer> layers = new HashMap<>();
  Graphics2D canvasG2;
  JDLayer selectedLayer;
  Rectangle canvasBounds;
  

  public LayerHandler(DrawingPanel dp, Rectangle canvasBoundary) {
    this.canvasG2 = dp.getGraphics();
    this.canvasBounds = canvasBoundary;
    this.addListeners(dp);
  }

  private void addListeners(DrawingPanel dp) {
    dp.onMouseClick((x,y) -> this.handleMouseClick(x, y));
    dp.onMouseDown((x, y) -> this.handleMouseDown(x, y));
    dp.onMouseUp((x, y) -> this.handleMouseUp(x, y));
    dp.onMouseDrag((x, y) -> this.handleMouseDrag(x, y));
    dp.onMouseMove((x, y) -> this.handleMouseMove(x, y, dp));
    dp.onKeyUp(key -> this.handleKeyPress(key));

  }

  // mouse event handlers
  private void handleMouseClick(int x, int y) {
    System.out.println("mouse clicked");
  }

  private void handleMouseDown(int x, int y) {
    //System.out.println(this.selectedLayer == null);
    //System.out.println(this.selectedLayer.isSelected);
    //System.out.println(this.selectedLayer.boundaryContainsXY(x, y));

    if (this.selectedLayer == null || !this.selectedLayer.isBoundaryOn || !this.selectedLayer.boundaryContainsXY(x, y)) return;
    System.out.println("lh drag");
    JDLayer l = this.selectedLayer;
    
    l.mouseX = x;
    l.mouseY = y;
    l.canDrag = true;
   // System.out.println(l.getX() + " " + l.getY());
  }

  private void handleMouseUp(int x, int y) {
    if (this.selectedLayer == null || !this.selectedLayer.isSelected) return;

    JDLayer l = this.selectedLayer;

    //System.out.println(l.getX() + " " + l.getY());
    if (l.getX() != 0 && l.getY() != 0) {
      //l.refresh();
      //this.refreshCanvas();
    }

    this.selectedLayer.canDrag = false;
  }

  private void handleMouseDrag(int x, int y) {
    
   
    if ( 
      this.selectedLayer == null || 
      !this.selectedLayer.canDrag
      
    )
      return;

      //System.out.println(this.canvasBounds.getBounds());
      //System.out.println(this.selectedLayer.getBoundary().getBounds());
      //System.out.println(this.canvasBounds.contains(this.selectedLayer.getBoundary()));      
    System.out.println("dragging layer");
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

  // key press handlers
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

  // NOTE: double recursion with renameLayer
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

  // NOTE: double recursion with deleteLayer
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

  public void selectLayer(String layerName, Boolean showBoundary) {
    JDLayer layer = this.getLayer(layerName);
    layer.select(showBoundary);
    this.selectedLayer = layer;
    this.refreshCanvas();
  }

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

  public void deselectLayer(String layerName) {
    this.selectedLayer.deselect();
    this.refreshCanvas();
  }

  private void drawLayer(JDLayer layer) {
    //this.canvasG2.drawImage(layer, (int) this.canvasBounds.getX(), (int) this.canvasBounds.getY(), null);
    this.canvasG2.drawImage(layer, layer.x, layer.y, null);
  }

  public void drawSelectedLayer() {
    this.drawLayer(selectedLayer);
  }


  public void refreshCanvas() {
    Integer[] cb = this.rectangleDimToArr(this.canvasBounds);

    this.canvasG2.setBackground(Color.WHITE);
    this.canvasG2.clearRect(cb[0], cb[1], cb[2], cb[3]);
    this.layers.forEach((name, layer) -> this.drawLayer(layer));
  }

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

  private int parseLayerInt(String layerName) { return Integer.valueOf(layerName.split(" ")[1]); }
  public Boolean layerExists(String layerName) { return this.layers.containsKey(layerName); }

  public static void main(String[] args) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
    final int TOTALLAYERS = 20;
    DrawingPanel dp = new DrawingPanel(800, 800);
    LayerHandler lh = new LayerHandler(dp, new Rectangle(0, 0, 800, 800));
    Color[] colors = {Color.BLACK, Color.GREEN, Color.BLUE, Color.PINK, Color.GRAY, Color.RED};
    Random rand = new Random();
    
    // draw some random lines for testing
    for (int i = 0; i <= TOTALLAYERS/2; i++) {
      int x1 = rand.nextInt(800);
      int y1 = rand.nextInt(800);
      int x2 = rand.nextInt(800);
      int y2 = rand.nextInt(800);
      JDLayer l = lh.addLayer();

      l.g2.setColor(colors[rand.nextInt(colors.length)]);
      l.g2.drawLine(x1, y1, x2, y2);
      lh.drawLayer(l);
    }

    // draw some random ovals for testing
    for (int i = TOTALLAYERS/2 + 1; i < TOTALLAYERS; i++) {
      int x = rand.nextInt(800);
      int y = rand.nextInt(800);
      int r = rand.nextInt(100) + 1;
      JDLayer l = lh.addLayer();

      l.g2.setColor(colors[rand.nextInt(colors.length)]);
      l.g2.fillOval(x, y, r, r);
      lh.drawLayer(l);
    }

    
    //l2g2.drawString("\uD83D\uDDD1", 50, 200); // trashcan icon
    
    // get random layer for drag testing
    //lh.selectedLayer = lh.getLayer("Layer " + ((TOTALLAYERS/2)+2));

    //System.out.println(lh.layers.keySet().toString());
    //System.out.println(lh.layers.size());
    //lh.deleteLayer("Layer 12");
    //System.out.println(lh.layers.keySet().toString());
    //System.out.println(lh.layers.size());


    
  }
}
