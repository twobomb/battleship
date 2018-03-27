package sample;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.ArcType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class BattleGrid {

    double x,y,w,h;//int
    int r = 10,c = 10;
    List<Ship> ships = new ArrayList<>();
    List<Point> shoots = new ArrayList<>();
    public BattleGrid(double x,double y,double w,double h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }
    public Ship getShip(Point p,List<Ship> ships){
        for (Ship s:ships)
            if(s.getAllPosition().contains(p))
                return s;

        return null;
    }
    public void generateScene() throws IOException {// one - 4, two - 3 , three -2, four - 1
        ships.clear();

        while (true) {
            Ship four = new Ship(getRand(0, getRows() - 1), getRand(0, getColumn() - 1), Ship.TYPE.FOUR, this);
            four.rotate = getRand(0,100)%2 == 0;
            if(!checkCollision(four)) {
                ships.add(four);
                break;
            }
        }
        int threeCnt = 2;
        while (true) {
            Ship three = new Ship(getRand(0, getRows() - 1), getRand(0, getColumn() - 1), Ship.TYPE.THREE, this);
            three.rotate = getRand(0,100)%2 == 0;
            if(!checkCollision(three)) {
                ships.add(three);
                threeCnt--;
            }
            if(threeCnt == 0)
                break;
        }
        int twoCnt = 3;
        while (true) {
            Ship two = new Ship(getRand(0, getRows() - 1), getRand(0, getColumn() - 1), Ship.TYPE.TWO, this);
            two.rotate = getRand(0,100)%2 == 0;
            if(!checkCollision(two)) {
                ships.add(two);
                twoCnt--;
            }
            if(twoCnt== 0)
                break;
        }
        int oneCnt = 4;
        while (true) {
            Ship one = new Ship(getRand(0, getRows() - 1), getRand(0, getColumn() - 1), Ship.TYPE.ONE, this);
            one.rotate = getRand(0,100)%2 == 0;
            if(!checkCollision(one)) {
                ships.add(one);
                oneCnt--;
            }
            if(oneCnt== 0)
                break;
        }

    }
    public void shoot(Point p){
        shoots.add(p);
    }
    public void botShoot(){
        Ship damagedShip = null;
        Point damageFirstPoint = new Point(0,0);
        for(Ship s:ships){
            if(s.isDestroy())continue;
            for(Point p:shoots)
                if(s.getAllPosition().contains(p)) {
                    damagedShip = s;
                }
            if(damagedShip != null)
                break;
        }
        int damage = 0;
        if(damagedShip != null){
            for(Point p:shoots)
                if(damagedShip.getAllPosition().contains(p)) {
                    damage++;
                    damageFirstPoint = p;
                }
        }
        Point shoot = new Point(0,0);
        if(damagedShip != null)
            while (true) {
                if (damage == 1) {
                    shoot.x = damageFirstPoint.x;
                    shoot.y = damageFirstPoint.y;
                    if(getRand(0,100)%2 == 0)
                        shoot.x += getRand(0,100)%2 == 0?-1:1;
                    else
                        shoot.y += getRand(0,100)%2 == 0?-1:1;
                }
                System.out.println(shoot);
                if(!shoots.contains(shoot) && shoot.x >= 0 && shoot.x < c && shoot.y >= 0 && shoot.y < r)
                    break;
            }
        else{
            while (true) {
                shoot = new Point(getRand(0, c - 1), getRand(0, r - 1));
                if(!shoots.contains(shoot))
                    break;
            }
        }
        shoot(shoot);

    }
    public boolean contains(Point p){
        return getX() <= p.x && getX() + getCellWidth()*getColumn() >= p.x &&
                getY() <= p.y && getY() + getCellHeight()*getRows() >= p.y;

    }
    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setWidth(int w) {
        this.w = w;
    }

    public void setHeight(int h) {
        this.h = h;
    }

    public boolean checkCollision(Ship checkS){
        for(Point p:checkS.getAllPosition())
            if(p.x >= c || p.y >= r)
                return true;
        for(Ship s:ships){
            if(s.equals(checkS))continue;
            for(Point p:checkS.getAllPosition())
                if(s.getAllPosition().contains(p))
                    return true;
        }
        return false;
    }

    public boolean checkCollisionPoint(Point p){
        if(p.x >= c || p.y >= r)
            return false;
        for(Ship s:ships){
            if(s.getAllPosition().contains(p))
                return true;
        }
        return false;
    }
    public int getRand(int min,int max){
        if(min < 0){
            int tmin = 0;
            int tmax = max + Math.abs(min);
            int trand = (int) Math.round(Math.random() * (tmax+tmin) - tmin);
            trand+=min;
            return trand;
        }
        return (int) Math.round(Math.random() * (max+min) - min);

    }
    public Point getNearCell(double x,double y){

        List<Point> centras =  new ArrayList<>();
        double offX =  (getCellWidth()/2);
        double offY =  (getCellHeight()/2);
        for(int col = 0; col < getColumn();col++)
            for(int row = 0; row < getRows();row++)
                centras.add(new Point(col*getCellWidth()+offX,row*getCellHeight()+offY));
        double min =  getDistance(new Point(x,y),centras.get(0));
        Point minPoint = centras.get(0);
        for(Point p2:centras)
            if(min >= getDistance(new Point(x,y),p2)){
                min = getDistance(new Point(x,y),p2);
                minPoint = p2;
            }
        return new Point(Math.round((minPoint.x-offX)/getCellWidth()),Math.round((minPoint.y-offY)/getCellHeight()));
    }
    public double getDistance(Point p1,Point p2){
        return Math.sqrt(Math.pow(p2.x-p1.x,2)+Math.pow(p2.y-p1.y,2));
    }
    public int getColumn() {
        return c;
    }

    public int getRows() {
        return r;
    }
    public double getCellWidth(){
        return w/getColumn();
    }
    public double getCellHeight(){
        return h/getRows();
    }

    public double  getX() {
        return x;
    }

    public double  getY() {
        return y;
    }

    public void draw(GraphicsContext ctx){
        ctx.save();
        ctx.translate(x,y);
        ctx.beginPath();
        for(int i = 0; i <= c;i++) {
            ctx.moveTo(i*w/c,0);
            ctx.lineTo(i*w/c,h);
        }
        for(int i = 0; i <= r;i++) {
            ctx.moveTo(0,i*h/r);
            ctx.lineTo(w,i*h/r);
        }
        ctx.closePath();
        ctx.stroke();

        ctx.restore();

        for(Ship s:ships)
            s.draw(ctx);

        ctx.save();
        ctx.translate(x,y);
        for(Point p:shoots){
            if(checkCollisionPoint(p))
                ctx.fillRect(getCellWidth()*p.x,getCellHeight()*p.y,getCellWidth(),getCellHeight());
            else
                ctx.fillArc(getCellWidth()*p.x+getCellWidth()/3,getCellHeight()*p.y + getCellHeight()/3,getCellWidth()-getCellWidth()/1.5,getCellHeight()-getCellHeight()/1.5,0,360, ArcType.ROUND);
        }
        ctx.restore();
    }
}
class Point{
    public double x,y;
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Point)
            if(((Point) obj).x == this.x && ((Point) obj).y == this.y)
                return true;
        return super.equals(obj);
    }

    @Override
    public String toString(){
        return "{ x: "+x+" y: "+y+" }";
    }
}
