package org.example.Experiment;

import org.example.Camera.ImageProcesor;
import org.example.RobotController.IRobotController;
import org.example.RobotController.SilentRobotController;
import org.opencv.core.Mat;

/*
 * TestBuilder to klasa która pozwala wybrać wyłączne strategie przetwarzania obrazu bez pozostałej logiki robota.
 * W ExperimentFpsTest jest użyta w testach aby przetestować wyłacznie stretegie dla przetwarzania orbazu dla ilości klatek na sekunde.
 */

public class TestBuilder {

    IFrameProcessor processor;
    String prefix = "";
    String fileName;

    public TestBuilder(int implementation, String _prefix) {
        prefix = _prefix;
        switch(implementation) {
            case 1:
                fileName = prefix + "ProcessorStrategy1.csv";
                processor = new ProcessorStrategy1();
                break;
            case 2:
                fileName = prefix + "ProcessorStrategy2.csv";
                processor = new ProcessorStrategy2();
                break;
            default:
                fileName = prefix + "ProcessorWithoutProcessing.csv";
                processor = new ProcessorWithoutProcessing();
        }
    }

    public class ProcessorStrategy1 implements IFrameProcessor {

        IRobotController robotController = new SilentRobotController();
        ImageProcesor imageProcesor = new ImageProcesor(robotController);
        @Override
        public Mat processFrame(Mat frame) {
            return imageProcesor.strategy1(frame);
        }
    }

    public class ProcessorStrategy2 implements IFrameProcessor {

        IRobotController robotController = new SilentRobotController();
        ImageProcesor imageProcesor = new ImageProcesor(robotController);
        @Override
        public Mat processFrame(Mat frame) {
            return imageProcesor.strategy2(frame);
        }
    }

    public class ProcessorWithoutProcessing implements IFrameProcessor{
        @Override
        public Mat processFrame(Mat frame) {
            return frame;
        }
    }

    public interface IFrameProcessor {
        public Mat processFrame(Mat frame);
    }


}
