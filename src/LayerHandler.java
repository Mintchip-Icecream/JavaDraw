import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.swing.JPanel;

public class LayerHandler {
 
  BufferedImage gui;
  Map<String, JDLayer> layers = new HashMap<>();
  Graphics2D canvasG2;
  JDLayer currentLayer;

  public LayerHandler(int width, int height, DrawingPanel dp) {
    this.gui = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    buildUI();
    this.canvasG2 = dp.getGraphics();
    this.addListeners(dp);
  }

  private void buildUI() {

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
    if (this.currentLayer == null || !this.currentLayer.isSelected || !this.currentLayer.boundaryContainsXY(x, y)) return;

    this.currentLayer.mouseX = x;
    this.currentLayer.mouseY = y;
    this.currentLayer.canDrag = true;
  }

  private void handleMouseUp(int x, int y) {
    if (this.currentLayer == null || !this.currentLayer.isSelected) return;

    this.currentLayer.canDrag = false;
  }

  private void handleMouseDrag(int x, int y) {
    if (this.currentLayer == null || !this.currentLayer.canDrag) return;

    JDLayer l = this.currentLayer;
    int newX = l.x + (x - l.mouseX);
    int newY = l.y + (y - l.mouseY);

    this.canvasG2.setBackground(Color.WHITE);
    this.canvasG2.clearRect(0, 0, 800, 800);
    l.x = newX;
    l.y = newY;
    l.mouseX = x;
    l.mouseY = y;
    l.detectBoundary();
    this.refreshCanvas();
  }

  private void handleMouseMove(int x, int y, DrawingPanel dp)  {
    JPanel imgPnl = this.getImagePanel(dp);

    if (this.currentLayer == null || imgPnl == null) return; 
    
    if (this.currentLayer.isSelected && this.currentLayer.getBoundaryElAtXY(x, y) != null) {
      String boundaryEl = this.currentLayer.getBoundaryElAtXY(x, y);

      // https://stackoverflow.com/questions/7359189/how-to-change-the-mouse-cursor-in-java
      switch (boundaryEl) {
        case "NW":
          imgPnl.setCursor(new Cursor(Cursor.NW_RESIZE_CURSOR)); 
          break;
        case "NE":
          imgPnl.setCursor(new Cursor(Cursor.NE_RESIZE_CURSOR));
          break;
        case "SE":
          imgPnl.setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
          break;
        case "SW":
          imgPnl.setCursor(new Cursor(Cursor.SW_RESIZE_CURSOR));
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
    if ("sdr".indexOf(key) == -1 || this.currentLayer == null) return;
      
      JDLayer l = this.currentLayer;
     
      l.detectBoundary();
      if (key == 's') this.selectLayer(l.getName()); 
      if (key == 'd') this.deselectLayer(l.getName());
      if (key == 'r') {
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
  }

  public JDLayer addLayer(String layerName) {
    if (layerExists(layerName)) 
      throw new IllegalArgumentException("Layer with name " + layerName + " already exists.");
    else {
      JDLayer l = new JDLayer(layerName, 800, 800); // TODO: soft-code width/height
      
      this.layers.put(layerName, l);
      return l;  // return newly added layer for convience
    }
  }

  // add: delete layer

  public void selectLayer(String layerName) {
    JDLayer layer = this.getLayer(layerName);
    layer.select();
    this.currentLayer = layer;
    this.refreshCanvas();
  }

  public void deselectLayer(String layerName) {
    this.currentLayer.deselect();
    this.refreshCanvas();
  }

  private void drawLayer(JDLayer layer) {
    this.canvasG2.drawImage(layer, layer.x, layer.y, null);
    layer.detectBoundary();
  }


  private void refreshCanvas() {
    // TODO: soft-code color and width/height
    this.canvasG2.setBackground(Color.white);  
    this.canvasG2.clearRect(0, 0, 800, 800);
    this.layers.forEach((name, layer) -> this.drawLayer(layer));
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

 

  public Boolean layerExists(String layerName) { return this.layers.containsKey(layerName); }

  public static void main(String[] args) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
    final int TOTALLAYERS = 20;
    DrawingPanel dp = new DrawingPanel(800, 800);
    LayerHandler lh = new LayerHandler(1, 1, dp);
    Color[] colors = {Color.BLACK, Color.GREEN, Color.BLUE, Color.PINK, Color.GRAY, Color.RED};
    Random rand = new Random();
    
    // draw some random lines for testing
    for (int i = 0; i <= TOTALLAYERS/2; i++) {
      int x1 = rand.nextInt(800);
      int y1 = rand.nextInt(800);
      int x2 = rand.nextInt(800);
      int y2 = rand.nextInt(800);
      JDLayer l = lh.addLayer("Layer " + i);

      l.g2.setColor(colors[rand.nextInt(colors.length)]);
      l.g2.drawLine(x1, y1, x2, y2);
      lh.drawLayer(l);
    }

    // draw some random ovals for testing
    for (int i = TOTALLAYERS/2 + 1; i < TOTALLAYERS; i++) {
      int x = rand.nextInt(800);
      int y = rand.nextInt(800);
      int r = rand.nextInt(100) + 1;
      JDLayer l = lh.addLayer("Layer " + i);

      l.g2.setColor(colors[rand.nextInt(colors.length)]);
      l.g2.fillOval(x, y, r, r);
      lh.drawLayer(l);
    }

    
    //l2g2.drawString("\uD83D\uDDD1", 50, 200); // trashcan icon
    
    // get random layer for drag testing
    lh.currentLayer = lh.getLayer("Layer " + ((TOTALLAYERS/2)+2));

    dp.onMouseClick((x, y) -> System.out.println(x + " " + y));
    
  }
}
