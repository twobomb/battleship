package sample;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
}
