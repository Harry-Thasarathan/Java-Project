package org.deeplearning4j.examples.convolution.mnist;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.GridPane;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.LossLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.io.ClassPathResource;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.Stage;

import org.nd4j.linalg.io.ClassPathResource;

/**
 * Test UI for MNIST classifier.
 * Run the MnistClassifier first to build the model.
 *
 * @author jesuino
 * @author fvaleri
 */
@SuppressWarnings("restriction")
public class MnistClassifierUI extends Application {

  private static final String basePath = System.getProperty("java.io.tmpdir") + "/mnist";
  private final int canvasWidth = 300;
  private final int canvasHeight = 300;
  private MultiLayerNetwork net; // trained modelprivate static DecimalFormat df2 = new DecimalFormat(".##");
  //private ComputationGraph graph;

  public MnistClassifierUI() throws Exception {

    String fullModel = new ClassPathResource("test_mnist_old.h5").getFile().getPath();
    net = KerasModelImport.importKerasSequentialModelAndWeights(fullModel,false);
  }

  public static void main(String[] args) throws Exception {
    launch();
  }

  @Override
  public void start(Stage stage) throws Exception {
    Canvas canvas = new Canvas(canvasWidth, canvasHeight);
    GraphicsContext ctx = canvas.getGraphicsContext2D();

    Button btback = new Button("back");
    HBox but = new HBox(btback);
    but.setAlignment(Pos.TOP_LEFT);

    ImageView imgView = new ImageView();
    imgView.setFitHeight(100);
    imgView.setFitWidth(100);
    ctx.setLineWidth(10);
    ctx.setLineCap(StrokeLineCap.SQUARE);
    Label lblResult = new Label();

    Label lbl[] = {new Label(), new Label(), new Label(), new Label(), new Label(), new Label(), new Label(), new Label(), new Label(), new Label()};
    GridPane preds = new GridPane();
    preds.setHgap(10);
    preds.setVgap(10);
    preds.setPadding(new Insets(10,10,10,10));
    preds.setAlignment(Pos.CENTER);

    Label num[] = {new Label(), new Label(), new Label(), new Label(), new Label(), new Label(), new Label(), new Label(), new Label(), new Label()};

    for(int i =0; i < 10; i++){
        preds.add(lbl[i],i,0);
        preds.add(num[i],i,1);
      }

    HBox hbBottom = new HBox(10, imgView, lblResult);
    hbBottom.setAlignment(Pos.CENTER);
    VBox root = new VBox(20,but, canvas, hbBottom, preds);
    root.setAlignment(Pos.CENTER);

    Scene scene = new Scene(root, 600, 600);
    stage.setScene(scene);
    stage.setTitle("Draw a digit and hit enter (right-click to clear)");
    stage.setResizable(false);
    stage.show();

    btback.setOnMouseClicked(e -> {
        System.out.println("go back son");
    });

    canvas.setOnMousePressed(e -> {
      ctx.setStroke(Color.WHITE);
      ctx.beginPath();
      ctx.moveTo(e.getX(), e.getY());
      ctx.stroke();
    });
    canvas.setOnMouseDragged(e -> {
      ctx.setStroke(Color.WHITE);
      ctx.lineTo(e.getX(), e.getY());
      ctx.stroke();
    });
    canvas.setOnMouseClicked(e -> {
      if (e.getButton() == MouseButton.SECONDARY) {
        clear(ctx);
      }
    });
    canvas.setOnKeyReleased(e -> {
      if (e.getCode() == KeyCode.ENTER) {
        BufferedImage scaledImg = getScaledImage(canvas);
        imgView.setImage(SwingFXUtils.toFXImage(scaledImg, null));
        try {
          predictImage(scaledImg, lblResult, lbl, num);
        } catch (Exception e1) {
          e1.printStackTrace();
        }
      }
    });
    clear(ctx);
    canvas.requestFocus();
  }

  private void clear(GraphicsContext ctx) {
    ctx.setFill(Color.BLACK);
    ctx.fillRect(0, 0, 300, 300);
  }

  private BufferedImage getScaledImage(Canvas canvas) {
    WritableImage writableImage = new WritableImage(canvasWidth, canvasHeight);
    canvas.snapshot(null, writableImage);
    Image tmp = SwingFXUtils.fromFXImage(writableImage, null).getScaledInstance(28, 28, Image.SCALE_SMOOTH);
    BufferedImage scaledImg = new BufferedImage(28, 28, BufferedImage.TYPE_BYTE_GRAY);
    Graphics graphics = scaledImg.getGraphics();
    graphics.drawImage(tmp, 0, 0, null);
    graphics.dispose();
    return scaledImg;
  }

  private void predictImage(BufferedImage img, Label lbl, Label[] preds, Label[] nums) throws IOException {
    NativeImageLoader loader = new NativeImageLoader(28, 28, 1, true);
    INDArray image = loader.asMatrix(img);
    ImagePreProcessingScaler scaler = new ImagePreProcessingScaler(0, 1);
    scaler.transform(image);
    INDArray output = net.output(image);
    //double x = output.getDouble(1)*100;
    lbl.setText("Prediction: " + net.output(image).argMax());

    for (int i = 0; i <10; i++){
        Double liklihood = output.getDouble(i)*100;
        liklihood = (double)Math.round(liklihood*100)/100;
        String likli = Double.toString(liklihood);
        preds[i].setText(likli);

        nums[i].setText(Integer.toString(i));

    }
  }

}
