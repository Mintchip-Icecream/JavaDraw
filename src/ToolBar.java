import java.awt.*;

public class ToolBar {
    public enum toolBarType{
        pen,type,eraser,layers,select,line,colorpicker
    }
    Canvas canvas;
    Graphics2D g2;
    toolBarType tBA;
    ColorButton colorbutton;
    AdjustRGButton rButton, bButton, gButton, penSizeButton;
    Eraser eraser;
    Pen pen;
    Color CurrentColor = new Color(0,0,0);
    public static int penSize = 2;
    Color red = new Color(255,122,122); Color green = new Color(122,255,122); Color blue = new Color(122,122,255);

    public ToolBar(Canvas canvas){
        this.canvas=canvas;
        this.g2=canvas.g2;
        this.rButton = new AdjustRGButton(canvas, 500, 0, 100, red);
        this.gButton = new AdjustRGButton(canvas, 600, 0, 100, green);
        this.bButton = new AdjustRGButton(canvas, 700, 0, 100, blue);
        this.colorbutton = new ColorButton(canvas,400,0,100, Color.black);
        this.eraser = new Eraser(canvas,300,0,100, Color.black);
        this.pen = new Pen(canvas,100,0,100, Color.black);
        this.penSizeButton = new AdjustRGButton(canvas, 200,0,100,Color.white);
        bButton.draw();
        gButton.draw();
        rButton.draw();
        colorbutton.draw();
        eraser.draw();
        pen.draw();
        penSizeButton.draw("Size: " + penSize);



    }
    // toolbar gets updated, which looks at the boolean if the mouse is on the taskbar, if it's on the taskbar it'll update all of the buttons (this is to reduce the amount of memory it consumes per second)

    public void update(){
        if (Canvas.mousePressed){
            pen.update();
            eraser.update();
        }
    }
    public void dragUpdate(){
        if (Canvas.mousePressed){
            rButton.update();
            gButton.update();
            bButton.update();
            penSizeButton.update();
            change();
        }
    }

    public void change(){
        CurrentColor = new Color(rButton.getValue(), gButton.getValue(), bButton.getValue());
        penSize = penSizeButton.getValue();
        colorbutton.changeColor(CurrentColor);
        g2.setColor(Color.white);
        g2.drawString("R: " +String.valueOf(rButton.getValue()), 425, 45);
        g2.drawString("G: " +String.valueOf(gButton.getValue()), 425, 65);
        g2.drawString("B: " + String.valueOf(bButton.getValue()), 425, 85);
        penSizeButton.draw("Size: "+String.valueOf(penSizeButton.getValue()));
    }

}
