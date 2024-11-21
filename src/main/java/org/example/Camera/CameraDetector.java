package org.example.Camera;


import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.opencv_java;
import org.example.RobotController.RobotController;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.util.ArrayList;
import java.util.List;

public class CameraDetector {

    public static RobotController robotController;

    public CameraDetector(RobotController _robotController) {
        robotController = _robotController;
    }

    public static void main() {
        // Załaduj bibliotekę OpenCV
        Loader.load(opencv_java.class);


        // Inicjalizuj kamerę
        VideoCapture camera = new VideoCapture(0); // 0 oznacza domyślną kamerę
        if (!camera.isOpened()) {
            System.out.println("Nie udało się otworzyć kamerki");
            return;
        }

        Mat frame = new Mat();          // Oryginalna klatka z kamery

        while (true) {
            if (camera.read(frame)) {
                // Wypisanie rozdzielczości ekranu
//                int frameWidth = frame.width();
//                int frameHeight = frame.height();
//                System.out.println("Rozdzielczość ekranu: " + frameWidth + "x" + frameHeight);

                // Przekształcenie obrazu na skalę szarości
                Mat grayFrame = new Mat();
                Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);

                // Binaryzacja obrazu: szukamy czarnych obiektów (jasne tło -> ciemne obiekty)
                Mat binaryFrame = new Mat();
                Imgproc.threshold(grayFrame, binaryFrame, 50, 255, Imgproc.THRESH_BINARY_INV);

                // Znajdowanie konturów czarnych obiektów
                List<MatOfPoint> contours = new ArrayList<>();
                Mat hierarchy = new Mat();
                Imgproc.findContours(binaryFrame, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

                // Iteracja przez znalezione kontury
                for (int i = 0; i < contours.size(); i++) {
                    // Obliczanie prostokątów otaczających kontury
                    Rect boundingBox = Imgproc.boundingRect(contours.get(i));

                    // drawing rectangles
                    if (boundingBox.width > 10 && boundingBox.height > 10) {
                        Imgproc.rectangle(frame, boundingBox, new Scalar(0, 255, 0), 2);

                        System.out.println("X: " + boundingBox.x + "\n");
                        if(boundingBox.x >= 250 && boundingBox.x <= 430){
//                            System.out.println("1");
                            robotController.sendCommand("1");
                        }
                        else if(boundingBox.x < 249) {
//                            System.out.println("3");
                            robotController.sendCommand("3");
                        }
                        else if(boundingBox.x > 430)
                        {
//                            System.out.println("4");
                            robotController.sendCommand("4");
                        }
                    }
                }

                // Wyświetlanie widoku kamerki
                //HighGui.imshow("Oryginalny obraz z zaznaczeniem", frame);

//                // Wyjście po naciśnięciu klawisza 'q'
//                if (HighGui.waitKey(30) == 'q') {
//                    break;
//                }
            } else {
                System.out.println("Nie udało się odczytać klatki z kamerki");
                break;
            }
        }

        // Zwolnienie zasobów
        camera.release();
        HighGui.destroyAllWindows();
    }

}
