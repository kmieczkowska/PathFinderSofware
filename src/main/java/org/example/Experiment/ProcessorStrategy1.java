package org.example.Experiment;

import org.example.RobotController.SilentRobotController;
import org.opencv.core.Mat;
import org.example.Camera.ImageProcesor;
import org.example.RobotController.IRobotController;
import org.example.RobotController.VirtualRobotController;

public class ProcessorStrategy1 implements IFrameProcessor {

    IRobotController robotController = new SilentRobotController();
    ImageProcesor imageProcesor = new ImageProcesor(robotController);
    @Override
    public Mat processFrame(Mat frame) {
        return imageProcesor.strategy1(frame);
    }
}
