import java.awt.*;

public class ToolBar {
    public enum toolBarType{
        pen,type,eraser,layers,select,line,colorpicker
    }
    Canvas canvas;
    Graphics2D g2;
    toolBarType tBA;
    ColorButton colorbutton;
    AdjustValButton rButton, bButton, gButton, addPenSizeButton, subtractPenSizeButton, penSizeButton, addLayerButton,subtractLayerButton, countingLayerButton;
    Eraser eraser;
    Pen pen;
    Color CurrentColor = new Color(0,0,0);
    public static int penSize = 2;
    public static int LayerCount;
    Color red = new Color(255,122,122); Color green = new Color(122,255,122); Color blue = new Color(122,122,255);

    public ToolBar(Canvas canvas){
        this.canvas=canvas;
        this.g2=canvas.g2;
        this.pen = new Pen(canvas,50,0,100, Color.black);
        this.addPenSizeButton= new AdjustValButton(canvas, 250, 0, 50, Color.white);
        this.subtractPenSizeButton= new AdjustValButton(canvas, 250, 50, 50, Color.white);
        this.penSizeButton = new AdjustValButton(canvas, 150,0,100,Color.white);
        this.eraser = new Eraser(canvas,300,0,100, Color.black);
        this.colorbutton = new ColorButton(canvas,400,0,100, Color.black);
        this.rButton = new AdjustValButton(canvas, 500, 0, 100, red);
        this.gButton = new AdjustValButton(canvas, 600, 0, 100, green);
        this.bButton = new AdjustValButton(canvas, 700, 0, 100, blue);
        this.countingLayerButton = new AdjustValButton(canvas, 800,0, 100, Color.white); // displays which layer we're on
        this.addLayerButton = new AdjustValButton(canvas, 900, 0, 50, Color.white);
        this.subtractLayerButton = new AdjustValButton(canvas, 900, 50, 50, Color.white);
        bButton.draw();
        gButton.draw();
        rButton.draw();
        colorbutton.draw();
        eraser.draw();
        pen.draw();
        penSizeButton.draw("Size: " + penSize, 50, Color.black);
        addLayerButton.draw("+", 25, Color.black);
        subtractLayerButton.draw("-", 75, Color.black);
        countingLayerButton.draw("Layer: " + LayerCount, 50, Color.black);
        subtractPenSizeButton.draw("-", 75, Color.black);
        addPenSizeButton.draw("+", 25, Color.black);



    }
    // toolbar gets updated, which looks at the boolean if the mouse is on the taskbar, if it's on the taskbar it'll update all of the buttons (this is to reduce the amount of memory it consumes per second)

    public void update(){
        if (Canvas.mousePressed){
            pen.update();
            eraser.update();
            addLayerButton.addValue();
            subtractLayerButton.addValue();
            addPenSizeButton.addValue();
            subtractPenSizeButton.addValue();
            clickChange();
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
        if (penSize == 0){penSize++;}
        colorbutton.changeColor(CurrentColor);
        g2.setColor(Color.white);
        g2.drawString("R: " +String.valueOf(rButton.getValue()), 425, 45);
        g2.drawString("G: " +String.valueOf(gButton.getValue()), 425, 65);
        g2.drawString("B: " + String.valueOf(bButton.getValue()), 425, 85);
        penSizeButton.draw("Size: "+String.valueOf(penSize), 50, Color.black);
    }
    public void clickChange(){
        LayerCount = addLayerButton.getValue()-subtractLayerButton.getValue();
        if (LayerCount <= 0){LayerCount = 1; subtractLayerButton.setValue(0);}
        countingLayerButton.draw("Layer: " + String.valueOf(LayerCount), 50, Color.black);

        int pensizebuttoncombined = addPenSizeButton.getValue()-subtractPenSizeButton.getValue();;
        penSize = penSize + pensizebuttoncombined;
        System.out.println(pensizebuttoncombined);
        if (penSize<= 0){penSize = 1;}
        addPenSizeButton.setValue(1);
        subtractPenSizeButton.setValue(1);
        penSizeButton.draw("Size: "+String.valueOf(penSize), 50, Color.black);

        pen.checkActive();
        eraser.checkActive();
    }

}
