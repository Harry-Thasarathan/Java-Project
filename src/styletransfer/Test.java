package org.deeplearning4j.examples.styletransfer;

import java.awt.image.BufferedImage;
import java.io.File;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicReference;

import java.awt.*;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;

import javax.imageio.ImageIO;


public class Test extends  Application {

    //Getting WebCam info from saxors applcation
    private class WebCamInfo {

        private String webCamName;
        private int webCamIndex;

        public String getWebCamName() {
            return webCamName;
        }

        public void setWebCamName(String webCamName) {
            this.webCamName = webCamName;
        }

        public int getWebCamIndex() {
            return webCamIndex;
        }

        public void setWebCamIndex(int webCamIndex) {
            this.webCamIndex = webCamIndex;
        }

        @Override
        public String toString() {
            return webCamName;
        }
    }

    //Variables needed for the program
    private BorderPane root;
    private FlowPane topPane;
    private FlowPane buttonControlsPane;
    private ImageView imgWebCamCapturedImage;
    private Webcam webCam = null;
    private boolean stopCamera = false;

    private BufferedImage grabbedImage;
    public BufferedImage styleImage;

    private ObjectProperty<Image> imageProperty = new SimpleObjectProperty<Image>();
    private BorderPane webCamPane;
    private Button btnCamreaStop;
    private Button btnCamreaStart;
    private Button btnCameraDispose;
    private Button btnCaptureImage;
    private Button btnStyle;
    private int capCounter = 0;
    private String pathfile = "";

    //Creating a Custom nonStandard Resolution
    private Dimension[] nonStandardResolutions = new Dimension[] {
            WebcamResolution.PAL.getSize(),
            WebcamResolution.HD720.getSize(),
            new Dimension(2000, 1000),
            new Dimension(1000, 500),
    };

