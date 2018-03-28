package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class Control implements Initializable{
    Stage primaryStage;

    @FXML
    Button newGame;

    @FXML
    Button lanGame;

    @FXML
    Button exitGame;

    public Control(Stage primaryStage){
        this.primaryStage = primaryStage;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //newGame.setOnAction(this::handleButtonAction);
        newGame.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("new_game.fxml"));
                loader.setController(new Control2(primaryStage));
                try {
                    primaryStage.getScene().setRoot(loader.load());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        lanGame.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("lan_menu.fxml"));
                loader.setController(new Control3(primaryStage));
                try {
                    primaryStage.getScene().setRoot(loader.load());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        exitGame.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert al = new Alert(Alert.AlertType.CONFIRMATION,"Вы уверены что хотите выйти?", ButtonType.YES ,ButtonType.NO);
                al.setHeaderText("Подтверждение");
                if(al.showAndWait().get() == ButtonType.YES)
                    primaryStage.close();
            }
        });
    }
}
