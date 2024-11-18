package org.example;

import com.fazecast.jSerialComm.SerialPort;
import org.example.Configuration.ConfigurationLoader;
import org.example.RobotController.RobotController;

public class Main {
    /*---------------START OF CONFIGURAITON----------------------*/
    /* ROBOT_MODE resposible for
    @ 0 - mode that using camera and server to contoll robot
    @ 1 - mode that using redlight sensor and client to controll robot
    @ 2 - put robot in DEBUG mode
        */
    public static final int ROBOT_MODE = 2; // robot
    public static final int SERIAL_LOCAL_MODE = 0; // local

    /*---------------END OF CONFIGURAITON------------------------*/

    //TODO plik konfiguracyjny

    public static void main(String[] args) {

        SerialPort serialPort;

        ConfigurationLoader config = new ConfigurationLoader("configuration.properties");
        SERIAL_LOCAL_MODE = config.getSerialMode();

        if(SERIAL_LOCAL_MODE ==  1){
            serialPort = SerialPort.getCommPort("COM3");
        }
        else{
            serialPort = SerialPort.getCommPort("/dev/ttyACM0");
        }
        RobotController robotController = new RobotController(serialPort);

        if(ROBOT_MODE == 0){
            throw new UnsupportedOperationException("Not implemented yet");
        }
        else if(ROBOT_MODE == 1){
            robotController.strategy_1();
        }
        else if(ROBOT_MODE == 2){
            robotController.debug();
        }

    }
}