    @Override
    public void start(Stage primaryStage) throws Exception {

        //Creating a structure for the project
        root = new BorderPane();

        //Top Pane
        topPane = new FlowPane();
        topPane.setAlignment(Pos.CENTER);
        topPane.setHgap(20);
        topPane.setOrientation(Orientation.HORIZONTAL);
        topPane.setPrefHeight(40);
        root.setTop(topPane);

        //WebCam Pane
        webCamPane = new BorderPane();
        imgWebCamCapturedImage = new ImageView();
        webCamPane.setCenter(imgWebCamCapturedImage);
        root.setCenter(webCamPane);
        createTopPanel();


        //Buttons for the webcam program
        buttonControlsPane = new FlowPane();
        buttonControlsPane.setOrientation(Orientation.HORIZONTAL);
        buttonControlsPane.setAlignment(Pos.CENTER);
        buttonControlsPane.setHgap(20);
        buttonControlsPane.setVgap(10);
        buttonControlsPane.setPrefHeight(40);
        buttonControlsPane.setDisable(true);
        createCameraControls();
        root.setBottom(buttonControlsPane);


        //Creating a Custom nonStandard Resolution
        Dimension[] nonStandardResolutions = new Dimension[] {
                WebcamResolution.PAL.getSize(),
                WebcamResolution.HD720.getSize(),
                new Dimension(2000, 1000),
                new Dimension(1000, 500),
        };

        /*webcam
        Webcam webcam = Webcam.getDefault();
        webcam.setCustomViewSizes(nonStandardResolutions);
        webcam.setViewSize(WebcamResolution.HD720.getSize());
        webcam.open();
        ImageIO.write(webcam.getImage(), "PNG", new File("hello-world.png"));
        webcam.close();
        */
        Scene scene = new Scene(root,1000, 1000);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();

        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                setImageViewSize();
            }
        });

    }

    protected void setImageViewSize(){
        double height = webCamPane.getHeight();
        double width = webCamPane.getWidth();

        imgWebCamCapturedImage.setFitHeight(height);
        imgWebCamCapturedImage.setFitWidth(width);
        imgWebCamCapturedImage.prefHeight(height);
        imgWebCamCapturedImage.prefWidth(width);
        imgWebCamCapturedImage.setPreserveRatio(true);

    }

    private void createTopPanel() {

        int webCamCounter = 0;
        Label lbInfoLabel = new Label("Select Your WebCam Camera");
        ObservableList<WebCamInfo> options = FXCollections.observableArrayList();

        topPane.getChildren().add(lbInfoLabel);

        for (Webcam webcam : Webcam.getWebcams()) {
            WebCamInfo webCamInfo = new WebCamInfo();
            webCamInfo.setWebCamIndex(webCamCounter);
            webCamInfo.setWebCamName(webcam.getName());
            options.add(webCamInfo);
            webCamCounter++;
        }

        ComboBox<WebCamInfo> cameraOptions = new ComboBox<WebCamInfo>();
        cameraOptions.setItems(options);
        cameraOptions.setPromptText("Choose Camera");
        cameraOptions.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<WebCamInfo>() {

            @Override
            public void changed(ObservableValue<? extends WebCamInfo> arg0, WebCamInfo arg1, WebCamInfo arg2) {
                if (arg2 != null) {
                    System.out.println("WebCam Index: " + arg2.getWebCamIndex() + ": WebCam Name:" + arg2.getWebCamName());
                    initializeWebCam(arg2.getWebCamIndex());
                }
            }
        });
        topPane.getChildren().add(cameraOptions);
    }
    protected void initializeWebCam(final int webCamIndex) {

        Task<Void> webCamTask = new Task<Void>() {

            @Override
            protected Void call() throws Exception {

                if (webCam != null) {
                    disposeWebCamCamera();
                }

                webCam = Webcam.getWebcams().get(webCamIndex);
                webCam.open();

                startWebCamStream();

                return null;
            }
        };

        Thread webCamThread = new Thread(webCamTask);
        webCamThread.setDaemon(true);
        webCamThread.start();

        buttonControlsPane.setDisable(false);
        btnCamreaStart.setDisable(true);
    }
    protected void startWebCamStream() {

        stopCamera = false;

        Task<Void> task = new Task<Void>() {

            @Override
            protected Void call() throws Exception {

                final AtomicReference<WritableImage> ref = new AtomicReference<>();
                BufferedImage img = null;

                while (!stopCamera) {
                    try {
                        if ((img = webCam.getImage()) != null) {

                            ref.set(SwingFXUtils.toFXImage(img, ref.get()));
                            img.flush();

                            Platform.runLater(new Runnable() {

                                @Override
                                public void run() {
                                    imageProperty.set(ref.get());
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                return null;
            }
        };

        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
        imgWebCamCapturedImage.imageProperty().bind(imageProperty);

    }

    private void createCameraControls() {

        //Button Controls for stop webCam
        btnCamreaStop = new Button();
        btnCamreaStop.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {

                stopWebCamCamera();
            }
        });
        btnCamreaStop.setText("Stop Camera");

        //Button Controls for Start webCam
        btnCamreaStart = new Button();
        btnCamreaStart.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                startWebCamCamera();
            }
        });
        btnCamreaStart.setText("Start Camera");

        //Button Controls for Dispose webCam
        btnCameraDispose = new Button();
        btnCameraDispose.setText("Dispose Camera");
        btnCameraDispose.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                disposeWebCamCamera();
            }
        });

        //Button Controls for capture image
        btnCaptureImage = new Button();
        btnCaptureImage.setText("Take a picture!");
        styleImage = null;
        btnCaptureImage.setOnAction(e->{
            try {
                //styleImage = webCam.getImage();
                ImageIO.write(webCam.getImage(), "JPG", new File("D:\\Tamilesh\\Documents\\Year 2\\Semester 2\\SoftwareInt\\Final Project\\DL4J\\dl4j-examples\\dl4j-examples\\src\\main\\resources\\styletransfer\\image" + capCounter + ".png"));
            }
            catch (Exception ex){
                System.out.println("ERROR");
            }
            capCounter++;
            pathfile = "image" + capCounter + ".png";
        });


        //Stylizing the Image
        btnStyle = new Button();
        btnStyle.setText("Stylize it!");
        btnStyle.setOnAction(e->{
            try {
                NeuralStyleTransfer.main(null);
            }
            catch (Exception err){
                System.out.println("ERROR");
            }
        });


        //Adding to the buttonControlsPane
        buttonControlsPane.getChildren().add(btnCamreaStart);
        buttonControlsPane.getChildren().add(btnCamreaStop);
        buttonControlsPane.getChildren().add(btnCameraDispose);
        buttonControlsPane.getChildren().add(btnCaptureImage);
        buttonControlsPane.getChildren().add(btnStyle);
    }

    protected void disposeWebCamCamera() {
        stopCamera = true;
        webCam.close();
        btnCamreaStart.setDisable(true);
        btnCamreaStop.setDisable(true);
    }

    protected void startWebCamCamera() {
        stopCamera = false;
        startWebCamStream();
        btnCamreaStop.setDisable(false);
        btnCamreaStart.setDisable(true);
    }

    protected void stopWebCamCamera() {
        stopCamera = true;
        btnCamreaStart.setDisable(false);
        btnCamreaStop.setDisable(true);
    }




    public static void main(String[] args) {
        launch(args);
    }
}
