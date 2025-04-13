package org.example.Camera;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.opencv_java;
import org.example.RobotController.IRobotController;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Otwarcie socketu, kamerki, ustawienia kamerki
 * Wykorzystanie przetworzonego obrazu z kamery
 */
public class CameraDetector {

    private IRobotController robotController;
    private InputStream inputStream;
    private OutputStream outputStream;

    public CameraDetector(IRobotController _robotController,
                          InputStream _inputStream,
                          OutputStream _outputStream)
    {
        robotController = _robotController;
        inputStream = _inputStream;
        outputStream = _outputStream;
    }

    /**
     * Uruchomienie kamery; wyświetlenie na obrazie elementów strategii
     */
    public void start() {
        Loader.load(opencv_java.class);
        ImageProcesor imageProcesor = new ImageProcesor(robotController);

        Thread clientHandler = new Thread(() -> {

            System.out.println("• Camera opening...");

            VideoCapture capture = new VideoCapture(0);  // 0 - usb camera
            if (!capture.isOpened()) {
                System.out.println("Error: Cannot open video source.");
                return;
            }
            System.out.println("• Camera is working!");

            Mat frame = new Mat();
            MatOfByte buffer = new MatOfByte();

            System.out.println("# Reading camera frame...");
            System.out.println("# Robot goes pyr pyr pyr ...");
            while (true) {

                if (capture.read(frame)) {

                    try {
                        frame = imageProcesor.strategy1(frame); // CHOSE YOUR STRATEGY!!

                        Imgcodecs.imencode(".jpg", frame, buffer);
                        byte[] imageBytes = buffer.toArray();

                        // Send frame size
                        byte[] sizeBuffer = ByteBuffer.allocate(4).putInt(imageBytes.length).array();
                        outputStream.write(sizeBuffer);

                        // Send frame bytes
                        outputStream.write(imageBytes);
                        outputStream.flush();

                    } catch (Exception e) {
                        System.err.println("Error sending frame: " + e.getMessage());
                        break;
                    }
                }

            }

            capture.release();
        });

        clientHandler.start();
    }
}
