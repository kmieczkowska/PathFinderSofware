package org.example.Experiment;

import org.opencv.core.Mat;

import java.awt.*;

public interface IFrameProcessor {
    public Mat processFrame(Mat frame);
}
