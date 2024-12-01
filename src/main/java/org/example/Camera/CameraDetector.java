package org.example.Camera;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.opencv_java;
import org.example.RobotController.IRobotController;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * Otwarcie socketu, kamerki, ustawienia kamerki
 * @param
 * @return
 */
public class CameraDetector {

    private IRobotController robotController;

    public CameraDetector(IRobotController _robotController) {
        robotController = _robotController;
    }

    public void start() {
        // Load OpenCV library
        Loader.load(opencv_java.class);

        int serverPort = 1234;
        ImageProcesor imageProcesor = new ImageProcesor(robotController);

        Thread clientHandler = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
                System.out.println("Server listening on port " + serverPort);

                while (true) { // Loop to handle multiple client connections
                    try (Socket socket = serverSocket.accept();
                         OutputStream outputStream = socket.getOutputStream()) {

                        System.out.println("Client connected: " + socket.getInetAddress());

                        VideoCapture capture = new VideoCapture(0);  // Open default camera
                        if (!capture.isOpened()) {
                            System.out.println("Error: Cannot open video source.");
                            break;
                        }

                        Mat frame = new Mat();
                        MatOfByte buffer = new MatOfByte();

                        while (capture.read(frame)) {
                            try {
                                frame = imageProcesor.proses(frame); // przetwarzanie kamery
                                // Encode frame as JPEG
                                Imgcodecs.imencode(".jpg", frame, buffer);
                                byte[] imageBytes = buffer.toArray();

                                // Send the size of the frame first (4 bytes)
                                byte[] sizeBuffer = ByteBuffer.allocate(4).putInt(imageBytes.length).array();
                                outputStream.write(sizeBuffer);

                                // Send the frame data
                                outputStream.write(imageBytes);
                                outputStream.flush();  // Flush to ensure data is sent

                            } catch (Exception e) {
                                System.err.println("Error sending frame: " + e.getMessage());
                                break;  // Exit the loop if there is an error sending data
                            }
                        }

                        capture.release();  // Release the camera

                    } catch (Exception e) {
                        System.err.println("Client connection error: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        clientHandler.start();

        // Optionally keep main thread alive
        try {
            clientHandler.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
