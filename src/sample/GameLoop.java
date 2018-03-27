package sample;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.ArcType;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameLoop extends AnimationTimer {
    private float deltaTime = 0;
    private Long lastTime = new Long(0);

    private Canvas canvas ;
    private GraphicsContext ctx;
    private double [] water_xoff = {0,200,500};
    double [] water_speed = {10,14,19};
    Image waterTexture;
    BattleGrid player_bg;
    BattleGrid enemy_bg ;
    Ship dragged = null;
    Point last_pos;
    public Canvas getCanvas() {
        return canvas;
    }
    public GameLoop(int w, int h) throws IOException {
        canvas = new Canvas(w,h);
        ctx = canvas.getGraphicsContext2D();
        FileInputStream file = new FileInputStream("C:\\Users\\twobomb\\Desktop\\water.png");
        waterTexture = new Image(file);
        file.close();
        int wh = (int)(canvas.getWidth()-120)/2;


        player_bg = new BattleGrid(40,canvas.getHeight()-40 - wh,wh,wh);
        enemy_bg = new BattleGrid(80+wh,canvas.getHeight()-40 - wh,wh,wh);
        player_bg.generateScene();
        enemy_bg.generateScene();
        canvas.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                if(event.getButton() == MouseButton.PRIMARY) {
                    if(player_bg.contains(new Point(event.getX(),event.getY()))) {
                        Point p = player_bg.getNearCell(event.getX() - player_bg.getX(), event.getY() - player_bg.getY());
                        dragged = player_bg.getShip(p, player_bg.ships);
                        if (dragged != null)
                            last_pos = new Point(dragged.c, dragged.r);
                    }
                    else if (enemy_bg.contains(new Point(event.getX(),event.getY()))){
                        Point p = enemy_bg.getNearCell(event.getX() - enemy_bg.getX(), event.getY() - enemy_bg.getY());
                        if(!enemy_bg.shoots.contains(p)) {
                            enemy_bg.shoot(p);
                            player_bg.botShoot();
                        }
                    }
                }
                else if(event.getButton() == MouseButton.SECONDARY && dragged!=null) {
                    dragged.rotate = !dragged.rotate;
                    dragged.moveTo(event.getX() - player_bg.getX(),event.getY()-player_bg.getY());
                }

            }
        });
        canvas.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() == MouseButton.PRIMARY && dragged!=null){
                    if(player_bg.checkCollision(dragged)) {
                        dragged.setRow((int) last_pos.y);
                        dragged.setColumn((int) last_pos.x);
                    }
                    dragged = null;
                }
            }
        });
        canvas.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(dragged != null)
                dragged.moveTo(event.getX() - player_bg.getX(),event.getY()-player_bg.getY());
            }
        });
    }

    public void setWidth(double width){
        canvas.setWidth(width);
        int wh = (int)(canvas.getWidth()-120)/2;
        player_bg.setY((int)canvas.getHeight()-40 - wh);
        player_bg.setWidth(wh);
        player_bg.setHeight( wh);
        enemy_bg.setX(80+wh);
        enemy_bg.setY((int)canvas.getHeight()-40 - wh);
        enemy_bg.setWidth(wh);
        enemy_bg.setHeight(wh);
    }
    @Override
    public void handle(long now) {
        ctx.clearRect(0,0,canvas.getWidth(),canvas.getHeight());
        if(lastTime!=0)
            deltaTime = (now-lastTime)/1000000000.0f ;
        lastTime = now;
        ctx.setGlobalAlpha(.5);

        for(int i = 0; i < water_xoff.length;i++) {
            ctx.drawImage(waterTexture, water_xoff[i],0,waterTexture.getWidth()/2,waterTexture.getHeight()/2,0,0,canvas.getWidth(),canvas.getHeight());
            water_xoff[i] = (water_xoff[i] + water_speed[i] * deltaTime) % (waterTexture.getWidth() / 2);
        }
        ctx.setGlobalAlpha(1);
        player_bg.draw(ctx);
        enemy_bg.draw(ctx);



    }
}
