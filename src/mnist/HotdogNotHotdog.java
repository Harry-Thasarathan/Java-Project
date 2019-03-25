package mnist;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.jfree.util.Log;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.io.ClassPathResource;

import java.io.File;
import java.io.IOException;

import static javafx.application.Application.launch;

public class HotdogNotHotdog extends Application {
    private static MultiLayerNetwork net2;
    private BorderPane borderPane = null;
    private INDArray feed_image = null;
    private File selectedFile = null;
    private TextArea textArea = null;




    //Creating a UI for the HotdogNotHotdog
    public void start(Stage primaryStage) throws  Exception {

        //Creating the panes needed for the program
        borderPane = new BorderPane();
        FlowPane buttonPane = new FlowPane();

        //Creating a Button for the program
        Button catButton = new Button("Check if Cat or Dog!");
        catButton.translateXProperty().setValue(80);
        //Adding Button to the flowPane
        buttonPane.getChildren().add(catButton);
        buttonPane.setAlignment(Pos.CENTER);

        //Adding it to the borderPane
        borderPane.setBottom(buttonPane);

        //Creating a MenuBar
        MenuBar menuBar = new MenuBar();

        //Creating a Menu
        Menu file = new Menu("File");

        //Items in the MenuBar
        MenuItem menuItemNew = new MenuItem("New");
        MenuItem menuItemOpen = new MenuItem("Open");
        MenuItem menuItemExit = new MenuItem("Exit");

        //Adding it to the Menu File
        file.getItems().addAll(menuItemNew, menuItemOpen, menuItemExit);

        //Adding it to the menuBar
        menuBar.getMenus().add(file);

        //Adding it to the pane
        borderPane.setTop(menuBar);

        //Creating a textArea;
        textArea = new TextArea();
        textArea.setPrefColumnCount(8);
        borderPane.setLeft(textArea);
        textArea.setEditable(false);


        //Creating the functionalities of the menuItems
        menuItemNew.setOnAction(e->{
            INDArray feed_image = null;
            Image imageDisplay = null;
            ImageView imageView = new ImageView(imageDisplay);
            borderPane.setCenter(imageView);
            feed_image = null;

            textArea.clear();
        });

        menuItemOpen.setOnAction(e->{
            FileChooser fileChooser = new FileChooser();
            selectedFile = fileChooser.showOpenDialog(primaryStage);

            if (selectedFile != null){
                Image image1 = new Image(selectedFile.toURI().toString());

                //Outputting image and making it look better
                ImageView imageView = new ImageView(image1);
                imageView.setFitWidth(400);
                imageView.setFitHeight(400);
                Circle circleClip = new Circle(200, 200, 200);
                borderPane.setCenter(imageView);
                borderPane.getCenter().setClip(circleClip);

            }
        });

        menuItemExit.setOnAction(e->{
            //Change this later to the main menu
            Platform.exit();

        });


        //Adding the buttons Functionlility
        catButton.setOnAction(e->{
            //Making label
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    String fullModel = new ClassPathResource("model.h5").getFile().getPath();
                    net2 = KerasModelImport.importKerasSequentialModelAndWeights(fullModel, false);
                    NativeImageLoader loader = new NativeImageLoader(128, 128, 3, true);
                    INDArray feed_image = loader.asMatrix(new org.datavec.api.util.ClassPathResource(selectedFile.getName()).getFile());
                    ImagePreProcessingScaler scaler = new ImagePreProcessingScaler(0, 1);
                    scaler.transform(feed_image);
                    INDArray output = net2.output(feed_image);




                    if (output.maxNumber().doubleValue() >= 0.5){
                        textArea.appendText("It's a CAT!");
                    }
                    else{
                        textArea.appendText("It's a DOG!");
                    }

                    return null;
                }

            };

            //If the task Failed gives a log error
            task.setOnFailed(ex->{
                Throwable exception = ex.getSource().getException();
                if (exception != null){
                    Log.error(exception);
                }
            });
            //Starting the Task
            new Thread(task).start();


        });


        Scene scene = new Scene(borderPane, 1000, 600);
        scene.getStylesheets().add("HotdogNotHotdog.css");
        primaryStage.setTitle("Cat or not a Dog");
        primaryStage.setScene(scene);
        primaryStage.show();

    }
    public static void main(String[] args) {
        launch(args);
    }
}
