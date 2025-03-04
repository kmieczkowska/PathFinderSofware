package org.example.RobotController;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fazecast.jSerialComm.SerialPort;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * Sterowanie robotem
 */
public class RobotController implements IRobotController {
    
    SerialPort serialPort;

    private int motorADirection = 1;
    private int motorBDirection = 1;

    @Override
    public void setMotorADirection(int direction) {
        motorADirection = direction;
    }

    @Override
    public void setMotorBDirection(int direction) {
        motorBDirection = direction;
    }

    @Override
    public void setMotorADirectionForward() {motorADirection = 0;}
    @Override
    public void setMotorADirectionBackward() {motorADirection = 1;}
    @Override
    public void setMotorBDirectionForward() {motorBDirection = 0;}
    @Override
    public void setMotorBDirectionBackward() {motorBDirection = 1;}

    /**
     * RobotController
     * Inicjowanie serialPort
     * @param serialPortInit
     */
    public RobotController(SerialPort serialPortInit){
        serialPort = serialPortInit;

    }

    /**
     * emergencyStop
     * wysyla znak 0 na port szeregowy (w stm zatrzymanie robota)
     */
//    @Override
//    public void emergencyStop() {
//        serialPort.writeBytes("0\n".getBytes(), 2);
//    }
//    @Override
//    public void moveForward() {
//        serialPort.writeBytes("1\n".getBytes(), 2);
//    }
//    @Override
//    public void moveReverse(){
//        serialPort.writeBytes("2\n".getBytes(), 2);
//    }
//    @Override
//    public void turnLeft(){
//        serialPort.writeBytes("3\n".getBytes(), 2);
//    }
//    @Override
//    public void turnRight(){
//        serialPort.writeBytes("4\n".getBytes(), 2);
//    }

    @Override
    public void emergencyStop() {
        setMovmentSpeed(0,0);
    }
    @Override
    public void moveForward() {
        setMovmentSpeed((motorADirection*100) + 100,(motorBDirection *100)+ 100);
    }
    @Override
    public void moveReverse(){
        setMovmentSpeed((motorADirection*100) + 100,(motorBDirection *100)+ 100);
    }
    @Override
    public void turnLeft(){
        setMovmentSpeed((motorADirection*100) + 100,(motorBDirection *100)+ 100);
    }
    @Override
    public void turnRight(){
        setMovmentSpeed((motorADirection*100) + 100,(motorBDirection *100)+ 100);
    }

    /**
     * Ustawienie predkosci na kazdym silniku
     * @param motorA 100 200
     * @param motorB 100 200
     */
    @Override
    public void setMovmentSpeed(int motorA, int motorB){
        String message = "5 " + Integer.toString(motorA) + " " + Integer.toString(motorB) +"\n";
        serialPort.writeBytes(message.getBytes(), message.length());
    }


    /**
     * Ustawienie predkosci na kazdym silniku
     * @return string z portu szeregowego
     */
    @Override
    public String getRobotData() {

        String command = "7\n";
        int bytesSent = serialPort.writeBytes(command.getBytes(), command.length());
        return Integer.toString(bytesSent);

    }

