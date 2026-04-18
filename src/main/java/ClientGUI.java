import java.util.HashMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
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
    HashMap<String, Scene> sceneMap, loginMap;
    private TextField usernameField, messageField, loginField;
    private Button sendButton, playButton, howtoplay, signinButton, messageButton;
    VBox clientList, loginVbox;
    HBox userBox;
    Client clientConnection;
    Label loginLabel, userLabel;
    String currentUser;
    BorderPane root;
    GridPane board;
    ScrollPane userPane;

    ListView<String> messagesList;
    public static void main(String[] args) {
        launch(args);
    }


    public void start(Stage primaryStage) {

         clientConnection = new Client(data -> {    //Creating the new Client
            Platform.runLater(()-> {
                if (((Message) data).getMsgType().equals(Message.error)) {          //Conditions for different server messages
                    loginLabel.setText("Username is already Taken!");
                } else if (((Message) data).getMsgType().equals(Message.userList)) {
                    currentUser = loginField.getText();
                    clientList.getChildren().clear();       //Clear the Vbox and repopulate it everytime a new user joins
                        for(String username : ((Message) data).getGroupMembers()) {
                            if(username.equals(currentUser)){      //This makes it so if its your screen you won't see the play btn on ur name
                                userLabel = new Label();
                                userLabel.setText(username);
                                userBox = new HBox(userLabel);
                                clientList.getChildren().add(userBox);
                            } else {
                                userLabel = new Label();        //Displays current players with a play btn and message btn
                                userLabel.setText(username);
                                playButton = new Button("Play");
                                messageButton = new Button("Message");
                                userBox = new HBox(userLabel, playButton, messageButton);
                                clientList.getChildren().add(userBox);
                            }
                        }
                    if(!loginField.getText().isEmpty() && ((Message) data).getGroupMembers().contains(loginField.getText())){   //Check to see if username is taken
                        currentUser = ((Message) data).getClient();     //If not switch scenes and thats your user
                        primaryStage.setScene(sceneMap.get("home"));
                        primaryStage.setTitle("Client");
                        primaryStage.show();
                 }
                } else if (((Message) data).getMsgType().equals(Message.serverMessage)) {

                } else if (((Message) data).getMsgType().equals(Message.challenge)) {

                }
            });
        });

        clientConnection.start();

        loginMap = new HashMap<String, Scene>();
        loginMap.put("login", loginGUI());

        primaryStage.setScene(loginMap.get("login"));
        primaryStage.setTitle("Client");
        primaryStage.show();

        sceneMap = new HashMap<String, Scene>();
        sceneMap.put("home",  createHomeGUI());
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
    }

    public Scene createHomeGUI(){
        root = new BorderPane();
        board = new GridPane();
        //clientList = new VBox();
        for (int row = 0 ; row < 8; row++){
            for (int col = 0 ; col < 8; col++){
                board.add(buildSquare(row,col),col,row);
            }
        }
        clientList = new VBox();
        userPane = new ScrollPane(clientList);      //created a scroll pane and itll adjust accordingly
        userPane.setFitToWidth(true);
        root.setLeft(board);
        root.setRight(userPane);
        return new Scene(root, 800, 600);
    }

    public Scene loginGUI(){   //Login Scene
        root = new BorderPane();

        loginField = new TextField();
        signinButton = new Button("Sign in");
        loginLabel = new Label();

        signinButton.setOnAction(e->{
            Message userName = new Message(Message.createUser, loginField.getText());       //Gets the username and stores it
            clientConnection.send(userName);
        });

        loginVbox = new VBox(10, loginField, signinButton, loginLabel);
        root.setCenter(loginVbox);
        loginVbox.setAlignment(Pos.CENTER);
        loginVbox.setMaxWidth(300);

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
