package org.example;

import com.fazecast.jSerialComm.SerialPort;
import org.example.Camera.CameraDetector;
import org.example.Configuration.ConfigurationLoader;
import org.example.RobotController.IRobotController;
import org.example.RobotController.RobotController;
import org.example.RobotController.VirtualRobotController;

public class Main {
        public static void main(String[] args) {

        SerialPort serialPort = null;

        ConfigurationLoader config = new ConfigurationLoader("configuration.properties");

        int SERIAL_LOCAL_MODE = Integer.valueOf(config.getSerialMode());
        int ROBOT_MODE = Integer.valueOf(config.getRobotMode());

        IRobotController robotController;

        boolean initializionPort = false;

        try {
                if(SERIAL_LOCAL_MODE ==  1){serialPort = SerialPort.getCommPort("COM3");}
                else{ serialPort = SerialPort.getCommPort("/dev/ttyACM0"); }
                serialPort.setComPortParameters(115200, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
                serialPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);
                initializionPort = serialPort.openPort();



        }
        catch (Exception e) {e.printStackTrace();}

        if(initializionPort) robotController = new RobotController(serialPort);
        else robotController = new VirtualRobotController();



        CameraDetector detector = new CameraDetector(robotController);

//        int MOVE_FWD_15 = Integer.valueOf(config.getRobotMoveFWD_15());
//        int MOVE_FWD_600 = Integer.valueOf(config.getRobotMoveFWD_600());
//        int MOVE_LEFT_15 = Integer.valueOf(config.getRobotTurnLEFT_15());
//        int MOVE_RIGHT_600 = Integer.valueOf(config.getRobotMoveRIGHT_600());


        if(ROBOT_MODE == 0){detector.start();}
        else if(ROBOT_MODE == 1){ robotController.strategy_1();}


    }
}