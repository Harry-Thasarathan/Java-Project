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
    private Text text_status;
    private VBox vbox = new VBox();
    private ArrayList<Text> holder= new ArrayList<>();

    int connection = 0;

    @Override
    public void start(Stage primaryStage) {

        vbox.getChildren().add(text);
        Scene scene = new Scene(vbox);
        primaryStage.setTitle("Server");
        primaryStage.setScene(scene);
        primaryStage.show();

        new Thread( () -> {
            try {
                ServerSocket serverSocket = new ServerSocket(8000);

                text.appendText("server has begun running");

                while (true) {
                    Socket socket = serverSocket.accept();

                    connection++;

                    Platform.runLater( () -> {
                        try {
                            DataOutputStream send_to_client = new DataOutputStream(socket.getOutputStream());
                            send_to_client.writeInt(connection);
                        }
                        catch(IOException xe){
                            xe.printStackTrace();
                        }
                        text.appendText("\nNew thread\n");
                        InetAddress inetAddress = socket.getInetAddress();

                        text.appendText("Client#: " + connection + "\n" + "Client ip: "+inetAddress.getHostAddress() + '\n');
                        holder.add(new Text());
                        vbox.getChildren().addAll(holder.get(connection-1));
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

                    int a = inputFromClient.readInt();
                    int num = inputFromClient.readInt();
                    Platform.runLater(() -> {
                        if (a == 0){
                            holder.get(num-1).setText("user number "+num+" is at main menu");
                        }
                        else if (a == 1){
                            holder.get(num-1).setText("user number "+num+" is in insta sim");
                        }
                        else if (a == 2){
                            holder.get(num-1).setText("user number "+num+" is in number reader");
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
