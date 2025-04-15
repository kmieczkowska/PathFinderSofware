package org.example.RobotController;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fazecast.jSerialComm.SerialPort;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.example.Services.ClockService;
import org.example.Services.SerialPortService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * Sterowanie robotem
 */
public class RobotController implements IRobotController {

    private Thread saveDataHandler;

    SerialPort serialPort;

    SerialPortService serialPortService;

    RobotDataJson deserializedRobotData;

    private int motorADirection = 1;
    private int motorBDirection = 1;
    private int motorAPower = 100;
    private int motorBPower = 100;

    public void setMotorAPower(int power) {
        motorAPower = power;
    }

    public void setMotorBPower(int power) {
        motorBPower = power;
    }

    @Override
    public void setMotorADirection(int direction) {
        motorADirection = direction;
    }

    @Override
    public void setMotorBDirection(int direction) {
        motorBDirection = direction;
    }

    @Override
    public void setMotorADirectionForward() {
        motorADirection = 0;
    }

    @Override
    public void setMotorADirectionBackward() {
        motorADirection = 1;
    }

    @Override
    public void setMotorBDirectionForward() {
        motorBDirection = 0;
    }

    @Override
    public void setMotorBDirectionBackward() {
        motorBDirection = 1;
    }

    /**
     * RobotController
     * Inicjowanie serialPort
     *
     * @param serialPortInit
     */
    public RobotController(SerialPort serialPortInit) {
        serialPort = serialPortInit;
        serialPortService = new SerialPortService(serialPort);

    }

    public void strategy_1() throws JsonProcessingException {
        readRobotData();
    }

    @Override
    public void emergencyStop() {
        setMovmentSpeed(0, 0);
    }

    @Override
    public void moveReverse() {
        setMovmentSpeed((motorADirection * 100) + 100, (motorBDirection * 100) + 100);
    }

    @Override
    public void turnLeft() {
        setMovmentSpeed((motorADirection * 100) + 100, (motorBDirection * 100) + 100);
    }

    @Override
    public void turnRight() {
        setMovmentSpeed((motorADirection * 100) + 100, (motorBDirection * 100) + 100);
    }

    /**
     * Ustawienie predkosci na kazdym silniku
     *
     * @param motorA 100 200
     * @param motorB 100 200
     */
    @Override
    public void setMovmentSpeed(int motorA, int motorB) {
        String message = "5 " + Integer.toString(motorA) + " " + Integer.toString(motorB) + "\n";
        try {
            serialPortService.write(message);
        } catch (Exception e) {
            System.err.println("Failed to send motor command: " + e.getMessage());
            e.printStackTrace();
            // Optionally: signal error, retry, or log to file
        }
        //serialPort.writeBytes(message.getBytes(), message.length());
    }

    @Override
    public void setMovmentSpeed() {
        String message = "5 " + Integer.toString(motorAPower) + " " + Integer.toString(motorBPower) + "\n";

        serialPortService.write(message);

        //serialPort.writeBytes(message.getBytes(), message.length());
    }

    /**
     * Ustawienie predkosci na kazdym silniku
     *
     * @return string z portu szeregowego
     */
    @Override
    public String getRobotData() {
        String command = "7\n";
        //int bytesSent = serialPort.writeBytes(command.getBytes(), command.length());

        int bytesSent = serialPortService.read(command.getBytes());

        return Integer.toString(bytesSent);
    }

