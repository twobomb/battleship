package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

import static com.sun.javafx.scene.control.skin.Utils.getResource;

public class Main extends Application {

    public static Stage stage;
    @Override
    public void start(Stage primaryStage) throws Exception{
        stage = primaryStage;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("main_menu.fxml"));
        loader.setController(new Control(primaryStage));
        primaryStage.setMinWidth(720);
        primaryStage.setMinHeight(480);

        Scene s = new Scene(loader.load(),720,480);

        primaryStage.setScene(s);
        primaryStage.setTitle("Морской бой");
        primaryStage.show();


    }

    public static void main(String[] args) throws IOException {
        launch(args);
    }
}
