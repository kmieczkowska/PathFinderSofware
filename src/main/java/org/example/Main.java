package org.example;

import com.fazecast.jSerialComm.SerialPort;
import org.example.RobotController.RobotController;

public class Main {
    /*---------------START OF CONFIGURAITON----------------------*/
    /* ROBOT_MODE resposible for
    @ 0 - mode that using camera and server to contoll robot
    @ 1 - mode that using redlight sensor and client to controll robot
     */
    public static final int ROBOT_MODE = 1;
    /*---------------END OF CONFIGURAITON------------------------*/

    public static void main(String[] args) {

        SerialPort serialPort = SerialPort.getCommPort("COM3");
        RobotController robotController = new RobotController(serialPort);

        if(ROBOT_MODE == 0){
            throw new UnsupportedOperationException("Not implemented yet");
        }
        else if(ROBOT_MODE == 1){
            robotController.strategy_1();
        }

    }
}