package org.example.Camera;


import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.opencv_java;
import org.example.RobotController.RobotController;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.io.OutputStream;
import java.net.Socket;
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
        VideoCapture camera = new VideoCapture(0); // 1 oznacza kamerę zewnętrzną
        if (!camera.isOpened()) {
            System.out.println("Nie udało się otworzyć kamerki");
            return;
        }

        Mat frame = new Mat();          // Oryginalna klatka z kamery
        String serverHost = "192.168.1.2"; // Adres serwera
        int serverPort = 12345;         // Port serwera

        try (Socket socket = new Socket(serverHost, serverPort);
             OutputStream outputStream = socket.getOutputStream()) {

            System.out.println("Połączono z serwerem: " + serverHost);

            while (true) {
                if (camera.read(frame)) {
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
                        Rect boundingBox = Imgproc.boundingRect(contours.get(i));
                        if (boundingBox.width > 10 && boundingBox.height > 10) {
                            Imgproc.rectangle(frame, boundingBox, new Scalar(0, 255, 0), 2);

                            System.out.println("X: "+boundingBox.x + "\n");

                            if(boundingBox.x >= 15 && boundingBox.x <= 600){
                                System.out.println("1");
                                robotController.sendCommand("1");
                            }
                            else if (boundingBox.x < 15){
                                System.out.println("3");
                                robotController.sendCommand("3");
                            }
                            else if(boundingBox.x > 600)
                            {
                                System.out.println("4");
                                robotController.sendCommand("4");
                            }
                        }
                    }

                    // Przesyłanie klatki jako obraz JPEG
                    byte[] imageBytes = matToBytes(frame);
                    if (imageBytes != null) {
                        outputStream.write(imageBytes);
                        outputStream.flush();
                    }

                    // Wyświetlanie widoku kamerki lokalnie
//                    HighGui.imshow("Oryginalny obraz z zaznaczeniem", frame);
//
//                    // Wyjście po naciśnięciu klawisza 'q'
//                    if (HighGui.waitKey(30) == 'q') {
//                        break;
//                    }
                } else {
                    System.out.println("Nie udało się odczytać klatki z kamerki");
                    break;
                }


            }
        } catch (Exception e) {
            System.err.println("Błąd podczas przesyłania obrazu: " + e.getMessage());
        } finally {
            camera.release();
            HighGui.destroyAllWindows();
        }
    }

    private static byte[] matToBytes(Mat mat) {
        try {
            MatOfByte buffer = new MatOfByte();
            Imgcodecs.imencode(".jpg", mat, buffer);
            return buffer.toArray();
        } catch (Exception e) {
            System.err.println("Błąd konwersji Mat na JPEG: " + e.getMessage());
            return null;
        }
    }
}
