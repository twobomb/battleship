package sample;

import com.sun.org.apache.bcel.internal.generic.SWITCH;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.beans.EventHandler;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

interface onGameOver{
    public void isOver();
}
public class BattleGrid {

    public enum LEVEL{EASY,NORMAL,HARD,EXPERT};//Уровни сложности
    double x,y,w,h;
    public List<onGameOver> onGameOverListener = new ArrayList<>();
    int miss = 0;//Промахи
    LEVEL level = LEVEL.EXPERT;//Уровень сложности данного поля боя
    int r = 10,c = 10;//Размер поля
    List<Ship> ships = new ArrayList<>();//Корабли данного поля
    List<Point> shoots = new ArrayList<>();//Выстрелы по данному полю
    Image krest;
    public boolean DRAW_SHIPS = true,DRAW_SHOOTS = true,DRAW_GRID = true,DRAW_DESTROY_SHIP = true,DRAW_NUMBERS = true;
    public BattleGrid(double x,double y,double w,double h) throws IOException {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        //FileInputStream file = new FileInputStream("C:\\Users\\twobomb\\Desktop\\battleship\\src\\sample\\kr.png");
        krest = new Image(getClass().getResource("kr.png").toString());
        //file.close();
    }
    public Ship getShip(Point p,List<Ship> ships){//Вернуть корабль по точке

        for (Ship s:ships)
            if(s.getAllPosition().contains(p))
                return s;

        return null;
    }

