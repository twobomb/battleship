package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;


public class Control3 implements Initializable{
    Stage primaryStage;
    @FXML
    TextField port;

    @FXML
    TextField host;
    @FXML
    Button createServer;

    @FXML
    Button joinGame;

    @FXML
    Button backToMenu;

    @FXML
    Label serverStatus;

    Server server;
    public Control3(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        backToMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(server!= null && server.isAlive)
                        server.stop();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("main_menu.fxml"));
                loader.setController(new Control(primaryStage));
                try {
                    primaryStage.getScene().setRoot(loader.load());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        joinGame.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!port.getText().matches("\\d{3,}"))
                    return;
                try {
                    Client client = new Client(host.getText(),Integer.valueOf(port.getText()));
                    if(client.checkConnect()){
                        client.startGame(primaryStage);
                    }
                    else{
                        Alert al = new Alert(Alert.AlertType.WARNING,"Сервер забит!", ButtonType.OK);
                        al.setHeaderText("Ошибка");
                        al.setTitle("Ошибка");
                        al.showAndWait();
                    }

                }
                catch (ConnectException e){
                    System.out.println("Ошибка подключения к серверу");
                    Alert a =  new Alert(Alert.AlertType.ERROR,"Ошибка подключение к серверу", ButtonType.OK);
                    a.setTitle("Ошибка");
                    a.setHeaderText("Сервер не найден");
                    a.showAndWait();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                catch (Exception e){
                    System.out.println("error");
                    e.getStackTrace();
                }
            }
        });
        createServer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(server != null){
                        server.stop();
                    server = null;
                    serverStatus.setText("Сервер отключен");
                    createServer.setText("Создать сервер");
                    createServer.setDisable(true);
                    Timer t = new Timer();
                    t.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            createServer.setDisable(false);
                        }
                    },2000);
                    return;
                }
                if(!port.getText().matches("\\d{3,}"))
                    return;
                try {
                    server = new Server(Integer.valueOf(port.getText()));
                    server.start();
                    serverStatus.setText("Сервер запущен на порту "+port.getText());
                    createServer.setText("Отключить сервер");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
