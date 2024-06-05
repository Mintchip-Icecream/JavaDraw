import java.awt.*;
import java.awt.event.*;

public class Canvas  {
    public static int width = Main.canvasWidth;
    public static int height = Main.canvasHeight;
    public static int initialx = 0; //used for mousedown events to set the initial coords for starting shapes (for buyan)
    public static int initialy = 0;
    public static int mouseX = 0; //main purpose for detecting when mouse is clicking buttons or is on toolbar
    public static int mouseY = 0;
    public static Color canvasColor = new Color(255,255,255);
    public static boolean mousePressed = false; //checked by button class to tell whether to activate or not
    public static boolean onToolBar = false; //probably not needed, just check if (mouseY < 100)
    public DrawingPanel panel = new DrawingPanel(1600, 1000);
    public Graphics2D g2 = panel.getGraphics();
    LayerHandler lh; // layer handler, when stuff is drawn on canvas it has to be drawn through the LayerHandler instance (ex. lh.g2.drawRect())
    ToolBar tb;


    public Canvas(){
        panel.setBackground(Color.darkGray);
        g2.setColor(canvasColor);
        g2.fillRect(0,100,width,height); //this is the canvas, art must fall between (x=0), (y=100) and (x=width), (y=height)
        this.lh=new LayerHandler(1,1,panel);;
        this.tb = new ToolBar(this);
        panel.onMouseDown((x,y)-> changeInitialCoords(x,y));
        panel.onDrag((x,y) -> penDraw(x,y)); // on penDraw just for testing purposes, will add a check for the tool we're on later
        panel.onMouseUp((x, y) -> mouseRelease());

    }

    public void changeInitialCoords(int x, int y){
        initialx = x;
        mouseX= x;
        initialy = y;
        mouseY = y;
        System.out.println(x + " " + y);
        mousePressed = true;
        System.out.println("mousepressed = true"); //testing purpose
        if (y < 100){ tb.update();} //VERY IMPORTANT, will update all the buttons to see if they're pressed, and if they're pressed they'll activate
        if(tb.activebutton.equals("eraser")) {
            tb.eraser.updateGraphics(g2);
        } else if (tb.activebutton.equals("pen")) {
            tb.pen.updateGraphics(g2);
        }
    }
    public void mouseRelease(){
        mousePressed = false;
    }
    public void penDraw(int x, int y){ //this method is mainly just for testing tool functionality, all methods will fall under one large onDrag update method
        mousePressed = true;
        mouseX = x;
        mouseY = y;
        if (x<=width && y <= height+100 && y >= 100){
            g2.fillOval(x, y, 5, 5);
        }
        // ex of a layerhandler usage: "lh.currentLayer.g2.setColor(Color.red);"
    }
    public void makeRect(int x, int y){ //not in use, just an experiment for a tool to make a rectangle
        g2.setColor(Color.red);
        if(x < initialx){
            if(y<initialy){
                g2.fillRect(x,y,initialx-x,initialy-y);
                System.out.println("opt 1");
            } else {
                g2.fillRect(x, initialy, initialx - x, y-initialy);
                System.out.println("opt 2");
            }
        }
        else if (y<initialy){
            if(x < initialx){
                g2.fillRect(x, y,initialx-x,initialy-y);
                System.out.println("opt 3");
            }else {
                g2.fillRect(initialx, y, x-initialx, initialy-y);
                System.out.println("opt 4");
            }
        }
        else{
            if (initialy != 0) {
                g2.fillRect(initialx, initialy, x - initialx, y - initialy);
            }
        }
    }


}
