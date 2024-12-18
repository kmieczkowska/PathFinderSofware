package org.example.Experiment;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.opencv_java;
import org.example.Camera.ImageProcesor;
import org.example.RobotController.IRobotController;
import org.example.RobotController.RobotController;
import org.example.RobotController.VirtualRobotController;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;

public class StrategySpeed {
    public static void main(String[] args) {
        long startTime;
        long endTime;
        int frameCount;
        IRobotController robotController = new VirtualRobotController();
        ImageProcesor imageProcesor = new ImageProcesor(robotController);


        Loader.load(opencv_java.class);

        VideoCapture camera = new VideoCapture(0);

        if (!camera.isOpened()) {
            System.out.println("Error: Camera is not accessible!");
            return;
        }

        startTime = System.nanoTime();
        frameCount = 0;
        Mat frame = new Mat();

        long duration = 5_000_000_000L; // 5 sekund w nanosekundach
        while (System.nanoTime() - startTime < duration) {
            if (camera.read(frame)) {
                frameCount++;
                frame = imageProcesor.strategy2(frame);
            } else {
                System.out.println("Error: Could not read a frame.");
            }
        }

        endTime = System.nanoTime();
        double elapsedTimeSeconds = (endTime - startTime) / 1_000_000_000.0;
        double fps = frameCount / elapsedTimeSeconds;

        System.out.println("Frames analyzed: " + frameCount);
        System.out.println("Elapsed time: " + elapsedTimeSeconds + " seconds");
        System.out.println("Frames per second (FPS): " + fps);

        String fileName = "results.csv";
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.append("Frames Analyzed,Elapsed Time (s),FPS\n"); // Nagłówki
            writer.append(frameCount + "," + elapsedTimeSeconds + "," + fps + "\n"); // Dane
            System.out.println("Results saved to " + fileName);
        } catch (IOException e) {
            System.err.println("Error saving to CSV: " + e.getMessage());
        }


    }
}

