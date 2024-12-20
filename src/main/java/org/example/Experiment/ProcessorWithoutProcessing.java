package org.example.Experiment;

import org.opencv.core.Mat;

public class ProcessorWithoutProcessing implements IFrameProcessor{
    @Override
    public Mat processFrame(Mat frame) {
        return frame;
    }
}