    /**
     * Zatrzymanie robota na czas delayInt
     * @param delayInt
     */
    @Override
    public void delay(int delayInt){
        try {
            Thread.sleep(delayInt);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    //lewe koło jedzie do przodu 100%
    @Override
    public void leftWheelForward() {
        setMovmentSpeed((motorADirection*100) + 100,0);
    }
    //prawe koło jedzie do przodu 100%
    @Override
    public void rightWheelForward() {
        setMovmentSpeed(0,(motorBDirection*100) + 100);
    }

    @Override
    public void setFlashlightBrightness(int brightness) {
        String message = "8 " + Integer.toString(brightness) +"\n";
        serialPort.writeBytes(message.getBytes(), message.length());
    }

    public void sendCommand(String command){
        serialPort.writeBytes((command + "\n").getBytes(), 2);
    }


    /**
     * Strategia dzialania robota
     * */
    public void readRobotData() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String command = "7\n";
        serialPort.writeBytes(command.getBytes(), command.length());
        delay(500);
        byte[] buffer = new byte[1024];
        int numRead = serialPort.readBytes(buffer, buffer.length);
        if (numRead > 0) {
            String receivedData = new String(buffer, 0, numRead);
            System.out.println("Received: " + receivedData);
            RobotDataJson deserializedRobotData = mapper.readValue(receivedData,RobotDataJson.class);
        }

    }
    static class RobotDataJson{
        @JsonCreator
        public RobotDataJson(@JsonProperty("sensorValue1") int sensorValue1,
                             @JsonProperty("sensorValue2") int sensorValue2,
                             @JsonProperty("sensorValue3") int sensorValue3,
                             @JsonProperty("sensorValue4") int sensorValue4,
                             @JsonProperty("sensorValue5") int sensorValue5,
                             @JsonProperty("xPos") float xPos,
                             @JsonProperty("yPos") float yPos,
                             @JsonProperty("theta") float theta,
                             @JsonProperty("rawAngle1") int rawAngle1,
                             @JsonProperty("rawAngle2") int rawAngle2) {
            this.sensorValue1 = sensorValue1;
            this.sensorValue2 = sensorValue2;
            this.sensorValue3 = sensorValue3;
            this.sensorValue4 = sensorValue4;
            this.sensorValue5 = sensorValue5;
            this.xPos = xPos;
            this.yPos = yPos;
            this.theta = theta;
            this.rawAngle1 = rawAngle1;
            this.rawAngle2 = rawAngle2;
        }

        private int sensorValue1;
        private int sensorValue2;
        private int sensorValue3;
        private int sensorValue4;
        private int sensorValue5;

        private float xPos;
        private float yPos;
        private float theta;

        private int rawAngle1;
        private int rawAngle2;


        public int getSensorValue1() {
            return sensorValue1;
        }

        public int getSensorValue2() {
            return sensorValue2;
        }

        public int getSensorValue3() {
            return sensorValue3;
        }

        public int getSensorValue4() {
            return sensorValue4;
        }

        public int getSensorValue5() {
            return sensorValue5;
        }
        public float getXPos() {
            return xPos;
        }
        public float getYPos() {
            return yPos;
        }
        public float getTheta() {
            return theta;
        }
        public int getRawAngle1() {
            return rawAngle1;
        }
        public int getRawAngle2() {
            return rawAngle2;
        }
    }


            // Buffer for received data
//            while (true) {
//                bytesSent = serialPort.writeBytes(command.getBytes(), command.length());
//
//
//                int bytesRead = 0;
//                try {
//                    bytesRead = in.read(buffer);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//                if (bytesRead > 0) {
//                    Robot.RobotData pos = Robot.RobotData.parseFrom(buffer);
//
//                    // Print received values
//                    System.out.println("Sensor 1: " + pos.getSensorValue1());
//                    System.out.println("Sensor 2: " + pos.getSensorValue2());
//                    System.out.println("Sensor 3: " + pos.getSensorValue3());
//                    System.out.println("Sensor 4: " + pos.getSensorValue4());
//                    System.out.println("Sensor 5: " + pos.getSensorValue5());
//                    System.out.println("Raw Angle 1: " + pos.getRawAngle1());
//                    System.out.println("Raw Angle 2: " + pos.getRawAngle2());
//                    System.out.println("X: " + pos.getXPos() + ", Y: " + pos.getYPos() + ", Theta: " + pos.getTheta());
//                    System.out.println("------------------------");
//                    delay(1000);
//                }
//            }



//            String sensorValues = getRobotData(); // wczytane jako bity profotbuff

//            delay(1000);
//            switch (sensorValues) {
//                case "01111":
//                    turnLeft();
//                    delay(300);
//                    emergencyStop();
//                    break;
//                case "10111":
//                    turnLeft();
//                    delay(150);
//                    emergencyStop();
//                    break;
//
//                case "11011":
//                    moveForward();
//                    delay(200);
//                    emergencyStop();
//                    break;
//                case "11101":
//                    turnRight();
//                    delay(150);
//                    emergencyStop();
//                    break;
//                case "11110":
//                    turnRight();
//                    delay(300);
//                    emergencyStop();
//                    break;
//                default:
//                    emergencyStop();
//                    break;
//            }
    public void debug(){

        System.out.println("[DEBUG] send move forward.");
        moveForward();
        delay(3000);

        System.out.println("[DEBUG] send emergency stop.");
        emergencyStop();
        delay(3000);

        for(int i =0;i<3;i++){
            System.out.println("[DEBUG] send get sensor values.");
            System.out.println("[DEBUG] sensor values: "+getRobotData());
            delay(1000);
        }
        delay(5000);
    }
}
