package org.example.Camera;

import org.example.RobotController.IRobotController;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Przetwarzanie obrazu kamerki
 */
public class ImageProcesor {

    private IRobotController robotController;

    public ImageProcesor(IRobotController _robotController) {
        robotController = _robotController;
    }

    /**
     * Dzieli ekran kamery na cztery równe części i oznacza je jako A, B, C, D.
     *
     * @param frame Obraz z kamerki.
     * @return Obraz z naniesionym podziałem i etykietami.
     */
    public Mat divideAndLabelScreen(Mat frame) {

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
        Imgproc.threshold(grayFrame, binaryFrame, 50, 255, Imgproc.THRESH_BINARY_INV); // prog

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

        //region Ruch do przodu / zjechanie ze sciezki
        int SPixel = (int) binaryFrame.get(midY, midX)[0];
        if (SPixel == 255) {
            robotController.setMovmentSpeed(50, 50);
        } else {

            int closestX = -1;
            int closestY = -1;
            double minDistance = Double.MAX_VALUE;

            // Przeszukiwanie obrazu w celu znalezienia najbliższego białego piksela
            for (int y = 0; y < binaryFrame.rows(); y++) {
                for (int x = 0; x < binaryFrame.cols(); x++) {
                    if ((int) binaryFrame.get(y, x)[0] == 255) { // Jeśli piksel jest biały
                        // Oblicz odległość euklidesową od centralnego punktu (midX, midY)
                        double distance = Math.sqrt(Math.pow(midX - x, 2) + Math.pow(midY - y, 2));
                        if (distance < minDistance) {
                            minDistance = distance;
                            closestX = x;
                            closestY = y;
                        }
                    }
                }

            }
            if (closestX != -1 && closestY != -1) {
                System.out.println("Najbliższy biały piksel znaleziony na pozycji: (" + closestX + ", " + closestY + ").");
            }

            // Określenie kierunku na podstawie sekcji, w której znajduje się najbliższy biały piksel
            if (closestX < midX && closestY < midY) { // Sekcja A
                robotController.setMovmentSpeed(-30, 30); // Skręt w lewo
            } else if (closestX >= midX && closestY < midY) { // Sekcja B
                robotController.setMovmentSpeed(30, -30); // Skręt w prawo
            } else if (closestX < midX && closestY >= midY) { // Sekcja C
                robotController.setMovmentSpeed(-50, 50); // Szybszy skręt w lewo
            } else if (closestX >= midX && closestY >= midY) { // Sekcja D
                robotController.setMovmentSpeed(50, -50); // Szybszy skręt w prawo
            }

        }

            //endregion
        String comparisonResult;

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
                    robotController.setMovmentSpeed(50, 40);
                } else if (blackPixelsD > blackPixelsB) {
                    comparisonResult = "D";
//                robotController.turnRight();
                    robotController.setMovmentSpeed(70, 30);
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

        public Mat proses (Mat frame){

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
                Rect boundingBox = Imgproc.boundingRect(contours.get(i));
                if (boundingBox.width > 10 && boundingBox.height > 10) {
                    Imgproc.rectangle(frame, boundingBox, new Scalar(0, 255, 0), 2);

                    //System.out.println("X: "+boundingBox.x + "\n");

                    if (boundingBox.x >= 15 && boundingBox.x <= 600) robotController.moveForward();
                    else if (boundingBox.x < 15) robotController.turnLeft();
                    else if (boundingBox.x > 600) robotController.turnRight();
                }
            }
            return frame;
        }
    }