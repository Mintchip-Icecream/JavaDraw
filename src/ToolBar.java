import java.awt.*;

public class ToolBar {
    public enum toolBarType{
        pen,type,eraser,layers,select,line,colorpicker
    }
    Canvas canvas;
    toolBarType tBA;
    ColorButton colorbutton;

    public ToolBar(Canvas canvas){
        this.canvas=canvas;
        this.colorbutton = new ColorButton(canvas,300,0,100, Color.black);
        colorbutton.draw();
    }
    // toolbar gets updated, which looks at the boolean if the mouse is on the taskbar, if it's on the taskbar it'll update all of the buttons (this is to reduce the amount of memory it consumes per second)

    public void update(){
        if (Canvas.mousePressed){
            colorbutton.update();
        }
    }

}
