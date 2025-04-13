package org.example.RobotController;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fazecast.jSerialComm.SerialPort;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

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
    
    SerialPort serialPort;

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

    public void strategy_1() throws JsonProcessingException {
        readRobotData();
    }

    @Override
    public void emergencyStop() {
        setMovmentSpeed(0,0);
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

    @Override
    public void setMovmentSpeed(){
        String message = "5 " + Integer.toString(motorAPower) + " " + Integer.toString(motorBPower) +"\n";
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

    @Override
    public void moveForward() {
        setMovmentSpeed(
                ((motorADirection*100) + 100) - motorAPower,
                ((motorBDirection *100)+ 100) - motorBPower
        );
    }
    
    //lewe koło jedzie do przodu 100%
    @Override
    public void leftWheelForward() {
//        setMovmentSpeed((((motorADirection*100) + 100) * motorAPower)/100,0);
        setMovmentSpeed(((motorADirection*100) + 100) - motorAPower ,0);
    }
    //prawe koło jedzie do przodu 100%
    @Override
    public void rightWheelForward() {
//        setMovmentSpeed(0,(((motorBDirection*100) + 100) * motorBPower)/100);
        setMovmentSpeed(0, ((motorBDirection * 100) + 100) - motorBPower);
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
        delay(1);
        byte[] buffer = new byte[1024];
        int numRead = serialPort.readBytes(buffer, buffer.length);
        if (numRead > 0) {
            String receivedData = new String(buffer, 0, numRead);
            deserializedRobotData = mapper.readValue(receivedData,RobotDataJson.class);
        }

    }

    public void showRobotData() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        byte[] buffer = new byte[1024];
        String command = "7\n";
        String receivedData;
        while(true){
            serialPort.writeBytes(command.getBytes(), command.length());
            delay(50);
            int numRead = serialPort.readBytes(buffer, buffer.length);
            if (numRead > 0) {
                receivedData = new String(buffer, 0, numRead);
                System.out.println(receivedData);
                deserializedRobotData = mapper.readValue(receivedData,RobotDataJson.class);
            }
            delay(500);
        }
    }


    public void saveDataRobot() throws JsonProcessingException {

        long startTime;
        final long TEST_DURATION_NS = 30_000_000_000L; // 30 seconds
        ObjectMapper mapper = new ObjectMapper();
        byte[] buffer = new byte[1024];
        String command = "7\n";
        String receivedData;
        int numRead = 0;

        System.out.println("Processing started at: " + new java.util.Date());
        try (FileWriter writer = new FileWriter("python" + File.separator + "data" + File.separator+"RobotData2.csv" )) {
            writer.append("nanoTime,sensorValue1,sensorValue2,sensorValue3,sensorValue4,sensorValue5,");
            writer.append("xPos,yPos,theta,");
            writer.append("rawAngle1,rawAngle2,");
            writer.append("xGyro,yGyro,zGyro,");
            writer.append("xAccel,yAccel,zAccel,");
            writer.append("Temp");
            writer.append("\n");
            startTime = System.nanoTime();
            while(System.nanoTime() - startTime < TEST_DURATION_NS){
                serialPort.writeBytes(command.getBytes(), command.length());
                delay(50);
                numRead = serialPort.readBytes(buffer, buffer.length);
                if (numRead > 0) {
                    receivedData = new String(buffer, 0, numRead);
                    try{
                        deserializedRobotData = mapper.readValue(receivedData,RobotDataJson.class);

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
                    }catch(IOException e){
                        System.out.println("Curapt!");
                    }

                }

            }
        } catch(IOException e){
            System.err.println("Error saving to CSV: " + e.getMessage());
        }finally {
            System.out.println("Processing ended at: " + new java.util.Date());
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
                             @JsonProperty("rawAngle2") int rawAngle2,

                             @JsonProperty("xGyro") float xGyro,
                             @JsonProperty("yGyro") float yGyro,
                             @JsonProperty("zGyro") float zGyro,

                             @JsonProperty("xAccel") float xAccel,
                             @JsonProperty("yAccel") float yAccel,
                             @JsonProperty("zAccel") float zAccel,

                             @JsonProperty("Temp") float Temp)
        {
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
            this.xGyro = xGyro;
            this.yGyro = yGyro;
            this.zGyro = zGyro;
            this.xAccel = xAccel;
            this.yAccel = yAccel;
            this.zAccel = zAccel;
            this.Temp = Temp;
        }

        // czujniki podczerwone - 5
        // daja wartosc zakresu do stweirdzenia koloru czanrego/bialego
        private int sensorValue1;
        private int sensorValue2;
        private int sensorValue3;
        private int sensorValue4;
        private int sensorValue5;

        // aktualna pozycja robota po wyliczeniu
        private float xPos;
        private float yPos;
        private float theta;

        // aktualna pozycja koła 1/2
        private int rawAngle1;
        private int rawAngle2;

        // żyroskop- pozycja robota na świecie
        private float xGyro;
        private float yGyro;
        private float zGyro;

        // akcelerometr - przeciążenia
        private float xAccel;
        private float yAccel;
        private float zAccel;

        // temperatura
        private float Temp;

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
        public float getXGyro() {
            return xGyro;
        }
        public float getYGyro() {
            return yGyro;
        }
        public float getZGyro() {
            return zGyro;
        }
        public float getXAccel() {
            return xAccel;
        }
        public float getYAccel() {
            return yAccel;
        }
        public float getZAccel() {
            return zAccel;
        }
        public float getTemp() {
            return Temp;
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
