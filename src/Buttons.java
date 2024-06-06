import java.awt.*;
public class Buttons {
    private int x;
    private int y;
    private int size;
    private Color color;
    Canvas canvas;
    Graphics g2;

    public Buttons(Canvas canvas,int inputx, int inputy, int inputsize, Color inputColor){
        this.g2=canvas.g2;
        x = inputx;
        y = inputy;
        size = inputsize;
        color = inputColor;
    }

    public void draw(){
        g2.setColor(Color.black);
        g2.drawRect(x,y,size,size);
        g2.setColor(color);
        g2.fillRect(x+2,y+2,size-2,size-2);
    }

    public boolean isPressed(){
        return Canvas.mousePressed && Canvas.mouseX >= x && Canvas.mouseX <= x + size && Canvas.mouseY >= y && Canvas.mouseY <=y+size;
    }

    public void update(){
        if (isPressed()){
            activate();
        }
    }
    public void activate(){
    }

    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public Color getColor(){
        return color;
    }
    public int getSize(){
        return size;
    }
}
