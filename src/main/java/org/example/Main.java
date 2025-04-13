package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fazecast.jSerialComm.SerialPort;
import org.example.Camera.CameraDetector;
import org.example.Configuration.ConfigurationLoader;
import org.example.RobotController.IRobotController;
import org.example.RobotController.RobotController;
import org.example.RobotController.VirtualRobotController;
import org.opencv.core.Core;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

public static void main(String[] args) {

        /*WARNING!*/
        /*Remember to set the appropriate MODE in "configuration.properties" file!*/

        SerialPort serialPort = null;
        boolean initializationPort = true;
        IRobotController robotController;
        InputStream inputStream = null;
        OutputStream outputStream = null;

        // Loading the configuration
        ConfigurationLoader config = new ConfigurationLoader("configuration.properties");

        int SERIAL_LOCAL_MODE = Integer.parseInt(config.getSerialMode());
        int ROBOT_MODE = Integer.parseInt(config.getRobotMode());
        int MOTOR_A_DIRECTION = Integer.parseInt(config.getMotorADirection());
        int MOTOR_B_DIRECTION = Integer.parseInt(config.getMotorBDirection());

        // Connecting to a microcontroller
        try {
                if (SERIAL_LOCAL_MODE == 1) {
                        serialPort = SerialPort.getCommPort("COM3");
                } else {
                        serialPort = SerialPort.getCommPort("/dev/ttyACM0");
                }
                serialPort.setComPortParameters(115200, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
                serialPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);
                initializationPort = serialPort.openPort();
        } catch (Exception e) {
                e.printStackTrace();
        }

        if (initializationPort) {
                robotController = new RobotController(serialPort);
        } else {
                robotController = new VirtualRobotController();
        }

        // Robot's motors settings
        robotController.setMotorADirection(MOTOR_A_DIRECTION);
        robotController.setMotorBDirection(MOTOR_B_DIRECTION);

        // Openning the socket
        System.out.println("# Waiting for the tester to grant access...");
        int serverPort = 1234;
        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
                Socket socket = serverSocket.accept();
                System.out.println("# I've connected to the tester!");

                outputStream = socket.getOutputStream();
                inputStream = socket.getInputStream();

                if (ROBOT_MODE == 0) { // Testing the project by pc and camera
                        CameraDetector detector = new CameraDetector(robotController, inputStream, outputStream);
                        detector.start();
                }
                else if (ROBOT_MODE == 1) { // Testing the project by robot
                        robotController.saveDataRobot();
                }

        } catch (Exception e) {
                e.printStackTrace();
        }
}
}
