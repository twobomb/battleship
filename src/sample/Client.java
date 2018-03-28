package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@SuppressWarnings("Duplicates")
public class Client extends Socket {
    OutputStream os;
    InputStream is;
    Timeline tm;
    GameLoop gl;

    public Client(String host, int port) throws IOException {
        super(host, port);
        os = getOutputStream();
        is = getInputStream();
        setSoTimeout(5000);
    }

    public boolean checkConnect() throws IOException {
        os.write(1);
        os.flush();
        if (is.read() == 1)
            return true;
        return false;
    }

    public void gameOver() {
        try {
            if(checkConnect()) {
                os.write(4);
                os.flush();
            }

        }
        catch (SocketException se){
            se.getStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if(!isClosed())
            try {
                close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        if (tm != null)
            tm.stop();
        if (gl != null)
            gl.stop();
    }

    public boolean playersIsReady() throws IOException {
        os.write(2);
        os.flush();
        if (is.read() == 1)
            return true;
        return false;
    }

    public boolean isMyStep() throws IOException {
        os.write(6);
        os.flush();
        if (is.read() == 1)
            return true;
        return false;
    }

    public void putField(GameLoop gl) throws IOException {
        List<LanShip> ls = new ArrayList<>();
        for (Ship s : gl.player_bg.ships)
            ls.add(new LanShip(s.r, s.c, s.type, s.rotate));

        os.write(3);
        os.flush();
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject(ls);
    }

    public void synhronizeField(GameLoop gl) throws IOException, ClassNotFoundException {
        os.write(7);
        os.flush();
        ObjectInputStream ois = new ObjectInputStream(is);
        List<LanShip> ships_enemy = (List<LanShip>) ois.readObject();
        gl.enemy_bg.ships.clear();
        for (LanShip s : ships_enemy) {
            Ship ns = new Ship(s.r, s.c, s.type, gl.enemy_bg);
            ns.rotate = s.rotate;
            gl.enemy_bg.ships.add(ns);
        }
    }

    public void synhronizeShoots(GameLoop gl) throws IOException, ClassNotFoundException {
        os.write(8);
        os.flush();
        ObjectInputStream ois = new ObjectInputStream(is);
        List<Point> sh = (List<Point>) ois.readObject();
        gl.player_bg.shoots.clear();
        gl.player_bg.shoots.addAll(sh);
    }

    public void startGame(Stage primaryStage) {
        try {
            gl = new GameLoop((int) primaryStage.getWidth(), (int) primaryStage.getHeight());
            gl.client = this;
            gl.isLanGame = true;
            GameLoop finalGl = gl;
            VBox root = new VBox();
            HBox menu = new HBox(10);
            Label status = new Label("Расстановка кораблей");
            gl.setHeight(primaryStage.getHeight() - menu.getHeight());
            Button start = new Button("Старт");
            start.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        if (start.getText() == "Выход") {
                            Alert al = new Alert(Alert.AlertType.CONFIRMATION, "Вы действительно хотите выйти, игра будет закончена для обоих клиентов!", ButtonType.YES, ButtonType.NO);
                            al.setHeaderText("Выйти?");
                            al.setTitle("Выход");
                            if (al.showAndWait().get() == ButtonType.NO)
                                return;
                            finalGl.stop();
                            gameOver();
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("main_menu.fxml"));
                            loader.setController(new Control(primaryStage));
                            primaryStage.getScene().setRoot(loader.load());
                        } else {
                            start.setText("Выход");

                            finalGl.isRun = true;
                            putField(finalGl);
                            tm = new Timeline();
                            KeyFrame kf = new KeyFrame(Duration.millis(600), new EventHandler<ActionEvent>() {

                                @Override
                                public void handle(ActionEvent event) {
                                    try {
                                        if (playersIsReady()) {
                                            if (isMyStep())
                                                status.setText("ВАШ ХОД");
                                            else
                                                status.setText("ХОД ПРОТИВНИКА");
                                            synhronizeField(finalGl);
                                            synhronizeShoots(finalGl);
                                            if (finalGl.player_bg.isOver() || finalGl.enemy_bg.isOver()) {
                                                tm.stop();
                                                if (finalGl.player_bg.isOver())
                                                    status.setText("Поражение");
                                                else
                                                    status.setText("Победа");

                                            }
                                        }
                                        else
                                            status.setText("Ожидание игрока 2");
                                    }
                                    catch (SocketException se){
                                        se.printStackTrace();
                                        tm.stop();
                                        gameOver();
                                        Platform.runLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                FXMLLoader loader = new FXMLLoader(getClass().getResource("main_menu.fxml"));
                                                loader.setController(new Control(primaryStage));
                                                try {
                                                    primaryStage.getScene().setRoot(loader.load());
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                Alert al = new Alert(Alert.AlertType.ERROR,"Непредвиденное завершение игры!", ButtonType.OK);
                                                al.setHeaderText("Ошибка");
                                                al.setTitle("Ошибка");
                                                al.showAndWait();
                                            }
                                        });
                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            tm.getKeyFrames().add(kf);
                            tm.setCycleCount(Timeline.INDEFINITE);
                            tm.play();

                        }
                    } catch (Exception e) {
                        e.getStackTrace();
                    }
                }

            });
            Button help = new Button("Помощь в управлении");
            help.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Alert h = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.OK);
                    h.setHeaderText("Помощь в управлении");
                    h.setTitle("Управление");
                    h.setContentText("Для начала расставьте корабли.\nЧтобы изменить позицию корабля наведите на него, зажмите ЛКМ и потащите в нужное место.\nЧтобы повернуть корабль, в момент перетаскивания нажмите ПКМ.\nЧтобы нажать игру нажмите старт.\nСлево ваше поле, справа поле врага.\nЧтобы атаковать вражеские корабли, нажимайте на клетки на поле врага.\nИгра будет окончена после того как все корабли одной из команд будут уничтожены!\nДля выхода из игры используйте кнопку выход.");
                    h.showAndWait();
                }
            });
            menu.getChildren().addAll(start, help, status);

            root.getChildren().addAll(menu, gl.getCanvas());

            gl.start();
            primaryStage.getScene().setRoot(root);

            primaryStage.widthProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    finalGl.setWidth((Double) newValue);
                }
            });
            primaryStage.heightProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    finalGl.setHeight((Double) newValue);
                }
            });
        }catch (Exception e){
            e.getStackTrace();
        }
    }
}
