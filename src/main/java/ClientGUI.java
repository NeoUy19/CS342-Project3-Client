import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import Checkers.Move;
import Checkers.Pieces;
import javafx.util.Duration;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class ClientGUI extends Application {
    HashMap<String, Scene> sceneMap, loginMap;
    HashMap<String, Stage> chatMap;
    HashMap<String ,ListView<String>> chatListMap;
    HashMap<String, Button> playbuttonMap;
    private TextField usernameField, messageField, loginField;
    private Button sendButton, playButton, howtoplay, signinButton, messageButton, agreeChal, rejectChal, emojiButton, emojisButton;
    VBox clientList, loginVbox, chatVbox;
    HBox userBox, messageBox, topBar;
    Client clientConnection;
    Label loginLabel, userLabel,errormsg, userNameLabel;
    Label turnLabel = new Label();
    String currentUser, opponent;
    BorderPane root;
    GridPane board;
    FlowPane emojiPane;
    ScrollPane userPane, messagePane;
    Stage chatbox;
    boolean pieceSelected = false;
    boolean inGame = false;
    String playerColor;
    String currentTurn = "RED";
    int pCol, pRow, nRow, nCol;
    Pieces selectedPiece;
    boolean isSlidePaneOpen = true;
    Pieces[][] tempBoard = new  Pieces[8][8]; //create a temporary board that allows the highlight of possible places to move

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
                        userNameLabel.setText(currentUser);
                        clientList.getChildren().clear();       //Clear the Vbox and repopulate it everytime a new user joins
                        for (String username : ((Message) data).getGroupMembers()) {
                            if (username.equals(currentUser)) {      //This makes it so if its your screen you won't see the play btn on ur name
                                continue;
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
                                playbuttonMap.put(username, playButton);
                                playButton.setGraphic(imageSword);
                                playButton.setOnAction((event) -> {
                                    playButton.setDisable(true);
                                    opponent = username;
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
                        if (((Message) data).getMessage().contains("Wins!")) {
                            ButtonType REMATCH = new ButtonType("rematch", ButtonBar.ButtonData.OK_DONE);
                            ButtonType HOME = new ButtonType("home",  ButtonBar.ButtonData.CANCEL_CLOSE);
                            Alert winner = new Alert(Alert.AlertType.CONFIRMATION);
                            try {
                                Media sound = new Media(getClass().getResource("/winning.mp3").toURI().toString());
                                MediaPlayer mediaPlayer = new MediaPlayer(sound);
                                mediaPlayer.play();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            winner.setTitle("Winner!");
                            winner.getButtonTypes().setAll(REMATCH, HOME);
                            winner.setHeaderText(((Message) data).getMessage());
                            Optional<ButtonType> result = winner.showAndWait();
                            if (result.isPresent() && result.get() == REMATCH) {
                                    clientConnection.send(new Message(Message.challenge, "", currentUser, opponent));
                            }
                            else if(result.isPresent() && result.get() == HOME) {
                                primaryStage.setScene(sceneMap.get("home"));
                                clientConnection.send(new Message(Message.challengeResponse, "Decline", currentUser, opponent));

                            }
                        }
                        else if (((Message) data).getMessage().contains("Draw!")) {
                            Alert draw = new Alert(Alert.AlertType.CONFIRMATION);
                            ButtonType REMATCH = new ButtonType("rematch", ButtonBar.ButtonData.OK_DONE);
                            ButtonType HOME = new ButtonType("home",  ButtonBar.ButtonData.CANCEL_CLOSE);
                            draw.setTitle("Draw!");
                            draw.getButtonTypes().setAll(REMATCH, HOME);
                            draw.setHeaderText(((Message) data).getMessage());
                            Optional<ButtonType> result = draw.showAndWait();
                            if (result.isPresent() && result.get() == REMATCH) {
                                clientConnection.send(new Message(Message.challenge, "", currentUser, opponent));
                            }
                            else if(result.isPresent() && result.get() == HOME) {
                                primaryStage.setScene(sceneMap.get("home"));
                                clientConnection.send(new Message(Message.challengeResponse, "Decline", currentUser, opponent));

                            }
                        }
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
                        if (primaryStage.getScene() == sceneMap.get("game"))
                        {
                            //messagesList.getItems().add(((Message) data).getClient() +": " + ((Message) data).getMessage());
                            chatListMap.get(((Message) data).getClient()).getItems().add(((Message) data).getClient() +": " + ((Message) data).getMessage());
                            Platform.runLater(() -> messagesList.scrollTo(messagesList.getItems().size() - 1));

                        }
                        else {
                            Stage currChat = chatsystem(currReceiver);
                            currChat.show();
                            chatListMap.get(currReceiver).getItems().add(currReceiver + ": " + ((Message) data).getMessage());  //receiver side

                        }
                    } else if (((Message) data).getMsgType().equals(Message.startGame)) {
                        inGame = true;
                        currentTurn = "RED";
                        playerColor = ((Message) data).getMessage();
                        opponent= ((Message) data).getTarget() ;
                        sceneMap.put("game", createGameGUI(((Message) data).getTarget()));
                        primaryStage.setScene(sceneMap.get("game"));

                    } else if(((Message) data).getMsgType().equals(Message.challengeResponse)){
                        if(((Message) data).getMessage().equals("Accept")){
                            playbuttonMap.get(((Message) data).getTarget()).setDisable(false);
                        } else{
                            if (primaryStage.getScene() == sceneMap.get("game")){
                                inGame = false;
                                primaryStage.setScene(sceneMap.get("home"));
                            }
                            else {
                                inGame = false;
//                                primaryStage.setScene(sceneMap.get("home"));
//                                String challengerName = ((Message) data).getClient();
//                                if (!challengerName.equals(currentUser)) {
//                                    playbuttonMap.get(opponent).setDisable(false);
//                                }
                                if (playbuttonMap.containsKey(opponent)) {
                                    playbuttonMap.get(opponent).setDisable(false);
                                }
                            }
                        }
                    }
                }
                else if (data instanceof Move) {
                    updateBoard((Move) data);
                }
                });
        });

        clientConnection.start();

        loginMap = new HashMap<String, Scene>();
        loginMap.put("login", loginGUI());

        playbuttonMap = new HashMap<>();

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
        boolean open = false;
        boolean close = true;
        userNameLabel = new Label();
        root = new BorderPane();
        board = new GridPane();
        board = buildBoard();
        BorderPane slidingPane = new BorderPane();
        Button users = new Button("Open Player List");
        HBox topBar = new HBox(userNameLabel,users);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPrefHeight(10);
        topBar.setPadding(new Insets(0,10, 0 ,10));
        topBar.setMargin(users,new Insets(0,10, 0 ,625));

        userNameLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #ffffff");
        topBar.setStyle("-fx-background-color: rgba(0,0,0,.75);"); //this is the top bar
        BorderPane.setMargin(board, new Insets(100, 0, 100, 50));
        clientList = new VBox();
        clientList.setPrefWidth(200);
        userPane = new ScrollPane(clientList);      //created a scroll pane and itll adjust accordingly
        userPane.setMaxWidth(150);
        userPane.setMaxHeight(700);
        userPane.setStyle("-fx-background-color: linear-gradient(to bottom, #f5f5f5, #bdbdbd);");
        slidingPane.setCenter(userPane);
        slidingPane.setLeft(users);
        users.setOnAction(event -> {
            TranslateTransition userPaneSlide = new TranslateTransition(Duration.millis(300),  slidingPane);
            if (isSlidePaneOpen){
                userPaneSlide.setToX(200); //slides to the left
                isSlidePaneOpen = false;
            }
            else{
                userPaneSlide.setToX(5);
                isSlidePaneOpen = true;
            }
            userPaneSlide.play();
        });
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #f5f5f5, #bdbdbd);");
        root.setLeft(board);
        root.setRight(slidingPane);
        root.setTop(topBar);
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
            messagesList.scrollTo(messagesList.getItems().size() - 1);
            messageField.clear();
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
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #f5f5f5, #bdbdbd);"+"-fx-font-family: 'serif';");

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
        loginVbox.setStyle("-fx-background-color: linear-gradient(to bottom, #f5f5f5, #bdbdbd);"+"-fx-font-family: 'serif';");
        root.setCenter(loginVbox);
        loginVbox.setAlignment(Pos.CENTER);
        loginVbox.setMaxWidth(300);

        return new Scene(root, 800, 600);
    }
    public Scene createGameGUI(String target){
        root = new BorderPane();
        errormsg =  new Label();
        Label RED_PLAYER; // THIS IS THE CHALLENGER IT GOES ON TOPBOARD
        Label BLACK_PLAYER;
        if (playerColor.equals("RED")){
            RED_PLAYER = new Label(currentUser);
            BLACK_PLAYER = new Label(target);
        }
        else {
            RED_PLAYER = new Label(target);
            BLACK_PLAYER = new Label(currentUser);
        }
        RED_PLAYER.setStyle("-fx-text-fill: white");
        BLACK_PLAYER.setStyle("-fx-text-fill: white");
        HBox topBoard = new HBox(RED_PLAYER);
        topBoard.setAlignment(Pos.CENTER_LEFT);
        topBoard.setPrefHeight(10);
        topBoard.setMaxWidth(360);
        topBoard.setPadding(new Insets(0,0, 0 ,10));

        HBox botBoard = new HBox(BLACK_PLAYER);
        botBoard.setAlignment(Pos.CENTER_LEFT);
        botBoard.setPrefHeight(10);
        botBoard.setMaxWidth(360);
        botBoard.setPadding(new Insets(0,0, 0 ,10));
        HBox topBar = new HBox(userNameLabel);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPrefHeight(10);
        topBar.setPadding(new Insets(0,10, 0 ,10));

        userNameLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #ffffff");
        topBar.setStyle("-fx-background-color: rgba(0,0,0,.75);"); //this is the top bar


        topBoard.setStyle("-fx-background-color: rgba(0,0,0,.75);"); //top of the board
        botBoard.setStyle("-fx-background-color: rgba(0,0,0,.75);"); //bottom of the board

        Button resignButton = new Button("Resign");
        VBox chatBox = buildChatBox(target);
        resignButton.setOnAction(e -> {
            clientConnection.send(new Message(Message.serverMessage, "Resign", currentUser, opponent));
        });
        HBox idk =  new HBox(10, turnLabel, resignButton);
        board = buildBoard();
        updateTurnIndicator();
        VBox boardBox = new VBox(idk,topBoard,board,botBoard);
        root.setCenter(boardBox);
        root.setRight(chatBox);
        root.setTop(topBar);
        BorderPane.setMargin(boardBox, new Insets(100, 0, 100, 50));
        BorderPane.setMargin(chatBox, new Insets(100, 50, 100, 0));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #f5f5f5, #bdbdbd);");

        return new Scene(root,800,600);
    }

    private VBox buildChatBox(String target){
        messagesList = new ListView<String>();
        chatListMap.put(target,messagesList);       //stores this message list with the user's name
        messageField = new TextField();
        messagePane = new ScrollPane(messagesList);
        emojiPane = new FlowPane();
        emojiPane.setVisible(false);
        sendButton = new Button("Send");
        emojiButton = new Button("emoji");
//        messagePane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); messagePane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        messagePane.setMaxHeight(300);
        messagePane.setStyle("-fx-background-color: transparent; -fx-vbar-policy: never;");
        messageField.setPrefWidth(200);
        messageBox = new HBox(10, messageField, sendButton, emojiButton);
        chatVbox = new VBox(10, messagePane, emojiPane ,messageBox);
        chatVbox.setAlignment(Pos.CENTER);

        Image laugh = new Image(getClass().getResourceAsStream("/laughingemoji.png"));
        ImageView laughView = new ImageView(laugh);
        laughView.setFitWidth(25);
        laughView.setFitHeight(25);
        Button laughBtn = new Button("");
        laughBtn.setGraphic(laughView);

        Image angry = new Image(getClass().getResourceAsStream("/angryemoji.png"));
        ImageView angryView = new ImageView(angry);
        angryView.setFitWidth(25);
        angryView.setFitHeight(25);
        Button angryBtn = new Button("");
        angryBtn.setGraphic(angryView);

        emojiPane.getChildren().add(laughBtn);
        emojiPane.getChildren().add(angryBtn);

        laughBtn.setOnAction(i ->{
            Message currMessage = new Message(Message.sendToIndvidual, "😂", currentUser, target);
            clientConnection.send(currMessage);
            messagesList.getItems().add(currentUser + ": " + "😂");
            Platform.runLater(() -> messagesList.scrollTo(messagesList.getItems().size() - 1));
        });

        angryBtn.setOnAction(o ->{
            Message currMessage = new Message(Message.sendToIndvidual, "🤬", currentUser, target);
            clientConnection.send(currMessage);
            messagesList.getItems().add(currentUser + ": " + "🤬");
            Platform.runLater(() -> messagesList.scrollTo(messagesList.getItems().size() - 1));
        });

        emojiButton.setOnAction(e->{
            emojiPane.setVisible(!emojiPane.isVisible());
        });

        sendButton.setOnAction(e->{
            Message currMessage = new Message(Message.sendToIndvidual, messageField.getText(), currentUser, target);    //Creates the message to individual
            clientConnection.send(currMessage);
            messagesList.getItems().add(currentUser + ": " + messageField.getText());
            Platform.runLater(() -> messagesList.scrollTo(messagesList.getItems().size() - 1));
            messageField.clear();
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
        Circle piece = null;
        for (Node n : oldSquare.getChildren()) {
            if (n instanceof Circle) {
                piece = (Circle) n;
                break;
            }
        }
        newSquare.getChildren().add(piece);
        oldSquare.getChildren().remove(piece);
        while (oldSquare.getChildren().size() > 1) {
            oldSquare.getChildren().remove(1);
        }
        oldSquare.setUserData(null);
        newSquare.setUserData(move.getPiece());
        tempBoard[move.getnRow()][move.getnCol()] = move.getPiece();
        tempBoard[move.getpRow()][move.getpCol()] = null;
        if (isJump) {
            tempBoard[midRow][midCol] = null;
        }
        if (move.getPiece().getPieceType() == Pieces.PieceType.KING){
            if (move.getPiece().getColor() == Pieces.Color.RED){
                Image redCrown = new Image(getClass().getResourceAsStream("/redCrown.png"));
                ImageView crownView = new ImageView(redCrown);
                crownView.setFitWidth(20);
                crownView.setFitHeight(20);
                newSquare.getChildren().add(crownView);
            }
            else if (move.getPiece().getColor() == Pieces.Color.BLACK){
                Image blackCrown = new Image(getClass().getResourceAsStream("/blackCrown.png"));
                ImageView crownView = new ImageView(blackCrown);
                crownView.setFitWidth(20);
                crownView.setFitHeight(20);
                newSquare.getChildren().add(crownView);
            }
        }

        if (isJump && middleSquare != null && middleSquare.getChildren().size() > 1) {
            while (middleSquare.getChildren().size() > 1) {
                middleSquare.getChildren().remove(1);
            }
            middleSquare.setUserData(null);
        }
        if (move.getPiece().getColor() == Pieces.Color.RED) {//switch turns
            currentTurn = "BLACK";
        } else {
            currentTurn = "RED";
        }
        updateTurnIndicator();
        return newSquare;
    }

    private StackPane buildSquare(int row, int col){
        StackPane square = new StackPane();
        Rectangle rectangle = new Rectangle(45,45);
        if ((row+col)%2 != 0){
            rectangle.setFill(Color.rgb(0,0,0));
            rectangle.setStyle(" -fx-border-color: black");
        }
        else {
            rectangle.setFill(Color.rgb(255,255,255));
            rectangle.setStyle(" -fx-border-color: black");
        }
        square.getChildren().add(rectangle);
        if ((row+col)%2 != 0) {
            if (row < 3) {
                square.getChildren().add(buildRedPiece());
                square.setUserData(new Pieces(Pieces.Color.RED));
                tempBoard[row][col] = new Pieces(Pieces.Color.RED);

            } else if (row > 4) {
                square.getChildren().add(buildBlackPiece());
                square.setUserData(new Pieces(Pieces.Color.BLACK));
                tempBoard[row][col] = new Pieces(Pieces.Color.BLACK);

            }
        }
        square.setOnDragDetected(e -> {
            if (square.getUserData() != null &&
                    ((Pieces) square.getUserData()).getColor().toString().equals(playerColor)) {
               Checkers.Board clientBoard = new Checkers.Board(tempBoard);
               Checkers.Rules clientRules = new Checkers.Rules(clientBoard);
                WritableImage pieceHeld = square.snapshot(null, null); //get the square clients click and drag
                Dragboard db = square.startDragAndDrop(TransferMode.MOVE);
                db.setDragView(pieceHeld);
                ClipboardContent content = new ClipboardContent();
                content.putString(row + "," + col);
                db.setContent(content);
                selectedPiece = (Pieces) square.getUserData();
                pRow = row;
                pCol = col;

                ArrayList<int[]> validMovesList = new ArrayList<>();
                validMovesList = clientRules.validMoves(pRow, pCol, selectedPiece.getColor());
                System.out.println("Valid moves count: " + validMovesList.size());
                for (int[] m : validMovesList) {
                    System.out.println("Valid move: row=" + m[0] + " col=" + m[1]);
                }
                System.out.println("Board children count: " + board.getChildren().size());
                System.out.println("Valid moves: " + validMovesList.size());
                for (Node n : board.getChildren()) {
                    for (int[] c : validMovesList) {
                        if (GridPane.getColumnIndex(n).equals(c[1]) && GridPane.getRowIndex(n).equals(c[0])) {
                            StackPane s = (StackPane) n;
                            Circle highlight = new Circle(15);
                            highlight.setId("highlight");
                            highlight.setFill(Color.rgb(255, 255, 255, 0.5));
                            highlight.setEffect(new Glow(1.0));
                            s.getChildren().add(highlight);
                        }
                    }
                }
                e.consume();
            }
        });
        square.setOnDragOver(e -> {
            if (e.getGestureSource() != square && e.getDragboard().hasString()) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
            e.consume();
        });
        square.setOnDragDone(e -> {
            for (Node n : board.getChildren()) {
                StackPane s = (StackPane) n;
                s.getChildren().removeIf(child -> child instanceof Circle && "highlight".equals(child.getId()));
            }
            e.consume();
        });
        square.setOnDragDropped(e -> {
            nRow = row;
            nCol = col;
            Move move = new Move(selectedPiece, pRow, pCol, nRow, nCol);
            clientConnection.send(move);
            for (Node n : board.getChildren()) {
                StackPane s = (StackPane) n;
                if (s.getUserData() == null) {
                    while (s.getChildren().size() > 1) {
                        s.getChildren().remove(1);
                    }
                }
            }
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
        Circle redCircle = new Circle(20);
        redCircle.setFill(Color.rgb(168,43,43));
        redCircle.setStroke(Color.rgb(120,20,20));
        redCircle.setStrokeWidth(4);
        return redCircle;
    }

    private Circle buildBlackPiece(){
        Circle blackCircle = new Circle(20);
        blackCircle.setFill(Color.rgb(75,75,75));
        blackCircle.setStroke(Color.rgb(100, 100, 100));
        blackCircle.setStrokeWidth(4);
        return blackCircle;
    }
    private void updateTurnIndicator() {
        if (currentTurn.equals("RED")) {
            turnLabel.setText("Red's Turn");
            turnLabel.setStyle("-fx-text-fill: rgb(168,43,43); -fx-font-size: 16px; -fx-font-weight: bold;");
        } else {
            turnLabel.setText("Black's Turn");
            turnLabel.setStyle("-fx-text-fill: rgb(75,75,75); -fx-font-size: 16px; -fx-font-weight: bold;");
        }
    }
}