    public boolean checkPosition(Ship checkShip){//Возвращает true если впритык стоит корабль
        for(Point p:checkShip.getAllPosition()){
            if(checkCollisionPoint(new Point(p.col+1,p.row)) || checkCollisionPoint(new Point(p.col-1,p.row)) || checkCollisionPoint(new Point(p.col,p.row+1)) || checkCollisionPoint(new Point(p.col,p.row-1)))
                return true;
        }
        if(checkCollision(checkShip))
            return true;
        return false;
    }
    public void generateScene() throws IOException {// one - 4, two - 3 , three -2, four - 1, случайная расстанока
        ships.clear();

        while (true) {
            Ship four = new Ship(getRand(0, getRows() - 1), getRand(0, getColumn() - 1), Ship.TYPE.FOUR, this);
            four.rotate = getRand(0,100)%2 == 0;
            if(!checkPosition(four)) {
                ships.add(four);
                break;
            }
        }
        int threeCnt = 2;
        while (true) {
            Ship three = new Ship(getRand(0, getRows() - 1), getRand(0, getColumn() - 1), Ship.TYPE.THREE, this);
            three.rotate = getRand(0,100)%2 == 0;
            if(!checkPosition(three)) {
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
            if(!checkPosition(two)) {
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
            if(!checkPosition(one)) {
                ships.add(one);
                oneCnt--;
            }
            if(oneCnt== 0)
                break;
        }

    }
    public Point getNiceShoot(){//Чит для ботов, который вернет позицию не подбитого коробля
      //  System.out.println(level);
        for(Ship s:ships)
            for(Point p:s.getAllPosition())
                if(!shoots.contains(p))
                    return p;
        return null;
    }
    public void shoot(Point p){//Выстрел
        shoots.add(p);
        if(isOver()) {
            for (onGameOver l : onGameOverListener)
                l.isOver();
        }

    }
    public boolean isOver(){
        for(Ship s:ships)
            if(!s.isDestroy())
                return false;
        return true;
    }
    public Point botShoot(){//Выстрел бота
        if(isOver())
            return null;

        Ship damagedShip = null;
        Point damageFirstPoint = new Point(0,0);
        for(Ship s:ships){
            if(s.isDestroy())continue;
            for(Point p:shoots)
                if(s.getAllPosition().contains(p)) {
                    damagedShip = s;
                    damageFirstPoint = p;
                }
            if(damagedShip != null)
                break;
        }
        int damage = 0;
        if(damagedShip != null){
            for(Point p:shoots)
                if(damagedShip.getAllPosition().contains(p)) {
                    damage++;
                }
        }
        Point shoot = new Point(0,0);
        if(damagedShip != null)
            while (true) {
                if (damage == 1) {
                    shoot.col = damageFirstPoint.col;
                    shoot.row = damageFirstPoint.row;
                    if(getRand(0,100)%2 == 0)
                        shoot.col += getRand(0,100)%2 == 0?-1:1;
                    else
                        shoot.row += getRand(0,100)%2 == 0?-1:1;
                }
                else if(damage > 1){
                    if(damagedShip.rotate){
                        for(int i = 0; i < c-1;i++)
                            if(shoots.contains(new Point(i+1,damagedShip.r)) && damagedShip.equals(getShip(new Point(i+1,damagedShip.r),ships)) && !shoots.contains(new Point(i,damagedShip.r))) {
                                shoot = new Point(i, damagedShip.r);
                                break;
                            }
                            else if (shoots.contains(new Point(i,damagedShip.r)) && damagedShip.equals(getShip(new Point(i,damagedShip.r),ships)) && !shoots.contains(new Point(i+1,damagedShip.r))) {
                                shoot = new Point(i + 1, damagedShip.r);
                                break;
                            }
                    }
                    else {
                        for(int i = 0; i < r-1;i++)
                            if(shoots.contains(new Point(damagedShip.c,i+1)) && damagedShip.equals(getShip(new Point(damagedShip.c,i+1),ships)) && !shoots.contains(new Point(damagedShip.c,i))) {
                                shoot = new Point( damagedShip.c,i);
                                break;
                            }
                            else if (shoots.contains(new Point(damagedShip.c,i)) && damagedShip.equals(getShip(new Point(damagedShip.c,i),ships)) && !shoots.contains(new Point(damagedShip.c,i+1))) {
                                shoot = new Point(damagedShip.c,i + 1);
                                break;
                            }
                    }
                }
                if(!shoots.contains(shoot) && shoot.col >= 0 && shoot.col < c && shoot.row >= 0 && shoot.row < r)
                    break;
            }
        else{
            while (true) {
                shoot = new Point(getRand(0, c - 1), getRand(0, r - 1));
                if(!shoots.contains(shoot))
                    break;
            }
        }

        switch (level){
            case EASY:
                if(miss >= 15)
                    shoot = getNiceShoot();
                break;
            case NORMAL:
                if(miss >= 8)
                    shoot = getNiceShoot();
                break;
            case HARD:
                if(miss >= 5)
                    shoot = getNiceShoot();
                break;
            case EXPERT:
                if(miss >= 2)
                    shoot = getNiceShoot();
                break;
        }

        if(getShip(shoot,ships) == null)
            miss++;
        else
            miss = 0;

        shoot(shoot);

        return shoot;
    }
    public boolean contains(Point p){//Точка в пределах поля?
        return getX() <= p.col && getX() + getCellWidth()*getColumn() >= p.col &&
                getY() <= p.row && getY() + getCellHeight()*getRows() >= p.row;

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

    public boolean checkCollision(Ship checkS){//Проверка колизии с другими кораблями или границами поля
        for(Point p:checkS.getAllPosition())
            if(p.col >= c || p.row >= r)
                return true;
        for(Ship s:ships){
            if(s.equals(checkS))continue;
            for(Point p:checkS.getAllPosition())
                if(s.getAllPosition().contains(p))
                    return true;
        }
        return false;
    }

    public boolean checkCollisionPoint(Point p){//Проверка колизии с другими кораблями или границами поля по 1 точке
        if(p == null)
            return false;
        if(p.col >= c || p.row >= r)
            return false;
        for(Ship s:ships){
            if(s.getAllPosition().contains(p))
                return true;
        }
        return false;
    }
    public int getRand(int min,int max){//Возвращает значение от мин до мах
        if(min < 0){
            int tmin = 0;
            int tmax = max + Math.abs(min);
            int trand = (int) Math.round(Math.random() * (tmax+tmin) - tmin);
            trand+=min;
            return trand;
        }
        return (int) Math.round(Math.random() * (max+min) - min);

    }
    public Point getNearCell(double x,double y){//Возврщает номер колонки и строки в которых лежат координаты

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
        return new Point(Math.round((minPoint.col -offX)/getCellWidth()),Math.round((minPoint.row -offY)/getCellHeight()));
    }
    public double getDistance(Point p1,Point p2){//Расстояние между двумя точками
        return Math.sqrt(Math.pow(p2.col -p1.col,2)+Math.pow(p2.row -p1.row,2));
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

    public void draw(GraphicsContext ctx){//Рисуем поле, корабли и выстрелы
        if(DRAW_GRID) {
            ctx.save();
            ctx.translate(x, y);
            ctx.beginPath();
            for (int i = 0; i <= c; i++) {
                ctx.moveTo(i * w / c, 0);
                ctx.lineTo(i * w / c, h);
            }
            for (int i = 0; i <= r; i++) {
                ctx.moveTo(0, i * h / r);
                ctx.lineTo(w, i * h / r);
            }
            ctx.closePath();
            ctx.stroke();

            ctx.restore();
        }
        if(DRAW_SHIPS)
            for (Ship s : ships)
                s.draw(ctx);
        if(DRAW_DESTROY_SHIP)
            for (Ship s : ships)
                if(s.isDestroy())
                    s.draw(ctx);
        if(DRAW_SHOOTS) {
            ctx.setFill(Color.GRAY);
            ctx.save();
            ctx.translate(x, y);
            for (Point p : shoots) {
                if (checkCollisionPoint(p)) {
                    ctx.drawImage(krest,getCellWidth() * p.col, getCellHeight() * p.row, getCellWidth(), getCellHeight());
                    //ctx.fillRect(getCellWidth() * p.col, getCellHeight() * p.row, getCellWidth(), getCellHeight());
                }
                else
                    ctx.fillArc(getCellWidth() * p.col + getCellWidth() / 3, getCellHeight() * p.row + getCellHeight() / 3, getCellWidth() - getCellWidth() / 1.5, getCellHeight() - getCellHeight() / 1.5, 0, 360, ArcType.ROUND);
            }
            ctx.restore();
        }
        if(DRAW_NUMBERS) {
            double h = getCellHeight() > 30?30:getCellHeight();
            ctx.setFont(Font.font("no-serif", h));
            ctx.setFill(Color.BLACK);
            int offset = 5;
            for (int i = 1; i <= getRows(); i++) {
                Text t = new Text(String.valueOf(i));
                t.setFont(ctx.getFont());
                double y = getY()+h + (i-1) * getCellHeight();
                y += getCellHeight()/2 - h/2;
                ctx.fillText(t.getText(), getX() - t.getLayoutBounds().getWidth()-offset, y);
            }

            char c = 'A';
            for (int i = 1; i <= getColumn(); i++, c++) {
                Text t = new Text(String.valueOf(c));
                t.setFont(ctx.getFont());
                double x = getX() + (i - 1) * getCellWidth();
                x += getCellWidth() / 2 - t.getLayoutBounds().getWidth() / 2;
                ctx.fillText(t.getText(), x, getY()-offset, getCellWidth());
            }
        }
    }
}
class Point implements Serializable{//Класс точки
    public double col, row;
    public Point(double col, double row) {
        this.col = col;
        this.row = row;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Point)
            if(((Point) obj).col == this.col && ((Point) obj).row == this.row)
                return true;
        return super.equals(obj);
    }

    @Override
    public String toString(){
        return "{ col: "+ col +" row: "+ row +" }";
    }
}
