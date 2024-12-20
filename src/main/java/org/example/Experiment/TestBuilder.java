package org.example.Experiment;

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
}