    /**
     * Zatrzymanie robota na czas delayInt
     *
     * @param delayInt
     */
    @Override
    public void delay(int delayInt) {
        try {
            Thread.sleep(delayInt);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void moveForward() {
        setMovmentSpeed(
                ((motorADirection * 100) + 100) - motorAPower,
                ((motorBDirection * 100) + 100) - motorBPower
        );

        //int test1 = ((motorADirection * 100) + 100) - motorAPower;
        //int test2 = ((motorBDirection * 100) + 100) - motorBPower;
        //System.out.println("A" + test1);
        //System.out.println("B" + test2);
    }

    //lewe koło jedzie do przodu 100%
    @Override
    public void leftWheelForward() {
        //setMovmentSpeed((((motorADirection*100) + 100) * motorAPower)/100,0);
        setMovmentSpeed(((motorADirection * 100) + 100) - motorAPower, 0);
        //int test = ((motorADirection * 100) + 100) - motorAPower;
        //System.out.println("leftwheelforward" + test);
    }

    //prawe koło jedzie do przodu 100%
    @Override
    public void rightWheelForward() {
        //setMovmentSpeed(0,(((motorBDirection*100) + 100) * motorBPower)/100);
        setMovmentSpeed(0, ((motorBDirection * 100) + 100) - motorBPower);
        //int test = ((motorBDirection * 100) + 100) - motorBPower;
        //System.out.println("rightwheelforward" + test);
    }

    @Override
    public void setFlashlightBrightness(int brightness) {
        String message = "8 " + Integer.toString(brightness) + "\n";
        serialPort.writeBytes(message.getBytes(), message.length());
    }

    public void sendCommand(String command) {
        serialPort.writeBytes((command + "\n").getBytes(), 2);
    }

    /**
     * Strategia dzialania robota
     */
    public void readRobotData() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String command = "7\n";
        serialPort.writeBytes(command.getBytes(), command.length());
        delay(1);
        byte[] buffer = new byte[1024];
        int numRead = serialPort.readBytes(buffer, buffer.length);
        if (numRead > 0) {
            String receivedData = new String(buffer, 0, numRead);
            deserializedRobotData = mapper.readValue(receivedData, RobotDataJson.class);
        }

    }

    public void showRobotData() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        byte[] buffer = new byte[1024];
        String command = "7\n";
        String receivedData;
        while (true) {
            serialPort.writeBytes(command.getBytes(), command.length());
            delay(50);
            int numRead = serialPort.readBytes(buffer, buffer.length);
            if (numRead > 0) {
                receivedData = new String(buffer, 0, numRead);
                System.out.println(receivedData);
                deserializedRobotData = mapper.readValue(receivedData, RobotDataJson.class);
            }
            delay(500);
        }
    }


    public void saveDataRobot(ClockService clockService, String NAME_OF_CVS_FILE) throws JsonProcessingException, InterruptedException {
        Thread saveDataHandler1 = new Thread(() -> {

            long startTime;
            final long TEST_DURATION_NS = 30_000_000_000L; // 30 seconds
            ObjectMapper mapper = new ObjectMapper();
            byte[] buffer = new byte[1024];
            String command = "7\n";
            String receivedData;
            int numRead = 0;

            System.out.println("Processing started at: " + new java.util.Date());
            try (FileWriter writer = new FileWriter("python" + File.separator + "data" + File.separator + NAME_OF_CVS_FILE)) {
                writer.append("nanoTime,sensorValue1,sensorValue2,sensorValue3,sensorValue4,sensorValue5,");
                writer.append("xPos,yPos,theta,");
                writer.append("rawAngle1,rawAngle2,");
                writer.append("xGyro,yGyro,zGyro,");
                writer.append("xAccel,yAccel,zAccel,");
                writer.append("Temp");
                writer.append("\n");
                startTime = System.nanoTime();


                while (System.nanoTime() - startTime < TEST_DURATION_NS) {
                    try {
                        serialPortService.write(command);
                        delay(80);
                        numRead = serialPortService.read(buffer);
                    } catch (Exception e) {
                        System.err.println("Serial communication failed: " + e.getMessage());
                        e.printStackTrace();
                        continue;
                    }
                    if (numRead > 0) {
                        receivedData = new String(buffer, 0, numRead);
                        try {
                            deserializedRobotData = mapper.readValue(receivedData, RobotDataJson.class);

                            writer.append(System.nanoTime() + ",");

                            writer.append(deserializedRobotData.getSensorValue1() + ",");
                            writer.append(deserializedRobotData.getSensorValue2() + ",");
                            writer.append(deserializedRobotData.getSensorValue3() + ",");
                            writer.append(deserializedRobotData.getSensorValue4() + ",");
                            writer.append(deserializedRobotData.getSensorValue5() + ",");

                            writer.append(deserializedRobotData.getXPos() + ",");
                            writer.append(deserializedRobotData.getYPos() + ",");
                            writer.append(deserializedRobotData.getTheta() + ",");

                            writer.append(deserializedRobotData.getRawAngle1() + ",");
                            writer.append(deserializedRobotData.getRawAngle2() + ",");

                            writer.append(deserializedRobotData.getXGyro() + ",");
                            writer.append(deserializedRobotData.getYGyro() + ",");
                            writer.append(deserializedRobotData.getZGyro() + ",");

                            writer.append(deserializedRobotData.getXAccel() + ",");
                            writer.append(deserializedRobotData.getYAccel() + ",");
                            writer.append(deserializedRobotData.getZAccel() + ",");

                            writer.append(deserializedRobotData.getTemp() + "");
                            writer.append("\n");
                        } catch (IOException e) {
                            System.out.println("Curapt!");
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Error saving to CSV: " + e.getMessage());
            } finally {
                System.out.println("Processing ended at: " + new java.util.Date());
            }
        });
        saveDataHandler = saveDataHandler1;
        saveDataHandler.start();
    }

    public void join() throws InterruptedException {
        if (saveDataHandler != null) {
            saveDataHandler.join();
        }
    }
}
