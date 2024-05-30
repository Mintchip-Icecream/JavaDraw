import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

public class JDLayer extends BufferedImage {
  int x = 0, y = 0, mouseX, mouseY;
  Boolean wasDragged = false;
  
  public JDLayer(int width, int height) {
    super(width, height, BufferedImage.TYPE_INT_ARGB);
  }

 

  public static void main(String[] args) {
    DrawingPanel dp = new DrawingPanel(800, 800);
    JDLayer l = new JDLayer(800, 800);
    JDLayer l2 = new JDLayer(800, 800);
    Graphics2D dpg2 = dp.getGraphics();
    Graphics2D lg2 = l.createGraphics();
    Graphics2D l2g2 = l2.createGraphics();

    dp.setBackground(Color.WHITE);
    lg2.setColor(Color.BLACK);
    l2g2.setColor(Color.RED);
    lg2.drawLine(5, 10, 500, 700);
    l2g2.drawLine(20, 500, 400, 20);
    
    dpg2.drawImage(l, 0, 0, dp);
    dpg2.drawImage(l2, 0, 0, dp);
   
    dp.onMouseDown((x, y) -> {
      l2.mouseX = x;
      l2.mouseY = y;
    });

    dp.onMouseDrag((x, y) -> {
      int newX = l2.x + (x - l2.mouseX);
      int newY = l2.y + (y - l2.mouseY);
      Color bg = Color.WHITE;

      dpg2.clearRect(0, 0, 800, 800);
      dpg2.setBackground(bg);
      dpg2.drawImage(l, l.x, l.y, dp);
      dpg2.drawImage(l2, newX, newY, dp);
      l2.x = newX;
      l2.y = newY;
      l2.mouseX = x;
      l2.mouseY = y;
    });
    
  }
}
