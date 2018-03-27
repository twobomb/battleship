package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Control2 implements Initializable {
    Stage primaryStage;
    @FXML
    Button inBattle;

    @FXML
    ComboBox level;
    public Control2(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
       level.getItems().add("Легкий");
       level.getItems().add("Нормальный");
       level.getItems().add("Сложный");
       level.getItems().add("Эксперт");
       level.getSelectionModel().selectFirst();
       inBattle.setOnAction(new EventHandler<ActionEvent>() {
           @Override
           public void handle(ActionEvent event) {
               GameLoop gl = null;
               try {
                   gl = new GameLoop((int) primaryStage.getWidth(), (int) primaryStage.getHeight());
               } catch (IOException e) {
                   e.printStackTrace();
               }
               GameLoop finalGl = gl;
               VBox root =  new VBox();
               HBox menu = new HBox(10);
               gl.setHeight(primaryStage.getHeight() - menu.getHeight());
               Button start = new Button("Старт");
               start.setOnAction(new EventHandler<ActionEvent>() {
                   @Override
                   public void handle(ActionEvent event) {
                       if(start.getText() == "Выход"){
                           Alert al = new Alert(Alert.AlertType.CONFIRMATION,"Вы действительно хотите выйти, игра не будет сохранена!",ButtonType.YES,ButtonType.NO);
                           al.setHeaderText("Выйти?");
                           al.setTitle("Выход");
                           if(al.showAndWait().get() == ButtonType.NO)
                               return;
                           finalGl.stop();
                           FXMLLoader loader = new FXMLLoader(getClass().getResource("main_menu.fxml"));
                           loader.setController(new Control(primaryStage));
                           try {
                               primaryStage.getScene().setRoot(loader.load());
                           } catch (IOException e) {
                               e.printStackTrace();
                           }
                       }
                       else {
                           start.setText("Выход");
                           finalGl.isRun = true;
                       }
                   }
               });
               Button help = new Button("Помощь в управлении");
               help.setOnAction(new EventHandler<ActionEvent>() {
                   @Override
                   public void handle(ActionEvent event) {
                       Alert h = new Alert(Alert.AlertType.INFORMATION,"",ButtonType.OK);
                       h.setHeaderText("Помощь в управлении");
                       h.setTitle("Управление");
                       h.setContentText("Для начала расставьте корабли.\nЧтобы изменить позицию корабля наведите на него, зажмите ЛКМ и потащите в нужное место.\nЧтобы повернуть корабль, в момент перетаскивания нажмите ПКМ.\nЧтобы нажать игру нажмите старт.\nСлево ваше поле, справа поле врага.\nЧтобы атаковать вражеские корабли, нажимайте на клетки на поле врага.\nИгра будет окончена после того как все корабли одной из команд будут уничтожены!\nДля выхода из игры используйте кнопку выход.");
                       h.showAndWait();
                   }
               });
               menu.getChildren().addAll(start,help);

               root.getChildren().addAll(menu,gl.getCanvas());

               BattleGrid.LEVEL lvl = BattleGrid.LEVEL.EASY;
               switch (level.getSelectionModel().getSelectedIndex()){
                   case 0: lvl = BattleGrid.LEVEL.EASY;break;
                   case 1: lvl = BattleGrid.LEVEL.NORMAL;break;
                   case 2: lvl = BattleGrid.LEVEL.HARD;break;
                   case 3: lvl = BattleGrid.LEVEL.EXPERT;break;
               }
               gl.setLevel(lvl);
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
           }
       });
    }
}
