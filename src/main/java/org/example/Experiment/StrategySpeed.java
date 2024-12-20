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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;

public class StrategySpeed {
    public static void main(String[] args) {
        TestCameraSpeed(0);
        TestCameraSpeed(1);
        TestCameraSpeed(2);
    }
    
    public static void TestCameraSpeed(int implementation) {

        long startTime;
        long startTimeSecond;
        long endTime;
        int frameCount;
        double elapsedTimeSeconds;
        double fps;

        TestBuilder testBuilder = new TestBuilder(implementation, "Desktop_");

        Loader.load(opencv_java.class);

        VideoCapture camera = new VideoCapture(0);

        if (!camera.isOpened()) {
            System.out.println("Error: Camera is not accessible!");
            return;
        }

        try (FileWriter writer = new FileWriter("data" + File.separator +"python" + File.separator + testBuilder.fileName)) {
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

                        testBuilder.processor.processFrame(frame);
                        frameCount++;

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

