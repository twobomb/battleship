package sample;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.FileInputStream;
import java.io.IOException;

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
    private String winner = "";
    boolean isRun = false;
    public Canvas getCanvas() {
        return canvas;
    }

    EventHandler OnMousePressedHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {

            if(event.getButton() == MouseButton.PRIMARY) {
                if(!isRun && player_bg.contains(new Point(event.getX(),event.getY()))) {
                    Point p = player_bg.getNearCell(event.getX() - player_bg.getX(), event.getY() - player_bg.getY());
                    dragged = player_bg.getShip(p, player_bg.ships);
                    if (dragged != null)
                        last_pos = new Point(dragged.c, dragged.r);
                }
                else if (isRun && enemy_bg.contains(new Point(event.getX(),event.getY()))){
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
    };
    public void setLevel(BattleGrid.LEVEL lvl){
        player_bg.level = lvl;
    }
    public GameLoop(int w, int h) throws IOException {
        canvas = new Canvas(w,h);
        ctx = canvas.getGraphicsContext2D();
        FileInputStream file = new FileInputStream("C:\\Users\\twobomb\\Desktop\\battleship\\src\\sample\\water.png");
        waterTexture = new Image(file);
        file.close();
        int wh = (int)(canvas.getWidth()-120)/2;


        player_bg = new BattleGrid(40,canvas.getHeight()-40 - wh,wh,wh);
        enemy_bg = new BattleGrid(80+wh,canvas.getHeight()-40 - wh,wh,wh);

        enemy_bg.DRAW_SHIPS = false;
        player_bg.generateScene();
        enemy_bg.generateScene();
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED,OnMousePressedHandler);

        canvas.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() == MouseButton.PRIMARY && dragged!=null){
                    if(player_bg.checkCollision(dragged)) {
                        dragged.setRow((int) last_pos.row);
                        dragged.setColumn((int) last_pos.col);
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
        player_bg.onGameOverListener.add(new onGameOver() {
            @Override
            public void isOver() {
                winner = "Вы проиграли!";
                canvas.removeEventHandler(MouseEvent.MOUSE_PRESSED,OnMousePressedHandler);

            }
        });
        enemy_bg.onGameOverListener.add(new onGameOver() {
            @Override
            public void isOver() {
                winner = "Вы выиграли!";
                canvas.removeEventHandler(MouseEvent.MOUSE_PRESSED,OnMousePressedHandler);

            }
        });
    }
    public void setWidth(double width){
        canvas.setWidth(width);
        int wh = (int)(canvas.getWidth()-120)/2;
        player_bg.setY((int) (canvas.getHeight()- player_bg.getCellHeight()*player_bg.getRows()-80));
        player_bg.setWidth(wh);
        player_bg.setHeight( wh);
        enemy_bg.setX(80+wh);
        enemy_bg.setY((int) (canvas.getHeight()- player_bg.getCellHeight()*player_bg.getRows()-80));
        enemy_bg.setWidth(wh);
        enemy_bg.setHeight(wh);
    }
    public void setHeight(double height){
        canvas.setHeight(height);
        player_bg.setY((int) (canvas.getHeight()- player_bg.getCellHeight()*player_bg.getRows()-80));
        enemy_bg.setY((int) (canvas.getHeight()- player_bg.getCellHeight()*player_bg.getRows()-80));
    }
    @Override
    public void handle(long now) {
        ctx.setFill(Color.GRAY);
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
        ctx.setFill(Color.BLUEVIOLET);
        ctx.setFont(Font.font(30));
        Text t = new Text(winner);
        t.setFont(Font.font(30));

        ctx.fillText(winner,canvas.getWidth()/2-t.getLayoutBounds().getWidth()/2,30);
    }
}
