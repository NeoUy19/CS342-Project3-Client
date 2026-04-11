import java.util.ArrayList;
import java.util.HashMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ClientGUI extends Application {
    HashMap<String, Scene> sceneMap;
    private TextField usernameField, messageField;
    private Button sendButton, playButton, howtoplay, signinButton;
    BorderPane root;
    GridPane board;

    ListView<String> messagesList;
    public static void main(String[] args) {
        launch(args);
    }


    public void start(Stage primaryStage) {
        sceneMap = new HashMap<String, Scene>();
        sceneMap.put("home",  createHomeGUI());
//        sceneMap.put("CreateUser",  createUserGui());
//        clientConnection.start();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });


        primaryStage.setScene(sceneMap.get("home"));
        primaryStage.setTitle("Client");
        primaryStage.show();
    }

    public Scene createHomeGUI(){
        root = new BorderPane();
        board = new GridPane();
        for (int row = 0 ; row < 8; row++){
            for (int col = 0 ; col < 8; col++){
                board.add(buildSquare(row,col),col,row);
            }
        }
        playButton = new Button("Play");
        howtoplay = new Button("How to Play");
        VBox rightSide = new VBox(playButton, howtoplay);
        root.setLeft(board);
        root.setRight(rightSide);
        return new Scene(root, 800, 600);
    }

    private StackPane buildSquare(int row, int col){
        StackPane square = new StackPane();
        Rectangle rectangle = new Rectangle(25,25);
        if ((row+col)%2 == 0){
            rectangle.setFill(Color.rgb(232,245,184));
        }
        else {
            rectangle.setFill(Color.rgb(0,0,0));
        }
        square.getChildren().add(rectangle);
        return square;
    }
}
