README

##CSCI 2020u Final Project - Computer Vision Deep Learning Demo
by Tamilselvan Balasuntharam, Harrish Thasarathan, Tommy Turcotte and Spencer Denford

### Introduction:
Here we present our final project for csci2020u, a computer vision and deep learning application with 3 interactive demonstrations. These include a hand written (single) digit recognizer, where the user can draw a number and our trained neural network will recognize it. A binary cat vs dog classifier, where the user can feed an image of a dog or cat and have a neural network determine which animal it is. As well as a Style Transfer demonstration where the user can take a webcam picture of themselves and have the style of the photo changed (similar to instagram filters).

## Prerequisites
- Java 1.8 (JDK)
- DeepLearning4j Library
- Maven

## Installation
- Clone this repo (allows use of DL4j libraries):
```bash
$ git clone https://github.com/deeplearning4j/dl4j-examples.git
$ cd dl4j-examples/
$ mvn clean install
```
- mvn clean install can take some time 
- Note that the library is large, delete it from your system when finished if you prefer
- Clone our repo
```bash
$ git clone https://github.com/Harry-Thasarathan/Java-Project.git
$ cd Java-Project
$ mvn clean install
```
- Open the project in IntelliJ and run the server and then the client files from IntelliJ, this will show the main menu from there you can view the rest of the demos

## Hand Written Digit Recognizer:
![Image description](https://github.com/Harry-Thasarathan/Java-Project/blob/master/resources/digits.png)
Left click and draw a number (0 to 9), then press enter. Our code will print the networks prediction as to the number classification. Below that is a more detailed label

## Cat or Dog: 
![Image description](https://github.com/Harry-Thasarathan/Java-Project/blob/master/resources/catndogscaled.png)

Upload a photo of a dog or a cat using our menu system. Press button to view the prediction on the left. Our code will distinguish weather it is a dog or a cat.

## Style Transfer:
![Image description](https://github.com/Harry-Thasarathan/Java-Project/blob/master/resources/StyleTransfer.png) 

Pressing the Capture! button will capture a webcam image. Pressing Stylize! will then run the style transfer algorithm on the webcam image. Since the style transfer image is computationally expensive and doesnt work well in the javafx thread (even as a seperate task), use our menu systems open command to open you file system and open the produced images to the screen. They are saved under resources. 


## Server

## Client



