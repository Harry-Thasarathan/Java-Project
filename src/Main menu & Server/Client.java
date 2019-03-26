package org.deeplearning4j.examples;

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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.text.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.deeplearning4j.examples.convolution.mnist.HotdogNotHotdog;
import org.deeplearning4j.examples.convolution.mnist.MnistClassifierUI;
import org.deeplearning4j.examples.styletransfer.NeuralStyleTransfer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.*;
import java.net.*;

public class Client extends Application {

    private Text title;
    private Button program_1;
    private Button program_2;
    private Button program_3;
    private Button exit_btn;
    private DataOutputStream toServer = null;
    private DataInputStream fromServer = null;



    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage)throws Exception {
        primaryStage.setTitle("Intro to Computer Vision (CSCI 2020)");

        HBox hbox = new HBox();

        VBox vbox = new VBox(); //creating our pane
        hbox.setPadding(new Insets(10,10,10,10));
        vbox.setSpacing(45);

        title = new Text();
        title.setText("Main Menu");
        title.setFont(new Font("Arial", 20));

        vbox.getChildren().add(title);

        program_1 = new Button("Style Transfer"); //creating the button object
        program_2 = new Button("Cat & Dog");
        program_3 = new Button("Digit Recognizer");
        exit_btn = new Button("Exit");
        program_1.setPrefWidth(200);
        program_2.setPrefWidth(200);
        program_3.setPrefWidth(200);
        exit_btn.setPrefWidth(200);
        program_1.setMinWidth(200);
        program_2.setMinWidth(200);
        program_3.setMinWidth(200);
        exit_btn.setMinWidth(200);
        program_1.setDefaultButton(true);
        program_2.setDefaultButton(true);
        program_3.setDefaultButton(true);
        exit_btn.setDefaultButton(true);
        vbox.getChildren().addAll(program_1, program_2, program_3, exit_btn);
        vbox.setAlignment(Pos.CENTER);


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

        Image image1 = new Image(new FileInputStream("D:\\Tamilesh\\Documents\\Year 2\\Semester 2\\SoftwareInt\\Final Project\\DL4J\\dl4j-examples\\dl4j-examples\\src\\main\\resources\\StyleTransfer.png"));
        Image image2 = new Image(new FileInputStream("D:\\Tamilesh\\Documents\\Year 2\\Semester 2\\SoftwareInt\\Final Project\\DL4J\\dl4j-examples\\dl4j-examples\\src\\main\\resources\\catndogscaled.png"));
        Image image3 = new Image(new FileInputStream("D:\\Tamilesh\\Documents\\Year 2\\Semester 2\\SoftwareInt\\Final Project\\DL4J\\dl4j-examples\\dl4j-examples\\src\\main\\resources\\scaled2.png"));
        ImageView imageview1 = new ImageView(image1);
        ImageView imageview2 = new ImageView(image2);
        ImageView imageview3 = new ImageView(image3);
        EventHandler<MouseEvent> eventHandler1 = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                test.setText("Preview for Style Transfer");
                vbox2.getChildren().add(imageview1);
            }
        };
        EventHandler<MouseEvent> eventHandler2 = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                test.setText("Preview for Cat & Dog");
                vbox2.getChildren().add(imageview2);

            }
        };
        EventHandler<MouseEvent> eventHandler3 = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                test.setText("Preview for Digit Recognizer");
                vbox2.getChildren().add(imageview3);

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
        program_3.addEventFilter(MouseEvent.MOUSE_ENTERED, eventHandler3);
        program_3.addEventFilter(MouseEvent.MOUSE_EXITED, eventHandlerClear);

        hbox.setSpacing(50);
        hbox.getChildren().addAll(vbox, vbox2);
        Scene scene1 = new Scene(hbox, 800, 450);
        scene1.getStylesheets().add("HotdogNotHotdog.css");
        primaryStage.setScene(scene1);
        primaryStage.show();






        try {
            Socket socket = new Socket("localhost", 8000);
            toServer = new DataOutputStream(socket.getOutputStream());
            fromServer = new DataInputStream(socket.getInputStream());
            int usernum = fromServer.readInt();

            program_1.setOnAction(e ->{//reading when the button is pressed
                try {
                    toServer.writeInt(1);
                    toServer.writeInt(usernum);
                    Stage menuStage = new Stage();
                    menuStage.initModality(Modality.WINDOW_MODAL);
                    menuStage.initOwner(primaryStage);
                    NeuralStyleTransfer neuralMenu = new NeuralStyleTransfer();
                    neuralMenu.start(menuStage);
                    menuStage.setOnHidden(a -> {
                        try {
                            toServer.writeInt(0);
                            toServer.writeInt(usernum);
                            primaryStage.setScene(scene1);
                        } catch (IOException ex) {

                        }
                    });

                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            });
            program_2.setOnAction(e ->{//reading when the button is pressed
                try {
                    toServer.writeInt(2);
                    toServer.writeInt(usernum);
                    Stage catStage = new Stage();
                    catStage.initModality(Modality.WINDOW_MODAL);
                    catStage.initOwner(primaryStage);
                    HotdogNotHotdog catndog = new HotdogNotHotdog();
                    catndog.start(catStage);
                    catStage.setOnHidden(a -> {
                        try {
                            toServer.writeInt(0);
                            toServer.writeInt(usernum);
                            primaryStage.setScene(scene1);
                        } catch (IOException ex) {

                        }
                    });
                    //primaryStage.setScene(scene3);
                    //primaryStage.show();
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            });
            program_3.setOnAction(e ->{//reading when the button is pressed
                try {
                    toServer.writeInt(3);
                    toServer.writeInt(usernum);
                    Stage numberStage = new Stage();
                    numberStage.initModality(Modality.WINDOW_MODAL);
                    numberStage.initOwner(primaryStage);
                    MnistClassifierUI numberhot = new MnistClassifierUI();
                    numberhot.start(numberStage);
                    numberStage.setOnHidden(a -> {
                        try {
                            toServer.writeInt(0);
                            toServer.writeInt(usernum);
                            primaryStage.setScene(scene1);
                        } catch (IOException ex) {

                        }
                    });
                    //primaryStage.setScene(scene3);
                    //primaryStage.show();
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            });
            exit_btn.setOnAction(e ->{//reading when the button is pressed
                try {
                    toServer.writeInt(4);
                    toServer.writeInt(usernum);
                    Platform.exit();
                }
                catch (IOException ex){
                    ex.printStackTrace();
                }
                Platform.exit();
            });
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
