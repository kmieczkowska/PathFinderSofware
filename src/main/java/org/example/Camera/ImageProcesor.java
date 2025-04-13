package org.example.Camera;

import org.example.RobotController.IRobotController;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import java.util.ArrayList;
import java.util.List;

/**
 * Przetwarzanie obrazu kamery
 */
public class ImageProcesor {

    private IRobotController robotController;
    private boolean isRunning = true;

    private int treshold = 50;
    public void setTreshold(int treshold) {this.treshold = treshold;}

    public void setStart(){
        isRunning = true;
    }
    public void setStop(){
        isRunning = false;
    }

    public ImageProcesor(IRobotController _robotController) {
        robotController = _robotController;
    }

    public Mat strategy3(Mat frame) {

        int width = frame.width();
        int height = frame.height();

        Mat grayFrame = new Mat();
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);

        // Binaryzacja obrazu: szukamy czarnych obiektów (jasne tło -> ciemne obiekty)
        Mat binaryFrame = new Mat();
        Imgproc.threshold(grayFrame, binaryFrame, treshold, 255, Imgproc.THRESH_BINARY_INV);

        // Znajdowanie konturów czarnych obiektów
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(binaryFrame, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        return frame;
    }

    /**
     * TO DO - it's not working
     * podziału na 4 części
     * porównywanie ilości pikseli po wybranych stronach
     * Ustawianie mocy motorów na podstawie części A, B, C lub D
     *
     * PROBLEM:
     */
    public Mat strategy2(Mat frame) {

        // Dzielenie obrazu na 4 części
        int width = frame.width();
        int height = frame.height();

        // Linie podziału
        int midX = width / 2;
        int midY = height / 2;

//        System.out.println("midX: " + midX); //320
//        System.out.println("midY: " + midY); //240

        //region Konwersja obrazu na skalę szarości i binaryzacja

        Mat grayFrame = new Mat();
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);

        Mat binaryFrame = new Mat();
        Imgproc.threshold(grayFrame, binaryFrame, treshold, 255, Imgproc.THRESH_BINARY_INV); // prog

        // Konwersja binaryzowanego obrazu z powrotem na format BGR, aby móc na nim rysować linie i etykiety
        Mat outputFrame = new Mat();
        Imgproc.cvtColor(binaryFrame, outputFrame, Imgproc.COLOR_GRAY2BGR);
        //endregion

        //region Obliczenie liczby czarnych pikseli dla każdej ćwiartki
        Mat sectionA = binaryFrame.submat(0, midY, 0, midX); // Górna lewa
        Mat sectionB = binaryFrame.submat(0, midY, midX, width); // Górna prawa
        Mat sectionC = binaryFrame.submat(midY, height, 0, midX); // Dolna lewa
        Mat sectionD = binaryFrame.submat(midY, height, midX, width); // Dolna prawa

        int blackPixelsA = Core.countNonZero(sectionA);
        int blackPixelsB = Core.countNonZero(sectionB);
        int blackPixelsC = Core.countNonZero(sectionC);
        int blackPixelsD = Core.countNonZero(sectionD);

//        System.out.println("Czarna liczba pikseli w A: " + blackPixelsA);
//        System.out.println("Czarna liczba pikseli w B: " + blackPixelsB);
//        System.out.println("Czarna liczba pikseli w C: " + blackPixelsC);
//        System.out.println("Czarna liczba pikseli w D: " + blackPixelsD);

        // Porównanie liczby czarnych pikseli
        int leftBlackPixels = blackPixelsA + blackPixelsC;
        int rightBlackPixels = blackPixelsB + blackPixelsD;

//        System.out.println("Czarna liczba pikseli po lewej stronie (A + C): " + leftBlackPixels);
//        System.out.println("Czarna liczba pikseli po prawej stronie (B + D): " + rightBlackPixels);

        //endregion

        //region Skrety

        String comparisonResult;


        //region TODO moveForward()
        //endregion

        //punkt srodkowy szuka czarnych pikseli
        //jesli prawo to B czy D, na tej podstawie ustala sile silnikow (skret)
        // Porównanie: Jeśli po lewej stronie (A + C) więcej czarnych pikseli

        // Lewa strona (A + C)
        if (leftBlackPixels > rightBlackPixels) {
            comparisonResult = "A+C lewo";  // Więcej czarnych pikseli po lewej stronie
            if (blackPixelsA > blackPixelsC) {
                comparisonResult = "A";
                robotController.setMovmentSpeed(40, 50);
                robotController.delay(20);
            } else if (blackPixelsC > blackPixelsA) {
                robotController.setMovmentSpeed(30, 70);
            }
        }
        // Prawa strona (B + D)
        else if (rightBlackPixels > leftBlackPixels) {
            comparisonResult = "B+D prawo";  // Więcej czarnych pikseli po prawej stronie
            robotController.turnRight();
            if (blackPixelsB > blackPixelsD) {
                comparisonResult = "B";
//                robotController.turnRight();
                robotController.setMovmentSpeed(90, 0);
            } else if (blackPixelsD > blackPixelsB) {
                comparisonResult = "D";
//                robotController.turnRight();
                robotController.setMovmentSpeed(0, -90);
            }
        } else {
            comparisonResult = "same black pixels value";
            robotController.moveForward(); // maja tyle samo
        }

