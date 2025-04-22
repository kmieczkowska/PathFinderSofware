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

    private String lastDirection = "";  // last direction (left, right, forward)
    private boolean lineLost = false;   // wether the line was lost

    // Tolerancja - liczba klatek, przez które robot będzie czekał bez wykrywania linii
    private int lostLineFrames = 0;
    private int lostLineThreshold = 10;  // steable - SPRAWDZIC JAKI PROG BEDZIE OK


    public Mat strategy6(Mat frame) {
        int width = frame.width();
        int height = frame.height();

        Mat grayFrame = new Mat();
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);

        Mat binaryFrame = new Mat();
        Imgproc.threshold(grayFrame, binaryFrame, 100, 255, Imgproc.THRESH_BINARY_INV);

        // Wiersz analizowany (np. ¾ wysokości obrazu)
        int rowToScan = (int)(height * 0.75);
        Mat row = binaryFrame.row(rowToScan);

        // Szukanie pierwszego czarnego piksela (lewa krawędź linii)
        int linePositionX = -1;
        for (int x = 0; x < width; x++) {
            double[] pixel = row.get(0, x);
            if (pixel != null && pixel.length > 0 && pixel[0] > 0) {
                linePositionX = x;
                break;
            }
        }

        // Konwersja z powrotem do koloru aby narysować wynik
        Mat outputFrame = new Mat();
        Imgproc.cvtColor(binaryFrame, outputFrame, Imgproc.COLOR_GRAY2BGR);

        // Środek obrazu
        int centerX = width / 2;
        String direction;

        if (linePositionX != -1) {
            // Zaznacz wykryty punkt
            Imgproc.circle(outputFrame, new Point(linePositionX, rowToScan), 5, new Scalar(0, 255, 0), -1);

            // Prosta logika sterowania
            int tolerance = 20; // margines tolerancji

            // Zgubienie linii: Jeżeli robot nie widzi linii przez określoną liczbę klatek
            lostLineFrames = 0;  // Resetujemy liczbę klatek bez linii, bo linia została wykryta
            lineLost = false;    // Linia nie została zgubiona

            if (linePositionX < centerX - tolerance) {
                direction = "Turn Left";
                lastDirection = "left";  // Zapamiętanie kierunku
                robotController.leftWheelForward();
            } else if (linePositionX > centerX + tolerance) {
                direction = "Turn Right";
                lastDirection = "right";  // Zapamiętanie kierunku
                robotController.rightWheelForward();
            } else {
                direction = "Forward";
                lastDirection = "forward";  // Zapamiętanie kierunku
                robotController.moveForward();
            }
        } else {
            // Jeśli nie wykryto konturów, linia została zgubiona
            lostLineFrames++;

            if (lostLineFrames > lostLineThreshold) {
                lineLost = true;  // Linia została zgubiona
                lostLineFrames = 0;  // Resetujemy licznik

                // Reakcja na zgubienie linii: obrócenie w przeciwnym kierunku
                if ("left".equals(lastDirection)) {
                    direction = "Line Lost - Turn Right";  // Obracamy w prawo, jeśli ostatnio skręcał w lewo
                    robotController.rightWheelForward();  // Obróć w prawo
                } else if ("right".equals(lastDirection)) {
                    direction = "Line Lost - Turn Left";  // Obracamy w lewo, jeśli ostatnio skręcał w prawo
                    robotController.leftWheelForward();  // Obróć w lewo
                } else {
                    // Jeśli robot jechał do przodu, wybieramy obrót w prawo lub lewo
                    direction = "Line Lost - Turn Right";  // Możemy wybrać dowolnie
                    robotController.rightWheelForward();  // Obróć w prawo
                }
            } else {
                direction = "Line Lost - Searching...";
                robotController.setMovmentSpeed(0, 0);  // Zatrzymaj robota, gdy nie wykrywa linii
            }
        }

        // Dodanie informacji na ekranie
        Imgproc.putText(outputFrame, direction, new Point(10, 30), Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(255, 0, 0), 2);
        Imgproc.line(outputFrame, new Point(centerX, 0), new Point(centerX, height), new Scalar(0, 0, 255), 1); // pionowy środek

        return outputFrame;
    }

    /**
     * TO DO - READY TO TEST
     * Robot wykrywa prostokąty - czarną linię
     * podąża za prostokątem
     * PROBLEM: światło
     */
    public Mat strategy5(Mat frame) {
        int width = frame.width();
        int height = frame.height();

        Mat grayFrame = new Mat();
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);

        Mat binaryFrame = new Mat();
        Imgproc.threshold(grayFrame, binaryFrame, treshold, 255, Imgproc.THRESH_BINARY_INV);  // black line, white bckgr

        // Looking for contours on binary frame
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(binaryFrame, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        Mat outputFrame = new Mat();
        Imgproc.cvtColor(binaryFrame, outputFrame, Imgproc.COLOR_GRAY2BGR);

        // If contours detected
        if (contours.size() > 0) {
            // Find the biggest one, which can be the line
            double maxArea = 0;
            MatOfPoint maxContour = null;

            for (MatOfPoint contour : contours) {
                double area = Imgproc.contourArea(contour);
                if (area > maxArea) {
                    maxArea = area;
                    maxContour = contour;
                }
            }

            if (maxContour != null) {
                // Calculate rectangle surrounding the contour
                Rect boundingBox = Imgproc.boundingRect(maxContour);
                int centerX = boundingBox.x + boundingBox.width / 2;  // middle point

                // Drawing rectangle
                Imgproc.rectangle(outputFrame, boundingBox.tl(), boundingBox.br(), new Scalar(0, 255, 0), 2);

                // Determining the direction depending on the location of the line
                int frameCenterX = width / 2;
                int tolerance = 50;

                if (centerX < frameCenterX - tolerance) {
                    lastDirection = "right";  // remembering the direction
//                    robotController.setMovmentSpeed(60, 30);
                    robotController.rightWheelForward();
                } else if (centerX > frameCenterX + tolerance) {

                    lastDirection = "left";
//                    robotController.setMovmentSpeed(30, 60);
                    robotController.leftWheelForward();
                } else {
                    lastDirection = "forward";
                    robotController.moveForward();
                }

                lostLineFrames = 0;  // Resetting the frame count without lines
                lineLost = false;    // The line was not lost
            }
        } else {
            // If no contours are detected, we assume the line is lost
            lostLineFrames++;

            if (lostLineFrames > lostLineThreshold) {
                lineLost = true;  // The line was lost
                lostLineFrames = 0;  // We reset the counter

                // Rotate in the opposite direction to the last one
                if ("left".equals(lastDirection)) {
                    robotController.turnRight();
                } else if ("right".equals(lastDirection)) {
                    robotController.turnLeft();
                } else {
                    // If it was moving forward, start turning right or left (any)
                    robotController.turnRight();
                }
            } else {
//                robotController.setMovmentSpeed(0, 0);  // Stop ODKOMENTOWAC I SPRAWDZIC JAK DZIALA
            }
        }

        String directionInfo = lineLost ? "Line Lost" : "Following Line";
        Imgproc.putText(outputFrame, directionInfo, new Point(10, 30), Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(255, 0, 0), 2);

        return outputFrame;
    }

    /**
     * TO DO - READY TO TEST
     * Robot wykrywa krawdz czarnej linii na bialym tle
     * trzyma sie tej krawedzi - zaznacza punkt na kamerze za ktorym podaza
     * PROBLEM:
     */
    public Mat strategy4(Mat frame) {

        int width = frame.width();
        int height = frame.height();

        Mat grayFrame = new Mat();
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);

        Mat binaryFrame = new Mat();
        Imgproc.threshold(grayFrame, binaryFrame, treshold, 255, Imgproc.THRESH_BINARY_INV);

        // Wiersz analizowany (np. ¾ wysokości obrazu)
        int rowToScan = (int)(height * 0.75);
        Mat row = binaryFrame.row(rowToScan);

        // Szukanie pierwszego czarnego piksela (lewa krawędź linii)
        int linePositionX = -1;
        for (int x = 0; x < width; x++) {
            double[] pixel = row.get(0, x);
            if (pixel != null && pixel.length > 0 && pixel[0] > 0) {
                linePositionX = x;
                break;
            }
        }

        // Szukanie pierwszego czarnego piksela (prawa krawędź linii)
