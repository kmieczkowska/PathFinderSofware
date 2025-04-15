package org.example;

import com.fazecast.jSerialComm.SerialPort;
import org.example.Camera.CameraDetector;
import org.example.Configuration.ConfigurationLoader;
import org.example.RobotController.IRobotController;
import org.example.RobotController.RobotController;
import org.example.RobotController.VirtualRobotController;
import org.example.Services.ClockService;

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
        int ROBOT_STRATEGY = Integer.parseInt(config.getRobotStretegy());
        int MOTOR_A_DIRECTION = Integer.parseInt(config.getMotorADirection());
        int MOTOR_B_DIRECTION = Integer.parseInt(config.getMotorBDirection());
        int MOTOR_A_POWER = Integer.parseInt(config.getMotorAPower());
        int MOTOR_B_POWER = Integer.parseInt(config.getMotorBPower());
        long RUNNING_DURATION = Integer.parseInt(config.getMotorBPower());;
        String NAME_OF_CVS_FILE = config.getNameOfCvsFile();

        // Connecting to a microcontroller
        try {
                if (SERIAL_LOCAL_MODE == 1) {
                        serialPort = SerialPort.getCommPort("COM3");
                } else {
                        serialPort = SerialPort.getCommPort("/dev/ttyACM0");
                }
                serialPort.setComPortParameters(115200, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
                serialPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);
                serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 0);


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

        robotController.setMotorAPower(MOTOR_A_POWER);
        robotController.setMotorBPower(MOTOR_B_POWER);

        // Openning the socket
        System.out.println("# Waiting for the tester to grant access...");
        int serverPort = 1234;
        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
                Socket socket = serverSocket.accept();
                System.out.println("# I've connected to the tester!");

                outputStream = socket.getOutputStream();
                inputStream = socket.getInputStream();

                if (ROBOT_STRATEGY != 0) { // Testing the project by pc and camera

                ClockService clockService = new ClockService();

                CameraDetector detector = new CameraDetector(robotController, inputStream, outputStream, ROBOT_STRATEGY);

                detector.start(clockService);

                robotController.saveDataRobot(clockService,NAME_OF_CVS_FILE);

                clockService.start();

                while (clockService.running.get());

                detector.join();
                robotController.join();

                robotController.setMovmentSpeed(0,0);
                serialPort.closePort();

                }

        } catch (Exception e) {
                e.printStackTrace();
        }
}
}
