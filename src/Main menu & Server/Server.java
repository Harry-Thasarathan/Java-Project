package sample;

import java.io.*;
import java.net.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import java.util.ArrayList;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.text.*;

public class Server extends Application {

    private TextArea text = new TextArea();
    private VBox vbox = new VBox();
    private ArrayList<Text> holder= new ArrayList<>(); //holding the text boxes

    int connection = 0;

    @Override
    public void start(Stage primaryStage) {

        vbox.getChildren().add(text);
        Scene scene = new Scene(vbox);
        primaryStage.setTitle("Server"); //creating a basic ui to interpret the server data
        primaryStage.setScene(scene);
        primaryStage.show();

        new Thread( () -> {
            try {
                ServerSocket serverSocket = new ServerSocket(8000); //creating the server socket

                text.appendText("server has begun running");

                while (true) {
                    Socket socket = serverSocket.accept(); //waiting for uer to connect

                    connection++;

                    Platform.runLater( () -> {
                        try {
                            DataOutputStream send_to_client = new DataOutputStream(socket.getOutputStream());
                            send_to_client.writeInt(connection); //giving the user an ID
                        }
                        catch(IOException xe){
                            xe.printStackTrace();
                        }
                        text.appendText("\nNew thread\n");
                        InetAddress inetAddress = socket.getInetAddress();

                        text.appendText("Client#: " + connection + "\n" + "Client ip: "+inetAddress.getHostAddress() + '\n');
                        holder.add(new Text());
                        vbox.getChildren().addAll(holder.get(connection-1)); //adding to the stage
                    });

                    new Thread(new HandleAClient(socket)).start();
                }
            }
            catch(IOException ex) {
                System.err.println(ex);
            }
        }).start();
    }

    class HandleAClient implements Runnable {
        private Socket socket;

        public HandleAClient(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {

                DataInputStream inputFromClient = new DataInputStream(socket.getInputStream());
                holder.get(connection-1).setText("user number "+connection+" is at main menu");
                while (true) {

                    int a = inputFromClient.readInt(); //reading info from the client. use info to print proper menu
                    int num = inputFromClient.readInt();
                    Platform.runLater(() -> {
                        if (a == 0){
                            holder.get(num-1).setText("User number "+num+" is at main menu");
                        }
                        else if (a == 1){
                            holder.get(num-1).setText("User number "+num+" is in Style Transfer");
                        }
                        else if (a == 2){
                            holder.get(num-1).setText("User number "+num+" is in Cat & Dog");
                        }
                        else if (a == 3){
                            holder.get(num-1).setText("User number "+num+" is in Digit Recognizer");
                        }
                        else if (a == 4){
                            holder.get(num-1).setText("User number "+num+" has gone offline");

                        }
                    });
                }
            }
            catch(IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
