package sample;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.awt.Color;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;

public class Main extends Application{
    @Override
    public void start(Stage primaryStage) throws IOException {
        File file = new File("C://Users/spenc/Documents/Y2S2/SystemsDev/colours/src/green.png");
        BufferedImage image = ImageIO.read(file);

        ArrayList<Integer> coloursString = new ArrayList<>(); // colour type
        ArrayList<Integer> coloursCount = new ArrayList<>(); // # of occorrences

        int width = image.getWidth();
        int height = image.getHeight();
        int clr;
        int red;
        int green;
        int blue;
        int counter = -1;
        int colourString;

        for (int x = 1; x < width; x++) {
            for (int y = 1; y < height; y++) {
                clr = image.getRGB(x, y);
                red = (clr >> 16) & 0xff;
                green = (clr >> 8) & 0xff;
                blue = clr & 0xff;

                colourString = (1000000000 + red * 1000000 + green * 1000 + blue);

                if (counter == -1) {
                    coloursString.add(colourString);
                    coloursCount.add(1);
                    counter = 0;
                } else {
                    counter = 0;
                    for (int i = 0; i < coloursCount.size(); i++) {
                        if (coloursString.get(i) == colourString) {
                            coloursCount.set(i, coloursCount.get(i) + 1);
                            counter += 1;
                        }
                    }
                    if (counter == 0) {// no occorrances
                        coloursString.add(colourString);
                        coloursCount.add(1);
                    }
                }
            }
        }

        int sqrSize = 0;
        counter = 0;

        while (sqrSize == 0) {
            counter += 1;
            if ((coloursCount.size() > ((counter - 1) * (counter - 1))) && (coloursCount.size() <= (counter * counter))) {
                sqrSize = counter;
            }
        }

        BufferedImage newImage = new BufferedImage(sqrSize, sqrSize, BufferedImage.TYPE_INT_ARGB); // output

        counter = 0;
        int myRed;
        int myGreen;
        int myBlue;
        int myCol;

        for (int x = 0; x < sqrSize; x++) {
            for (int y = 0; y < sqrSize; y++) { /// Check if counter iterates correctly
                if (counter < coloursCount.size()) {
                    myCol = coloursString.get(counter) - 1000000000;
                    myBlue = myCol % 1000;// at index 7, 8, 9
                    myCol = (myCol - myBlue) / 1000;
                    myGreen = myCol % 1000;// at index 4, 5, 6
                    myCol = (myCol - myGreen) / 1000;
                    myRed = myCol % 1000;// at index 1, 2, 3
                    Color myColour = new Color(myRed, myGreen, myBlue);
                    newImage.setRGB(x, y, myColour.getRGB());
                    counter += 1;
                }
                else {
                    Color myColour = new Color(255, 255, 255);
                    newImage.setRGB(x, y, myColour.getRGB());
                }
            }
        }

        File outputfile = new File("C://Users/spenc/Documents/Y2S2/SystemsDev/colours/src/palette.png");
        ImageIO.write(newImage, "png", outputfile);

        display(primaryStage);
    }

    public void display(Stage primaryStage) throws IOException {
        Pane pane = new Pane();

        Image original = new Image("green.png");
        ImageView view = new ImageView();
        view.setImage(original);
        view.setY(50);
        view.setX(50);

        Image paletteize = new Image("palette.png");
        ImageView viewPalette = new ImageView();
        viewPalette.setImage(paletteize);
        viewPalette.setY(100);
        viewPalette.setX(400);

        pane.getChildren().add(view);
        pane.getChildren().add(viewPalette);

        Scene scene = new Scene(pane, 600, 300);
        primaryStage.setTitle("Paletteize");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}