        //endregion

        //region Rysowanie linii i etykiet

        Imgproc.line(outputFrame, new Point(midX, 0), new Point(midX, height), new Scalar(0, 0, 255), 2); // Pionowa linia
        Imgproc.line(outputFrame, new Point(0, midY), new Point(width, midY), new Scalar(0, 0, 255), 2); // Pozioma linia

        Imgproc.putText(outputFrame, "A", new Point(midX / 2, midY / 2), Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 0, 255), 2);
        Imgproc.putText(outputFrame, "B", new Point(midX + midX / 2, midY / 2), Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 0, 255), 2);
        Imgproc.putText(outputFrame, "C", new Point(midX / 2, midY + midY / 2), Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 0, 255), 2);
        Imgproc.putText(outputFrame, "D", new Point(midX + midX / 2, midY + midY / 2), Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 0, 255), 2);

        Imgproc.putText(outputFrame, comparisonResult, new Point(10, 30), Imgproc.FONT_HERSHEY_SIMPLEX, 0.8, new Scalar(0, 255, 0), 2);

        //endregion

        return outputFrame;
    }

    /**
     * IT WORKS
     * Przetwarzanie obrazu kamery używając:
     * podziału na 2 części
     * porównywanie ilości pikseli po wybranych stronach
     */
    public Mat strategy1(Mat frame){

        String comparisonResult; // Comparison which part has more pixels

        // Dividing the image into 2 parts (left | right)
        int width = frame.width();
        int midX = width / 2;

        int height = frame.height();
        int midY = height / 2;


        // Image conversion to grayscale and binarization
        Mat grayFrame = new Mat();
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY); // GrayScale

        Mat binaryFrame = new Mat();
        Imgproc.threshold(grayFrame, binaryFrame, treshold, 255, Imgproc.THRESH_BINARY_INV); // Setting the threshold

        // Conversion a binarized image back to RGB format - we will be able to draw lines and labels on a video
        Mat outputFrame = new Mat();
        Imgproc.cvtColor(binaryFrame, outputFrame, Imgproc.COLOR_GRAY2BGR);

        // Settings parts
        Mat sectionA = binaryFrame.submat(0, height, 0, midX); // left part of a video
        Mat sectionB = binaryFrame.submat(0, height, midX, width); // right part of a video

        // Counting pixels on each part
        int blackPixelsA = Core.countNonZero(sectionA);
        int blackPixelsB = Core.countNonZero(sectionB);

        if (blackPixelsA > blackPixelsB) {
            robotController.rightWheelForward();
//            robotController.setMovmentSpeed();
            comparisonResult = "Left";
        }
        else if (blackPixelsB > blackPixelsA) {
            robotController.leftWheelForward();
//            robotController.setMovmentSpeed();
            comparisonResult = "Right";
        }
        else{
            comparisonResult = "Forward";
            robotController.moveForward();
        }

        Imgproc.line(outputFrame, new Point(midX, 0), new Point(midX, height), new Scalar(0, 0, 255), 2); // Red vertical line
        Imgproc.putText(outputFrame, "A", new Point(midX / 2, midY), Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 0, 255), 2);
        Imgproc.putText(outputFrame, "B", new Point(midX + midX / 2, midY), Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 0, 255), 2);
        Imgproc.putText(outputFrame, comparisonResult, new Point(10, 30), Imgproc.FONT_HERSHEY_SIMPLEX, 0.8, new Scalar(0, 255, 0), 2);

        return outputFrame;
    }

    /**
     * TO DO - it's not working
     * Przetwarzanie obrazu kamery - wersja 1
     * Wyszukiwanie bialych i czarnych elementów na kamerze
     * Zaznaczanie elementów na kamerze
     * Na podstawie położenia zaznaczonych elementów na osi X określanie, w którym kierunku ma jechać robot
     *
     * PROBLEM: zbyt duża reakcja na światło i niedoskonałości
     */
    public Mat initialStrategy0(Mat frame){

        Mat grayFrame = new Mat();
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);

        // Binaryzacja obrazu: szukamy czarnych obiektów (jasne tło -> ciemne obiekty)
        Mat binaryFrame = new Mat();
        Imgproc.threshold(grayFrame, binaryFrame, treshold, 255, Imgproc.THRESH_BINARY_INV);

        // Znajdowanie konturów czarnych obiektów
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(binaryFrame, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Iteracja przez znalezione kontury
        for (int i = 0; i < contours.size(); i++) {
            Rect boundingBox = Imgproc.boundingRect(contours.get(i));
            if (boundingBox.width > 10 && boundingBox.height > 10) {
                Imgproc.rectangle(frame, boundingBox, new Scalar(0, 255, 0), 2);

                //System.out.println("X: "+boundingBox.x + "\n");

                if(boundingBox.x >= 15 && boundingBox.x <= 600) robotController.moveForward();
                else if (boundingBox.x < 15) if(isRunning) robotController.turnLeft();
                else if(boundingBox.x > 600) if(isRunning) robotController.turnRight();
            }
        }
        return frame;
    }
}
