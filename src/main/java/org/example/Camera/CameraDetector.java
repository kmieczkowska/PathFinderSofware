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
 */
public class CameraDetector {

    private int manual = 0;

    private OutputStream outputStream;
    private InputStream inputStream;

    private IRobotController robotController;

    public CameraDetector(IRobotController _robotController) {
        robotController = _robotController;
    }

    public void start() {
        // Load OpenCV library
        Loader.load(opencv_java.class);
        ImageProcesor imageProcesor = new ImageProcesor(robotController);
        Thread responseHandler = new Thread(() -> {
            while (true) {
                try {
                    ClientPackageClass.ClientPackage clientPackage = receiveClientPackage();
                    imageProcesor.setTreshold(clientPackage.getTreshold());
                    robotController.setFlashlightBrightness(clientPackage.getFlashlightBrightness());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
//                String clientResponse = readClientResponse(inputStream);
//                if (clientResponse != null) {
//                    if (clientResponse.equals("manual")) imageProcesor.setStop();
//                    else if (clientResponse.equals("server")) imageProcesor.setStart();
//                }
            }
        });
        Thread clientHandler = new Thread(() -> {
                while (true) { // Loop to handle multiple client connections
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
                                frame = imageProcesor.strategy2(frame); // przetwarzanie kamery
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

                    }
                }
        });
        System.out.println("Waiting for client connection...");
        int serverPort = 1234;
        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            Socket socket = serverSocket.accept();
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
        } catch (Exception e) {
        e.printStackTrace();
        }


        clientHandler.start();
        responseHandler.start();

        try {
            clientHandler.join();
            responseHandler.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private String readClientResponse(InputStream inputStream) {
        try {
            // Read the size of the response (4 bytes)
            byte[] sizeBuffer = new byte[4];
            if (inputStream.read(sizeBuffer) != 4) return null;
            int responseSize = ByteBuffer.wrap(sizeBuffer).getInt();

            // Read the actual response
            byte[] responseBuffer = new byte[responseSize];
            int bytesRead = 0;
            while (bytesRead < responseSize) {
                int result = inputStream.read(responseBuffer, bytesRead, responseSize - bytesRead);
                if (result == -1) break;
                bytesRead += result;
            }

            return new String(responseBuffer, 0, bytesRead);
        } catch (Exception e) {
            System.err.println("Error reading client response:" + e.getMessage());
            return null;
        }
    }

    public ClientPackageClass.ClientPackage receiveClientPackage() throws IOException {
        byte[] sizeBuffer = new byte[4];
        if (inputStream.read(sizeBuffer) != 4) {
            throw new IOException("Failed to read message size.");
        }
        int messageSize = ByteBuffer.wrap(sizeBuffer).getInt();

        // Read the actual message
        byte[] messageBuffer = new byte[messageSize];
        int bytesRead = 0;
        while (bytesRead < messageSize) {
            int result = inputStream.read(messageBuffer, bytesRead, messageSize - bytesRead);
            if (result == -1) {
                throw new IOException("Stream closed before reading the full message.");
            }
            bytesRead += result;
        }
        return ClientPackageClass.ClientPackage.parseFrom(messageBuffer);
    }

    private void invokeSetMovementSpeed(String response) {
        try {
            // Split the response by commas (expected format: "motorA,motorB,delay")
            String[] parts = response.split(",");
            if (parts.length != 3) {
                System.err.println("Invalid response format:" + response);
                return;
            }

            int motorA = Integer.parseInt(parts[0].trim());
            int motorB = Integer.parseInt(parts[1].trim());
            int delay = Integer.parseInt(parts[2].trim());

            // Call the robot controller method
            setSpeed(motorA, motorB, delay);

            System.out.println("Set movement speed: motorA=" + motorA + ", motorB=" + motorB + ", delay=" + delay);
        } catch (Exception e) {
            System.err.println("Error parsing response or invoking setMovementSpeed: " + e.getMessage());
        }
    }
    private void setSpeed(int motorA, int motorB, int delay){
        robotController.setMovmentSpeed(motorA,motorB);
        robotController.delay(delay);
        robotController.setMovmentSpeed(0,0);
    }

}
