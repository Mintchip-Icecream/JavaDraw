import java.awt.*;

public class ToolBar {
    public enum toolBarType{
        pen,type,eraser,layers,select,line,colorpicker
    }
    Canvas canvas;
    toolBarType tBA;
    ColorButton colorbutton;
    Eraser eraser;
    Pen pen;
    String activebutton = "colorbutton";
    public ToolBar(Canvas canvas){
        this.canvas=canvas;
        this.colorbutton = new ColorButton(canvas,600,0,100, Color.black);
        colorbutton.draw();
        this.eraser = new Eraser(canvas,300,0,100, Color.black);
        eraser.draw();
        this.pen = new Pen(canvas,100,0,100, Color.black);
        pen.draw();
    }

    public void update(){
        if (colorbutton.isPressed()){
            colorbutton.update();
            eraser.deactivate();
            pen.deactivate();
            activebutton="colorbutton";
        }
        else if (eraser.isPressed()){
            eraser.update();
            colorbutton.deactivate();
            pen.deactivate();
            activebutton="eraser";
        }
        else if(pen.isPressed()){
            pen.update();
            activebutton="pen";
            eraser.deactivate();
            colorbutton.deactivate();
        }
    }

}
