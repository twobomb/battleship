package sample;


import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Ship {

    int w,h;
    int r,c;
    Image texture;
    public static enum TYPE{ONE,TWO,THREE,FOUR};
    TYPE type;
    BattleGrid bg;
    boolean rotate = true;

    public Ship(int r, int c, TYPE type,BattleGrid bg) throws IOException {
        this.r = r;
        this.c = c;
        this.type = type;
        FileInputStream file = null;
        this.bg = bg;
        switch (type){
            case ONE:
                file = new FileInputStream("C:\\Users\\twobomb\\Desktop\\c1.png");break;
            case TWO:
                file = new FileInputStream("C:\\Users\\twobomb\\Desktop\\c2.png");break;
            case THREE:
                file = new FileInputStream("C:\\Users\\twobomb\\Desktop\\c3.png");break;
            case FOUR:
                file = new FileInputStream("C:\\Users\\twobomb\\Desktop\\c4.png");break;
        }
        texture = new Image(file);
        file.close();

    }
    public boolean isDestroy(){
        for(Point p:getAllPosition())
            if(!bg.shoots.contains(p))
                return false;
        return true;
    }
    public void setRow(int r) {
        this.r = r;
    }

    public void setColumn(int c) {
        this.c = c;
    }

    public List<Point> getAllPosition(){//Point {x:column,y:row}
        List<Point> points = new ArrayList<>();
        points.add(new Point(c,r));

        int w = 0;
        if(rotate)
            switch (type){
                case TWO: w = 1;break;
                case THREE: w = 2;break;
                case FOUR: w = 3;break;
            }
        if(w > 0)
            for(int i = 1; i <= w;i++)
                points.add(new Point(c+i,r));
        int h = 0;
        if(!rotate)
            switch (type){
                case TWO: h = 1;break;
                case THREE: h = 2;break;
                case FOUR: h = 3;break;
            }
        if(h > 0)
            for(int i = 1; i <= h;i++)
                points.add(new Point(c,r+i));
            return points;

    }
    public double getWidth(){
        int w = 1;
        if(rotate)
            switch (type){
                case TWO: w = 2;break;
                case THREE: w = 3;break;
                case FOUR: w = 4;break;
            }
            return w;
    }
    public double getHeight(){
        int h = 1;
        if(!rotate)
            switch (type){
                case TWO: h = 2;break;
                case THREE: h = 3;break;
                case FOUR: h = 4;break;
            }
            return h;
    }
    public void moveTo(double x, double y){
        int w = (int) (getWidth()-1);
        int h = (int) (getHeight()-1);
        Point p = findNearCenter(new Point(x,y));
        if(p.x+w >= bg.getColumn())
            p.x = bg.getColumn() - w - 1;
        if(p.y + h >= bg.getRows())
            p.y = bg.getRows() - h - 1;
        c = (int) p.x;
        r = (int) p.y;
    }
    public double getDistance(Point p1,Point p2){
        return Math.sqrt(Math.pow(p2.x-p1.x,2)+Math.pow(p2.y-p1.y,2));
    }
    public Point findNearCenter(Point p1){//Возвращает row,col ближайшие к точке p1
        List<Point> centras =  new ArrayList<>();
        double offX = (bg.getCellWidth()/2);
        double offY = (bg.getCellHeight()/2);
        for(int col = 0; col < bg.getColumn();col++)
            for(int row = 0; row < bg.getRows();row++)
                centras.add(new Point(col*bg.getCellWidth()+offX,row*bg.getCellHeight()+offY));
        double min =  getDistance(p1,centras.get(0));
        Point minPoint = centras.get(0);
        for(Point p2:centras)
            if(min >= getDistance(p1,p2)){
                min = getDistance(p1,p2);
                minPoint = p2;
            }
        return new Point((minPoint.x-offX)/bg.getCellWidth(),(minPoint.y-offY)/bg.getCellHeight());
    }
    public void draw(GraphicsContext ctx){

            double x = (bg.getX() + bg.getCellWidth()*c);
            double  y =  (bg.getY() + bg.getCellHeight()*r);
            double  w = bg.getCellWidth();
            double  h = bg.getCellHeight();
            switch (type){
                case TWO:h*=2;break;
                case THREE:h*=3;break;
                case FOUR:h*=4;break;
            }
            if(rotate){
                ctx.save();
                ctx.translate(x+w/2,y+bg.getCellHeight()/2);
                ctx.rotate(-90);
                ctx.drawImage(texture,-w/2,-bg.getCellHeight()/2,w,h);
                ctx.restore();
            }
            else
                ctx.drawImage(texture,x,y,w,h);
    }
}
