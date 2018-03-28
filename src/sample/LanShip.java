package sample;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
public class LanShip implements Serializable{

    int r,c;
    public static enum TYPE{ONE,TWO,THREE,FOUR};
    Ship.TYPE type;
    boolean rotate = true;


    public LanShip(int r, int c, Ship.TYPE type,boolean rotate) {
        this.r = r;
        this.c = c;
        this.type = type;
        this.rotate = rotate;
    }
    public  List<Point> getAllPosition(){//Point {col:column,row:row}
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

    public static LanShip getShip(Point p,List<LanShip> ships){//Вернуть корабль по точке

        for (LanShip s:ships)
            if(s.getAllPosition().contains(p))
                return s;

        return null;
    }
}
