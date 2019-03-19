package org.deeplearning4j.examples.convolution.mnist;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.io.ClassPathResource;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class HotdogNotHotdog {
    private static MultiLayerNetwork net2;

    public static void main(String[] args) throws Exception {
        String fullModel = new ClassPathResource("model.h5").getFile().getPath();
        net2 = KerasModelImport.importKerasSequentialModelAndWeights(fullModel, false);
        NativeImageLoader loader = new NativeImageLoader(128, 128, 3, true);
        INDArray feed_image = loader.asMatrix(new org.datavec.api.util.ClassPathResource("cat_test.jpeg").getFile());
        ImagePreProcessingScaler scaler = new ImagePreProcessingScaler(0, 1);
        scaler.transform(feed_image);
        INDArray output = net2.output(feed_image);
        System.out.println(output);
    }
}
