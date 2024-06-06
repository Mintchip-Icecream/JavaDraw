import java.awt.*;
public class Buttons {
    private int x;
    private int y;
    private int size;
    private Color color;
    Canvas canvas;
    Graphics g2;
    ////for Mr. Forkner, the code for this specific button references this video (this class is the only thing borrowed from outside sources as far as I know): https://youtu.be/MHhFTqAHiOA?si=c74su5JJdWTdQWH2

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
    public void draw(String text, int yValue, Color color2){
        draw();
        g2.setColor(color2);
        g2.drawString(text, getX()+25, yValue);
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
