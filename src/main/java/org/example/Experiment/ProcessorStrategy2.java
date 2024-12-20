package org.example.Experiment;

import org.example.Camera.ImageProcesor;
import org.example.RobotController.IRobotController;
import org.example.RobotController.VirtualRobotController;
import org.opencv.core.Mat;

public class ProcessorStrategy2 implements IFrameProcessor {

    IRobotController robotController = new VirtualRobotController();
    ImageProcesor imageProcesor = new ImageProcesor(robotController);
    @Override
    public Mat processFrame(Mat frame) {
        return imageProcesor.strategy2(frame);
    }
}
