import java.util.HashMap;
import java.util.Optional;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import Checkers.Move;
import Checkers.Pieces;
public class ClientGUI extends Application {
    HashMap<String, Scene> sceneMap, loginMap;
    HashMap<String, Stage> chatMap;
    HashMap<String ,ListView<String>> chatListMap;
    private TextField usernameField, messageField, loginField;
    private Button sendButton, playButton, howtoplay, signinButton, messageButton, agreeChal, rejectChal;
    VBox clientList, loginVbox, chatVbox;
    HBox userBox, messageBox;
    Client clientConnection;
    Label loginLabel, userLabel,errormsg;
    String currentUser;
    BorderPane root;
    GridPane board;
    ScrollPane userPane, messagePane;
    Stage chatbox;
    boolean pieceSelected = false;
    String playerColor;
    int pCol, pRow, nRow, nCol;
    Pieces selectedPiece;

    ListView<String> messagesList;
    public static void main(String[] args) {
        launch(args);
    }


    public void start(Stage primaryStage) {
         clientConnection = new Client(data -> {    //Creating the new Client
            Platform.runLater(()-> {
                if (data instanceof  Message) {
                    if (((Message) data).getMsgType().equals(Message.error)) {          //Conditions for different server messages
                        loginLabel.setText("Username is already Taken!");
                    } else if (((Message) data).getMsgType().equals(Message.userList)) {
                        currentUser = loginField.getText();
                        clientList.getChildren().clear();       //Clear the Vbox and repopulate it everytime a new user joins
                        for (String username : ((Message) data).getGroupMembers()) {
                            if (username.equals(currentUser)) {      //This makes it so if its your screen you won't see the play btn on ur name
                                userLabel = new Label();
                                userLabel.setText(username);
                                userLabel.setPrefHeight(20);
                                userLabel.setStyle("-fx-font-weight: 1800;" + "-fx-font-variant: small-caps;" + "-fx-text-fill: white;" + "-fx-font-size: 20;");
                                userBox = new HBox(userLabel);
                                userBox.setStyle("-fx-background-color: #9a6139;" + "-fx-border-color: black; -fx-border-width: 1;" );
                                userBox.setPrefHeight(40);
                                clientList.getChildren().add(userBox);
                            } else {
                                userLabel = new Label();        //Displays current players with a play btn and message btn
                                userLabel.setText(username);
                                userLabel.setPrefHeight(20);
                                userLabel.setStyle("-fx-font-weight: 1800;" + "-fx-font-variant: small-caps;" + "-fx-text-fill: white;" + "-fx-font-size: 20");
                                Image sword = new Image(getClass().getResourceAsStream("/swordsTransparent.png"));
                                ImageView imageSword = new ImageView(sword);
                                imageSword.setFitWidth(25);
                                imageSword.setFitHeight(25);
                                playButton = new Button("");
                                playButton.setGraphic(imageSword);
                                playButton.setOnAction((event) -> {
                                    clientConnection.send(new Message(Message.challenge, "", currentUser, username));
                                });
                                Image message = new Image(getClass().getResourceAsStream("/message.png"));
                                ImageView imageMessage = new ImageView(message);
                                imageMessage.setFitWidth(25);
                                imageMessage.setFitHeight(25);
                                messageButton = new Button("");
                                messageButton.setGraphic(imageMessage);
                                userBox = new HBox(10,userLabel, playButton, messageButton);
                                userBox.setStyle("-fx-background-color: #9a6139;" + "-fx-border-color: black; -fx-border-width: 1;");
                                userBox.setPrefHeight(40);
                                clientList.getChildren().add(userBox);
                                messageButton.setOnAction(e -> {
                                    Stage chatStage = chatsystem(username);
                                    chatStage.show();
                                });
                            }
                        }
                        if (primaryStage.getScene() == loginMap.get("login") && ((Message) data).getGroupMembers().contains(currentUser)) {
                            primaryStage.setScene(sceneMap.get("home"));
                            primaryStage.setTitle("Client");
                            primaryStage.show();
                        }
                    } else if (((Message) data).getMsgType().equals(Message.serverMessage)) {

                    } else if (((Message) data).getMsgType().equals(Message.challenge)) {
                        ButtonType ACCEPT = new ButtonType("Accept", ButtonBar.ButtonData.OK_DONE);
                        ButtonType REJECT = new ButtonType("Reject", ButtonBar.ButtonData.CANCEL_CLOSE);
                        Alert giveplayerChoice = new Alert(Alert.AlertType.CONFIRMATION);
                        giveplayerChoice.setTitle("Game Challenge");
                        giveplayerChoice.getButtonTypes().setAll(ACCEPT, REJECT);
                        giveplayerChoice.setHeaderText(((Message) data).getClient() + "Wants to play!");
                        giveplayerChoice.setContentText("Do you accept?");

                        Optional<ButtonType> result = giveplayerChoice.showAndWait();
                        if (result.isPresent() && result.get() == ACCEPT) {
                            clientConnection.send(new Message(Message.challengeResponse, "Accept", currentUser, ((Message) data).getClient()));
                        } else if (result.isPresent() && result.get() == REJECT) {
                            clientConnection.send(new Message(Message.challengeResponse, "Decline", currentUser, ((Message) data).getClient()));
                        }
                    } else if (((Message) data).getMsgType().equals(Message.sendToIndvidual)) {
                        String currReceiver = ((Message) data).getClient();
                        Stage currChat = chatsystem(currReceiver);
                        currChat.show();
                        chatListMap.get(currReceiver).getItems().add(currReceiver + ": " + ((Message) data).getMessage());  //receiver side
                    } else if (((Message) data).getMsgType().equals(Message.startGame)) {
                        playerColor = ((Message) data).getMessage();
                        sceneMap.put("game", createGameGUI(((Message) data).getClient()));
                        primaryStage.setScene(sceneMap.get("game"));
                    }
                    ;
                }
                else if (data instanceof Move) {
                    updateBoard((Move) data);
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
        sceneMap.put("home", createHomeGUI());

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
        board = buildBoard();
        clientList = new VBox();
        clientList.setPrefWidth(200);
        userPane = new ScrollPane(clientList);      //created a scroll pane and itll adjust accordingly
        userPane.setFitToWidth(true);
        userPane.setMaxHeight(200);
        userPane.setStyle("-fx-background-color:#595252 ");
        root.setStyle("-fx-background-color:#b61e2b;");
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
        loginField.setPromptText("Enter your username");
        loginField.setPrefHeight(35);
        signinButton = new Button("Sign in");
        signinButton.setStyle("-fx-font-size: 24px;" + "-fx-font-weight: bold;" + "-fx-font-variant: small-caps;" + "-fx-background-color: #b61e2b;" + "-fx-text-fill: white;");
        loginLabel = new Label();

        signinButton.setOnAction(e->{
            Message userName = new Message(Message.createUser, loginField.getText());       //Gets the username and stores it
            if(!loginField.getText().equals("")){
                clientConnection.send(userName);
            }
        });

        loginVbox = new VBox(10, imageView,loginField, signinButton, loginLabel);
        loginVbox.setStyle("-fx-background-color: #ffffff;"+"-fx-font-family: 'serif';");
        root.setCenter(loginVbox);
        loginVbox.setAlignment(Pos.CENTER);
        loginVbox.setMaxWidth(300);

        return new Scene(root, 800, 600);
    }
    public Scene createGameGUI(String target){
        root = new BorderPane();
        errormsg =  new Label();
        Label userLabel = new Label(currentUser);
        Label opponentLabel = new Label(target);
        Button resignButton = new Button("Resign");
        VBox chatBox = buildChatBox(target);
        HBox idk =  new HBox(10, userLabel, resignButton);
        board = buildBoard();
        root.setTop(opponentLabel);
        root.setCenter(board);
        root.setBottom(idk);
        root.setRight(chatBox);
        return new Scene(root,800,600);
    }

    private VBox buildChatBox(String target){
        messagesList = new ListView<String>();
        messageField = new TextField();
        messagePane = new ScrollPane(messagesList);
        sendButton = new Button("Send");

        messageBox = new HBox(10, messageField, sendButton);
        chatVbox = new VBox(10, messagePane, messageBox);
        chatVbox.setAlignment(Pos.CENTER);

        sendButton.setOnAction(e->{
            Message currMessage = new Message(Message.sendToIndvidual, messageField.getText(), currentUser, target);    //Creates the message to individual
            clientConnection.send(currMessage);
            chatListMap.get(target).getItems().add(currentUser + ": " + messageField.getText());     //Sender side
        });
        return chatVbox;
    }

    private StackPane updateBoard (Move move){
        StackPane oldSquare = null;
        StackPane newSquare = null;
        StackPane middleSquare = null; //For taking pieces

        int midRow = (move.getpRow() + move.getnRow()) / 2;
        int midCol = (move.getpCol() + move.getnCol()) / 2;
        boolean isJump = Math.abs(move.getnRow() - move.getpRow()) == 2;

        for (Node c : board.getChildren()){
            if (GridPane.getColumnIndex(c) == move.getpCol() && GridPane.getRowIndex(c) == move.getpRow()){
                oldSquare = (StackPane) c;
            }
            else if (GridPane.getColumnIndex(c) == move.getnCol() &&  GridPane.getRowIndex(c) == move.getnRow()){
                newSquare = (StackPane) c;
            }
            else if (isJump && GridPane.getColumnIndex(c) == midCol && GridPane.getRowIndex(c) == midRow){
                middleSquare = (StackPane) c;
            }
        }
        Circle piece = (Circle) oldSquare.getChildren().get(1);
        newSquare.getChildren().add(piece);
        oldSquare.getChildren().remove(piece);
        oldSquare.setUserData(null);
        newSquare.setUserData(move.getPiece());

        if (isJump && middleSquare != null && middleSquare.getChildren().size() > 1) {
            middleSquare.getChildren().remove(1);
            middleSquare.setUserData(null);
        }
        return newSquare;
    }
//    private void handleSquareClick(int row, int col){
//        System.out.println("Clicked: " + row + ", " + col + " playerColor: " + playerColor);
//        for (Node c : board.getChildren()){
//            if (GridPane.getRowIndex(c) == row && GridPane.getColumnIndex(c) == col) {
//                if (!pieceSelected) {
//                    System.out.println("Sending move: " + pRow + "," + pCol + " -> " + nRow + "," + nCol);
//                    System.out.println("Color: " + ((Pieces) c.getUserData()).getColor().toString());
//                    System.out.println("Match: " + ((Pieces) c.getUserData()).getColor().toString().equals(playerColor));
//                    if (c.getUserData() != null && ((Pieces) c.getUserData()).getColor().toString().equals(playerColor)) {
//                        System.out.println("UserData: " + c.getUserData());
//                        pieceSelected = true;
//                        selectedPiece = (Pieces) c.getUserData();
//                        pRow = row;
//                        pCol = col;
//                        c.setEffect(new Glow());
//                    } else {
//                        errormsg.setText("That is not your piece!");
//                    }
//                } else {
//                    System.out.println("Sending move: " + pRow + "," + pCol + " -> " + nRow + "," + nCol);
//                    nRow = row;
//                    nCol = col;
//                    Move move = new Move(selectedPiece, pRow, pCol, nRow, nCol);
//                    clientConnection.send(move);
//                    pieceSelected = false;
//                }
//            }
//        }
//    }
    private StackPane buildSquare(int row, int col){
        StackPane square = new StackPane();
        Rectangle rectangle = new Rectangle(50,50);
        if ((row+col)%2 != 0){
            rectangle.setFill(Color.rgb(0,0,0));        }
        else {
            rectangle.setFill(Color.rgb(255,255,255));
        }
        square.getChildren().add(rectangle);
        if ((row+col)%2 != 0) {
            if (row < 3) {
                square.getChildren().add(buildRedPiece());
                square.setUserData(new Pieces(Pieces.Color.RED));
            } else if (row > 4) {
                square.getChildren().add(buildBlackPiece());
                square.setUserData(new Pieces(Pieces.Color.BLACK));
            }
        }
        square.setOnDragDetected(e -> {
            if (square.getUserData() != null &&
                    ((Pieces) square.getUserData()).getColor().toString().equals(playerColor)) {
                selectedPiece = (Pieces) square.getUserData();
                Dragboard db = square.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(row + "," + col);
                db.setContent(content);
                pRow = row;
                pCol = col;
                e.consume();
            }
        });
        square.setOnDragOver(e -> {
            if (e.getGestureSource() != square && e.getDragboard().hasString()) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
            e.consume();
        });

        square.setOnDragDropped(e -> {
            nRow = row;
            nCol = col;
            Move move = new Move(selectedPiece, pRow, pCol, nRow, nCol);
            clientConnection.send(move);
            e.setDropCompleted(true);
            e.consume();
        });
        return square;
    }
    private GridPane buildBoard(){
        GridPane board = new GridPane();
        for (int row = 0 ; row < 8; row++){
            for (int col = 0 ; col < 8; col++){
                board.add(buildSquare(row,col),col,row);
            }
        }
        return board;
    }
    private Circle buildRedPiece(){
        Circle redCircle = new Circle(25);
        redCircle.setFill(Color.rgb(168,43,43));
        return redCircle;
    }

    private Circle buildBlackPiece(){
        Circle blackCircle = new Circle(25);
        blackCircle.setFill(Color.rgb(67,57,57));
        return blackCircle;
    }
}