//        int linePositionX = -1;
//        for (int x = width - 1; x >= 0; x--) { // Przechodzimy od prawej do lewej
//            double[] pixel = row.get(0, x);
//            if (pixel != null && pixel.length > 0 && pixel[0] > 0) {
//                linePositionX = x;
//                break;
//            }
//        }

        // Konwersja z powrotem do koloru aby narysować wynik
        Mat outputFrame = new Mat();
        Imgproc.cvtColor(binaryFrame, outputFrame, Imgproc.COLOR_GRAY2BGR);

        // Środek obrazu
        int centerX = width / 2;

        String direction;

        if (linePositionX != -1) {
            // Zaznacz wykryty punkt
            Imgproc.circle(outputFrame, new Point(linePositionX, rowToScan), 5, new Scalar(0, 255, 0), -1);

            // Prosta logika sterowania
            int tolerance = 20; // margines tolerancji

            if (linePositionX < centerX - tolerance) {
                direction = "Turn Left";
//                robotController.setMovmentSpeed(30, 60);
                robotController.leftWheelForward();
            } else if (linePositionX > centerX + tolerance) {
                direction = "Turn Right";
//                robotController.setMovmentSpeed(60, 30);
                robotController.rightWheelForward();
            } else {
                direction = "Forward";
                robotController.moveForward();
            }
        } else {
            direction = "Line lost - Stop";
            robotController.setMovmentSpeed(0, 0); // lub szukaj linii
        }

        // Dodanie informacji na ekranie
        Imgproc.putText(outputFrame, direction, new Point(10, 30), Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(255, 0, 0), 2);
        Imgproc.line(outputFrame, new Point(centerX, 0), new Point(centerX, height), new Scalar(0, 0, 255), 1); // pionowy środek

        return outputFrame;
    }

    /**
     * TO DO - it's not working
     * podziału na 4 części
     * porównywanie ilości pikseli po wybranych stronach
     * Ustawianie mocy motorów na podstawie części A, B, C lub D
     *
     * PROBLEM:
     */

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

    Mat frame = new Mat();

    String comparisonResult;
    int width = frame.width();
    int midX = width / 2;

    int height = frame.height();
    int midY = height / 2;

    Mat grayFrame = new Mat();

    Mat binaryFrame = new Mat();
    Mat outputFrame = new Mat();

    Mat sectionA;
    Mat sectionB;
    int blackPixelsA = 0;
    int blackPixelsB = 0;

    /**
     * IT WORKS
     * Przetwarzanie obrazu kamery używając:
     * podziału na 2 części
     * porównywanie ilości pikseli po wybranych stronach
     */
    public Mat strategy1(Mat frame){

//        String comparisonResult; // Comparison which part has more pixels
//
//        // Dividing the image into 2 parts (left | right)
//        int width = frame.width();
//        int midX = width / 2;
//
//        int height = frame.height();
//        int midY = height / 2;


        // Image conversion to grayscale and binarization
//        Mat grayFrame = new Mat();
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY); // GrayScale

//        Mat binaryFrame = new Mat();
        Imgproc.threshold(grayFrame, binaryFrame, treshold, 255, Imgproc.THRESH_BINARY_INV); // Setting the threshold

        // Conversion a binarized image back to RGB format - we will be able to draw lines and labels on a video
//        Mat outputFrame = new Mat();
        Imgproc.cvtColor(binaryFrame, outputFrame, Imgproc.COLOR_GRAY2BGR);

        // Settings parts
//        Mat sectionA = binaryFrame.submat(0, height, 0, midX); // left part of a video
//        Mat sectionB = binaryFrame.submat(0, height, midX, width); // right part of a video

         sectionA = binaryFrame.submat(0, height, 0, midX); // left part of a video
         sectionB = binaryFrame.submat(0, height, midX, width); // right part of a video

        // Counting pixels on each part
//        int blackPixelsA = Core.countNonZero(sectionA);
//        int blackPixelsB = Core.countNonZero(sectionB);

        blackPixelsA = Core.countNonZero(sectionA);
        blackPixelsB = Core.countNonZero(sectionB);

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
