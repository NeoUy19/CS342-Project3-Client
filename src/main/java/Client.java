import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;
import Checkers.Move;
import Checkers.Pieces;
public class Client extends Thread{

    Socket socketClient;
    ObjectOutputStream out;
    ObjectInputStream in;
    private Consumer<Serializable> callback;

    Client(Consumer<Serializable> call){
        callback = call;
    }

    public void run() {
        try {
            socketClient= new Socket("127.0.0.1",5555);
            out = new ObjectOutputStream(socketClient.getOutputStream());
            in = new ObjectInputStream(socketClient.getInputStream());
            socketClient.setTcpNoDelay(true);
        }
        catch(Exception e) {}

        while(true) {
            try {
                Object receieved = in.readObject();
                if (receieved instanceof Message){
                    Message message = (Message) receieved;
                    callback.accept(message);
                }
                else if (receieved instanceof Move) {
                    Move move = (Move) receieved;
                    callback.accept(move);
                }
            }
            catch(Exception e) {}
        }
    }

    public void send(Serializable data) {
        try {
            out.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}