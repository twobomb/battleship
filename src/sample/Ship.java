package sample;


import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

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
        String file = null;
        this.bg = bg;
        switch (type){
            case ONE:
                file = getClass().getResource("c1.png").toString();break;
            case TWO:
                file = getClass().getResource("c2.png").toString();break;
            case THREE:
                file = getClass().getResource("c3.png").toString();break;
            case FOUR:
                file = getClass().getResource("c4.png").toString();break;
        }
        texture = new Image(file);


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

    public List<Point> getAllPosition(){//Point {col:column,row:row}
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
        if(p.col +w >= bg.getColumn())
            p.col = bg.getColumn() - w - 1;
        if(p.row + h >= bg.getRows())
            p.row = bg.getRows() - h - 1;
        c = (int) p.col;
        r = (int) p.row;
    }
    public double getDistance(Point p1,Point p2){
        return Math.sqrt(Math.pow(p2.col -p1.col,2)+Math.pow(p2.row -p1.row,2));
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
        return new Point((minPoint.col -offX)/bg.getCellWidth(),(minPoint.row -offY)/bg.getCellHeight());
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
                if(isDestroy()) {
                    ctx.setGlobalAlpha(.5);
                    ctx.setFill(Color.RED);
                    ctx.fillRect(-w/2,-bg.getCellHeight()/2,w,h);
                    ctx.setGlobalAlpha(1);
                }
                ctx.restore();
            }
            else {
                ctx.drawImage(texture, x, y, w, h);
                if(isDestroy()) {
                    ctx.setGlobalAlpha(.5);
                    ctx.setFill(Color.RED);
                    ctx.fillRect(x,y,w,h);
                    ctx.setGlobalAlpha(1);
                }
            }
    }
}
