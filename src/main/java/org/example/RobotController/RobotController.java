package org.example.RobotController;


import com.fazecast.jSerialComm.SerialPort;


public class RobotController {
    SerialPort serialPort;

    public RobotController(SerialPort serialPortInit){
         serialPort = serialPortInit;
    }

    private void emergencyStop() {
        serialPort.writeBytes("0\n".getBytes(), 2);
    }

    private void moveForward() {
        serialPort.writeBytes("1\n".getBytes(), 2);
    }

    private void moveReverse(){
        serialPort.writeBytes("2\n".getBytes(), 2);
    }

    private void turnLeft(){
        serialPort.writeBytes("3\n".getBytes(), 2);
    }

    private void turnRight(){
        serialPort.writeBytes("4\n".getBytes(), 2);
    }

    private void setMovmentSpeed(int motorA, int motorB){
        String message = "5 " + Integer.toString(motorA) + " " + Integer.toString(motorB) +"\n";
        serialPort.writeBytes(message.getBytes(), message.length());
    }


    private String getSensorValues() {

        serialPort.writeBytes("7\n".getBytes(), 2);
        return "00000";

    }

    private void delay(int delayInt){
        try {
            Thread.sleep(delayInt);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void strategy_1() {
        while (true) {
            String sensorValues = getSensorValues();
            switch (sensorValues) {
                case "01111":
                    turnLeft();
                    delay(300);
                    emergencyStop();
                    break;
                case "10111":
                    turnLeft();
                    delay(150);
                    emergencyStop();
                    break;

                case "11011":
                    moveForward();
                    delay(200);
                    emergencyStop();
                    break;
                case "11101":
                    turnRight();
                    delay(150);
                    emergencyStop();
                    break;
                case "11110":
                    turnRight();
                    delay(300);
                    emergencyStop();
                    break;
                default:
                    emergencyStop();
                    break;
            }
        }
    }
}
