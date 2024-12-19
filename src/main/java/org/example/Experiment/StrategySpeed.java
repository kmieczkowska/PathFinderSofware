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
        long startTimeSecond;
        long endTime;
        int frameCount;
        double elapsedTimeSeconds;
        double fps;
        IRobotController robotController = new VirtualRobotController();
        ImageProcesor imageProcesor = new ImageProcesor(robotController);
        String fileName = "desktop_strategy_1.csv";

        Loader.load(opencv_java.class);

        VideoCapture camera = new VideoCapture(0);

        if (!camera.isOpened()) {
            System.out.println("Error: Camera is not accessible!");
            return;
        }

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.append("Frames Analyzed,Elapsed Time (s),FPS\n");
            startTime = System.nanoTime();
            // 300 sekund = 5 min
            while (System.nanoTime() - startTime < 300_000_000_000L) {
                frameCount = 0;
                Mat frame = new Mat();
                startTimeSecond = System.nanoTime();
                // 5 sekund
                while (System.nanoTime() - startTimeSecond < 5_000_000_000L) {
                    if (camera.read(frame)) {
                        frameCount++;
                        frame = imageProcesor.strategy2(frame);
                    } else {
                        System.out.println("Error: Could not read a frame.");
                    }
                }
                endTime = System.nanoTime();
                elapsedTimeSeconds = (endTime - startTimeSecond) / 1_000_000_000.0;
                fps = frameCount / elapsedTimeSeconds;
                writer.append(frameCount + "," + ((endTime-startTime)/ 1_000_000_000.0) + "," + fps + "\n"); // Dane
            }
        } catch(IOException e){
            System.err.println("Error saving to CSV: " + e.getMessage());
        }
    }
}

