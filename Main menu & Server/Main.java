package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.text.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.*;
import java.net.*;

public class Main extends Application {

    private Text title;
    private Button program_1;
    private Button program_2;
    private Button exit_btn;
    private DataOutputStream toServer = null;
    private DataInputStream fromServer = null;



    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage)throws FileNotFoundException {
        primaryStage.setTitle("Intro to Computer Vision (CSCI 2020)");

        HBox hbox = new HBox();

        VBox vbox = new VBox(); //creating our pane
        hbox.setPadding(new Insets(10,10,10,10));
        vbox.setSpacing(45);

        title = new Text();
        title.setText("Main Menu");
        title.setFont(new Font("Arial", 20));

        vbox.getChildren().add(title);

        program_1 = new Button("insta simulator"); //creating the button object
        program_2 = new Button("writing reader");
        exit_btn = new Button("Exit");
        program_1.setPrefWidth(200);
        program_2.setPrefWidth(200);
        exit_btn.setPrefWidth(200);
        program_1.setMinWidth(200);
        program_2.setMinWidth(200);
        exit_btn.setMinWidth(200);
        program_1.setDefaultButton(true);
        program_2.setDefaultButton(true);
        exit_btn.setDefaultButton(true);
        vbox.getChildren().addAll(program_1, program_2, exit_btn);
        vbox.setAlignment(Pos.CENTER);

        exit_btn.setOnAction(e ->{//reading when the button is pressed
            Platform.exit();
        });

        VBox vbox2 = new VBox();
        vbox2.setPadding(new Insets(85,0,0,0));

        Text test = new Text();
        test.setText("Welcome\n\n" +
                "This is our final project for CSCI2020\n" +
                "we hope you enjoy it, and wish you a great day.\n\n" +
                "Our First Program allows you to edit and add\n" +
                "filters to pictures taken with a webcam using a\n" +
                "slider.\n\n" +
                "Our second program reads an input written by \n" +
                "hand and returns that value as processed by the\n" +
                "computer.\n\n" +
                "Hover over the buttons for previews.");
        vbox2.setAlignment(Pos.TOP_CENTER);
        vbox2.setSpacing(20);
        vbox2.getChildren().add(test);

        Image image1 = new Image(new FileInputStream("C:\\Users\\Tommy\\Pictures\\scaled.png"));
        Image image2 = new Image(new FileInputStream("C:\\Users\\Tommy\\Pictures\\scaled2.png"));
        ImageView imageview1 = new ImageView(image1);
        ImageView imageview2 = new ImageView(image2);
        EventHandler<MouseEvent> eventHandler1 = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                test.setText("Preview for Image Filter");
                vbox2.getChildren().add(imageview1);
            }
        };
        EventHandler<MouseEvent> eventHandler2 = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                test.setText("Preview for Written Text Reader");
                vbox2.getChildren().add(imageview2);

            }
        };
        EventHandler<MouseEvent> eventHandlerClear = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                test.setText("Welcome\n\n" +
                        "This is our final project for CSCI2020\n" +
                        "we hope you enjoy it, and wish you a great day.\n\n" +
                        "Our First Program allows you to edit and add\n" +
                        "filters to pictures taken with a webcam using a\n" +
                        "slider.\n\n" +
                        "Our second program reads an input written by \n" +
                        "hand and returns that value as processed by the\n" +
                        "computer.\n\n" +
                        "Hover over the buttons for previews.");
                vbox2.getChildren().remove(1);
            }
        };

        program_1.addEventFilter(MouseEvent.MOUSE_ENTERED, eventHandler1);
        program_1.addEventFilter(MouseEvent.MOUSE_EXITED, eventHandlerClear);
        program_2.addEventFilter(MouseEvent.MOUSE_ENTERED, eventHandler2);
        program_2.addEventFilter(MouseEvent.MOUSE_EXITED, eventHandlerClear);

        hbox.setSpacing(50);
        hbox.getChildren().addAll(vbox, vbox2);
        Scene scene1 = new Scene(hbox, 800, 450);
        primaryStage.setScene(scene1);
        primaryStage.show();

        Button btn_back = new Button("Back");
        btn_back.setDefaultButton(true);

        Pane pane1 = new Pane();
        Text insta_sim = new Text(380, 220, "put ur stuff here Tam!");
        pane1.getChildren().addAll(btn_back, insta_sim);
        Scene scene2 = new Scene(pane1, 800, 450);

        Button btn_back2 = new Button("Back");
        btn_back2.setDefaultButton(true);

        Pane pane = new Pane();
        Text text_reader = new Text(380, 220, "put ur stuff here Harry!");
        pane.getChildren().addAll(btn_back2, text_reader);
        Scene scene3 = new Scene(pane, 800, 450);

        try {
            Socket socket = new Socket("localhost", 8000);
            toServer = new DataOutputStream(socket.getOutputStream());
            fromServer = new DataInputStream(socket.getInputStream());
            int usernum = fromServer.readInt();
            btn_back.setOnAction(a ->{//reading when the button is pressed
                try {
                    toServer.writeInt(0);
                    toServer.writeInt(usernum);
                    primaryStage.setScene(scene1);
                }
                catch (IOException ex){
                    ex.printStackTrace();;
                }
            });


            btn_back2.setOnAction(a ->{//reading when the button is pressed
                try {
                    toServer.writeInt(0);
                    toServer.writeInt(usernum);
                    primaryStage.setScene(scene1);
                }
                catch (IOException ex){
                    ex.printStackTrace();;
                }
            });

            program_1.setOnAction(e ->{//reading when the button is pressed
                try {
                    toServer.writeInt(1);
                    toServer.writeInt(usernum);
                    primaryStage.setScene(scene2);
                    primaryStage.show();
                }
                catch (IOException ex){
                    ex.printStackTrace();
                }
            });
            program_2.setOnAction(e ->{//reading when the button is pressed
                try {
                    toServer.writeInt(2);
                    toServer.writeInt(usernum);
                    primaryStage.setScene(scene3);
                    primaryStage.show();
                }
                catch (IOException ex){
                    ex.printStackTrace();
                }
            });

        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}