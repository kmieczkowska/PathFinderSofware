package org.example.OldLegacyCode.OldTestsToRefactor;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.opencv_java;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

public class MemoryUsageFrame {
    public static void main(String[] args) {

        long startTime;
        long endTime;
        int frameCount = 0;
        double elapsedTimeSeconds;
        double fps;
        final long SAMPLE_DURATION_NS = 5_000_000_000L;

        long frameSizeInBytes;
        double sumFrameSizeInBytes = 0;
        Loader.load(opencv_java.class);
        Mat frame = new Mat();
        VideoCapture camera = new VideoCapture(0);

        if (!camera.isOpened()) {
            System.out.println("Error: Camera is not accessible!");
            return;
        }
        startTime = System.nanoTime();

        while (System.nanoTime() - startTime < SAMPLE_DURATION_NS) {
            if (camera.read(frame) && !frame.empty()) {
                frameSizeInBytes = (long) frame.cols() *  frame.rows() * frame.channels() * (frame.elemSize1());
                sumFrameSizeInBytes = sumFrameSizeInBytes + frameSizeInBytes;
                frameCount++;
            } else {
                System.out.println("Error: Could not read a frame.");
            }
        }
        endTime = System.nanoTime();
        elapsedTimeSeconds = (endTime - startTime) / 1_000_000_000.0;
        fps = frameCount / elapsedTimeSeconds;

        double sumFrameSizeInMB = sumFrameSizeInBytes / (1024.0 * 1024.0);

        System.out.println("FPS: " + fps);
        System.out.println("Total Frame Size in MB: " + sumFrameSizeInMB / 5 + " MB");


    }
}
