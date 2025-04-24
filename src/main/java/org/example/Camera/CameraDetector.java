package org.example.Camera;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.opencv_java;
import org.example.RobotController.IRobotController;
import org.example.Services.ClockService;
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

    private Thread clientHandler;
    private IRobotController robotController;
    private InputStream inputStream;
    private OutputStream outputStream;
    private int ROBOT_STRATEGY;

    public CameraDetector(IRobotController _robotController,
                          InputStream _inputStream,
                          OutputStream _outputStream,
                          int robotStrategy)
    {
        robotController = _robotController;
        inputStream = _inputStream;
        outputStream = _outputStream;
        ROBOT_STRATEGY = robotStrategy;
    }

    /**
     * Uruchomienie kamery; wyświetlenie na obrazie elementów strategii
     */
    public void start(ClockService clockService) throws InterruptedException {
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
            while (clockService.running.get()) {

                if (capture.read(frame)) {
                    clockService.increment();
                    try {
                        switch (ROBOT_STRATEGY){
                            case 1:
                                frame = imageProcesor.strategy1(frame);
                                break;
                            case 2:
                                frame = imageProcesor.strategy2(frame);
                                break;
                            case 3:
                                frame = imageProcesor.strategy3(frame);
                                break;
                            case 4:
                                frame = imageProcesor.strategy4(frame);
                                break;
                            case 5:
                                frame = imageProcesor.strategy5(frame);
                                break;
                            case 6:
                                frame = imageProcesor.strategy6(frame);
                                break;
                        }
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
            System.out.println("# Camera closed!");

        });
        clientHandler.start();
    }

    public void join() throws InterruptedException {
        if (clientHandler != null) {
            clientHandler.join(); // 🧵 Wait for thread to finish
        }
    }


}
