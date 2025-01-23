package org.example.Camera;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.opencv_java;
import org.example.RobotController.IRobotController;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * Otwarcie socketu, kamerki, ustawienia kamerki
 * @param
 * @return
 *
 * TO DO:
 * trzeba przerobic kod tak aby był wybor czy chcemu przetwarzac kod na robcie czy serwerze
 * trzeba rozidelc ta klse na samo zgarnianie obrazu a stratefgi przenisesc gdies iniedzj.
 *
 */
public class CameraDetector {

    private int manual = 0;

    private OutputStream outputStream;
    private InputStream inputStream;
    private IRobotController robotController;

    public CameraDetector(IRobotController _robotController,InputStream _inputStream,OutputStream _outputStream) {

        robotController = _robotController;
        outputStream = _outputStream;
        inputStream = _inputStream;
    }

    public void start() {
        // Load OpenCV library
        Loader.load(opencv_java.class);
        ImageProcesor imageProcesor = new ImageProcesor(robotController);
        Thread clientHandler = new Thread(() -> {
                while (true) {
                    {
                        VideoCapture capture = new VideoCapture(0);  // Open default camera
                        if (!capture.isOpened()) {
                            System.out.println("Error: Cannot open video source.");
                            break;
                        }
                        Mat frame = new Mat();
                        MatOfByte buffer = new MatOfByte();
                        while (capture.read(frame)) {
                            try {
                                frame = imageProcesor.strategy2(frame);
                                Imgcodecs.imencode(".jpg", frame, buffer);
                                byte[] imageBytes = buffer.toArray();

                                // Send the size of the frame first (4 bytes)
                                byte[] sizeBuffer = ByteBuffer.allocate(4).putInt(imageBytes.length).array();
                                outputStream.write(sizeBuffer);

                                // Send the frame data
                                outputStream.write(imageBytes);
                                outputStream.flush();

                            } catch (Exception e) {
                                System.err.println("Error sending frame: " + e.getMessage());
                                break;
                            }
                        }
                        capture.release();
                    }
                }
        });
        clientHandler.start();
    }


}
