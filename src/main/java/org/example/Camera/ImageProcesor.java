package org.example.Camera;

import org.example.RobotController.IRobotController;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
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

    public Mat proses(Mat frame){

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

                if(boundingBox.x >= 15 && boundingBox.x <= 600) robotController.sendCommand("1");
                else if (boundingBox.x < 15) robotController.sendCommand("3");
                else if(boundingBox.x > 600) robotController.sendCommand("4");

            }
        }
        return frame;
    }
}
