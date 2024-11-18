package org.example;

import com.fazecast.jSerialComm.SerialPort;
import org.example.Configuration.ConfigurationLoader;
import org.example.RobotController.RobotController;

public class Main {
        public static void main(String[] args) {

        SerialPort serialPort;

        ConfigurationLoader config = new ConfigurationLoader("configuration.properties");

        int SERIAL_LOCAL_MODE = Integer.valueOf(config.getSerialMode());
        int ROBOT_MODE = Integer.valueOf(config.getRobotMode());


        if(SERIAL_LOCAL_MODE ==  1){serialPort = SerialPort.getCommPort("COM3");}
        else{ serialPort = SerialPort.getCommPort("/dev/ttyACM0"); }

        serialPort.setComPortParameters(115200, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);

        RobotController robotController = new RobotController(serialPort);

        if(ROBOT_MODE == 0){throw new UnsupportedOperationException("Not implemented yet");}
        else if(ROBOT_MODE == 1){ robotController.strategy_1();}
        else if(ROBOT_MODE == 2){ robotController.debug();}

    }
}