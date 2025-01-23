package org.example.Experiment;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.opencv_java;
import org.example.OldLegacyCode.OldTestsToRefactor.Usage;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ExperimentFpsTest {
    public static void main(String[] args) {

        FpsTest(0);
        FpsTest(1);
        FpsTest(2);
    }

    public static void FpsTest(int implementation) {

        final long TEST_DURATION_NS = 300_000_000_000L; // 5 minutes
        final long SAMPLE_DURATION_NS = 5_000_000_000L; // 5 seconds

        long startTime;
        long startTimeSecond;
        long endTime;
        int frameCount;
        double elapsedTimeSeconds;
        double fps;



        TestBuilder testBuilder = new TestBuilder(implementation, "Desktop_");
        //TestBuilder testBuilder = new TestBuilder(implementation, "RaspberryPi");

        Usage usage = new Usage();

        Loader.load(opencv_java.class);

        Mat frame = new Mat();

        VideoCapture camera = new VideoCapture(0);

        if (!camera.isOpened()) {
            System.out.println("Error: Camera is not accessible!");
            return;
        }

        System.out.println("Processing "+"["+implementation+"]"+" started at: " + new java.util.Date());

        try (FileWriter writer = new FileWriter("python" + File.separator + "data" + File.separator + testBuilder.fileName)) {
            writer.append("Frames Analyzed,Elapsed Time (s),FPS,CPU Load, Memory usage Percentage\n");
            startTime = System.nanoTime();
            // 300 sekund = 5 min
            while (System.nanoTime() - startTime < TEST_DURATION_NS) {
                frameCount = 0;
                startTimeSecond = System.nanoTime();
                // 5 sekund
                while (System.nanoTime() - startTimeSecond < SAMPLE_DURATION_NS) {
                    if (camera.read(frame) && !frame.empty()) {
                        testBuilder.processor.processFrame(frame);

                        frameCount++;
                    } else {
                        System.out.println("Error: Could not read a frame.");
                    }
                }
                usage.getUsage();
                endTime = System.nanoTime();
                elapsedTimeSeconds = (endTime - startTimeSecond) / 1_000_000_000.0;
                fps = frameCount / elapsedTimeSeconds;
                writer.append(frameCount + "," + ((endTime-startTime)/ 1_000_000_000.0) + "," + fps + ","); // Dane
                writer.append(usage.cpuLoad + ",");
                writer.append(usage.usedMemoryPercentage + "");
                writer.append("\n");
            }
        } catch(IOException e){
            System.err.println("Error saving to CSV: " + e.getMessage());
        }finally {
            camera.release();
            System.out.println("Processing "+"["+implementation+"]"+" ended at: " + new java.util.Date());
        }
    }
}

