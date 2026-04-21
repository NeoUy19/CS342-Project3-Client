import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {
    private String client;
    private String target;
    private String message;
    private String msgType;
    private ArrayList<String> groupMembers;
    //Message types
    public static final String serverMessage = "Server message";
    public static final String createUser = "Create a username!";
    public static final String sendToIndvidual = "Send to Individual";
    public static final String sendToGroup = "Send to Group";
    public static final String sendToAll = "Send to All";
    public static final String error = "Error";
    public static final String userList = "User list";
    public static final String status = "Status";
    public static final String challenge = "challenge";
    public static final String challengeResponse = "challengeResponse";
    public static final String startGame = "start game";


    public Message(String msgType, String client) { //Create a user
        this.msgType = msgType;
        this.client = client;
    }

    public Message(String msgType, String message, String client) { //Send to all
        this.msgType = msgType;
        this.message = message;
        this.client = client;
    }


    public Message(String msgType , String message, String client, String target){ //send to individual
        this.message = message;
        this.client = client;
        this.target = target;
        this.msgType = msgType;
    }
    public Message(String msgType, String message, String client, ArrayList<String> groupMembers) { //send to group
        this.msgType = msgType;
        this.message = message;
        this.client = client;
        this.groupMembers = groupMembers;
    }

    public String getClient() {
        return client;
    }

    public String getTarget() {
        return target;
    }

    public String getMessage() {
        return message;
    }

    public String getMsgType() {
        return msgType;
    }

    public ArrayList<String> getGroupMembers(){
        return groupMembers;
    }
    @Override
    public String toString() {
        return client + ": " + message;
    }
}
