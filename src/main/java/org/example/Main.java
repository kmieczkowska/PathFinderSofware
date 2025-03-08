package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fazecast.jSerialComm.SerialPort;
import org.example.Configuration.ConfigurationLoader;
import org.example.RobotController.IRobotController;
import org.example.RobotController.RobotController;
import org.example.RobotController.VirtualRobotController;

import java.io.InputStream;
import java.io.OutputStream;

public class Main {

        public static void main(String[] args) {

        SerialPort serialPort = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        IRobotController robotController;
        boolean initializionPort = false;

        // Wczytanie wartości z pliku kofiguracyjnego
        ConfigurationLoader config = new ConfigurationLoader("configuration.properties");
        int SERIAL_LOCAL_MODE = Integer.valueOf(config.getSerialMode());
        int ROBOT_MODE = Integer.valueOf(config.getRobotMode());
        int MOTOR_A_DIRECTION = Integer.valueOf(config.getMotorADirection());
        int MOTOR_B_DIRECTION = Integer.valueOf(config.getMotorBDirection());

        // Próba połączenia się z mikrokontrolerem robota do sterowania robotem
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

        robotController.setMotorADirection(MOTOR_A_DIRECTION);
        robotController.setMotorBDirection(MOTOR_B_DIRECTION);

        // Otworzenie soketu czekanie na połączenie z clientem
//        System.out.println("Waiting for client connection...");
//        int serverPort = 1234;
//        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
//                Socket socket = serverSocket.accept();
//                outputStream = socket.getOutputStream();
//                inputStream = socket.getInputStream();
//        } catch (Exception e) {
//                e.printStackTrace();
//        }

        //ServerCommunication serverCommunication = new ServerCommunication(robotController,inputStream,outputStream);

        //CameraDetector detector = new CameraDetector(robotController,inputStream,outputStream);

        //if(ROBOT_MODE == 0){ detector.start(); }
        if(ROBOT_MODE == 1){
            try {
                robotController.saveDataRobot();
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

    }
}