package org.example.OldClient;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.opencv_java;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Server {
    static {
        Loader.load(opencv_java.class);
    }

    public static void main(String[] args) {
        int port = 5000;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server listening on port " + port);
            Socket socket = serverSocket.accept();
            OutputStream outputStream = socket.getOutputStream();

            VideoCapture capture = new VideoCapture(0);  // 0 for default camera
            if (!capture.isOpened()) {
                System.out.println("Error: Cannot open video source.");
                return;
            }

            Mat frame = new Mat();
            MatOfByte buffer = new MatOfByte();

            while (capture.read(frame)) {
                Imgcodecs.imencode(".jpg", frame, buffer);
                byte[] imageBytes = buffer.toArray();

                // Send the size of the frame first
                byte[] sizeBuffer = ByteBuffer.allocate(4).putInt(imageBytes.length).array();
                outputStream.write(sizeBuffer);

                // Send the frame data
                outputStream.write(imageBytes);
            }

            capture.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
