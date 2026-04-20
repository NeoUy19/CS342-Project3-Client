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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ClientGUI extends Application {
    HashMap<String, Scene> sceneMap, loginMap;
    HashMap<String, Stage> chatMap;
    HashMap<String ,ListView<String>> chatListMap;
    private TextField usernameField, messageField, loginField;
    private Button sendButton, playButton, howtoplay, signinButton, messageButton;
    VBox clientList, loginVbox, chatVbox;
    HBox userBox, messageBox;
    Client clientConnection;
    Label loginLabel, userLabel;
    String currentUser;
    BorderPane root;
    GridPane board;
    ScrollPane userPane, messagePane;
    Stage chatbox;

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
                                messageButton.setOnAction(e->{
                                    Stage chatStage = chatsystem(username);
                                    chatStage.show();
                                });
                            }
                        }
                    if(!loginField.getText().isEmpty() && ((Message) data).getGroupMembers().contains(loginField.getText())){   //Check to see if username is taken
                        primaryStage.setScene(sceneMap.get("home"));
                        primaryStage.setTitle("Client");
                        primaryStage.show();
                 }
                } else if (((Message) data).getMsgType().equals(Message.serverMessage)) {

                } else if (((Message) data).getMsgType().equals(Message.challenge)) {

                } else if (((Message) data).getMsgType().equals(Message.sendToIndvidual)){
                    String currReceiver = ((Message)data).getClient();
                    Stage currChat = chatsystem(currReceiver);
                    currChat.show();
                    chatListMap.get(currReceiver).getItems().add( currReceiver + ": " + ((Message) data).getMessage());  //receiver side
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

        chatMap = new HashMap<String, Stage>();

        chatListMap = new HashMap<String, ListView<String>>();

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

    public Stage chatsystem(String target){

        if(chatMap.containsKey(target)){        //Check to see if the message pop up already exists
            return chatMap.get(target);         //Used a hashmap b/c we can see if a stage already exists for that specific chat
        }

        messagesList = new ListView<String>();
        chatListMap.put(target,messagesList);      //ChatMap stores a window for each person that joins and messagelist stores each messagelist per person

        chatbox = new Stage();      //used a stage so we can close out of the messaging system
        messageField = new TextField();
        messagePane = new ScrollPane(messagesList);
        sendButton = new Button("Send");

        messageBox = new HBox(10, messageField, sendButton);
        chatVbox = new VBox(10, messagePane, messageBox);
        chatVbox.setAlignment(Pos.CENTER);

        chatbox.setScene(new Scene(chatVbox,400, 500));

        sendButton.setOnAction(e->{
            Message currMessage = new Message(Message.sendToIndvidual, messageField.getText(), currentUser, target);    //Creates the message to individual
            clientConnection.send(currMessage);
            chatListMap.get(target).getItems().add(currentUser + ": " + messageField.getText());     //Sender side

        });

        chatMap.put(target,chatbox); //Adds a entry to the hashmap
        return chatbox;
    }

    public Scene loginGUI(){   //Login Scene
        Image image = new Image(getClass().getResourceAsStream("/checkersTran.PNG"));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(350);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);

        root = new BorderPane();
        root.setStyle("-fx-background-color: #ffffff;"+"-fx-font-family: 'serif';");

        loginField = new TextField();
        signinButton = new Button("Sign in");
        signinButton.setStyle("-fx-font-size: 24px;" + "-fx-font-weight: bold;" + "-fx-font-variant: small-caps;" + "-fx-background-color: #b61e2b;" + "-fx-text-fill: white;");
        loginLabel = new Label();

        signinButton.setOnAction(e->{
            Message userName = new Message(Message.createUser, loginField.getText());       //Gets the username and stores it
            clientConnection.send(userName);
        });

        loginVbox = new VBox(10, imageView,loginField, signinButton, loginLabel);
        loginVbox.setStyle("-fx-background-color: #ffffff;"+"-fx-font-family: 'serif';");
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
