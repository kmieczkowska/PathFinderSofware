package org.example.RobotController;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * W przypadku gdy nie wykryto portu szeregowego - obsługa konsoli
 */
public class VirtualRobotController implements IRobotController {

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
    public void setMotorADirectionForward() {motorADirection = 1;}
    @Override
    public void setMotorADirectionBackward() {motorADirection = -1;}
    @Override
    public void setMotorBDirectionForward() {motorBDirection = 1;}
    @Override
    public void setMotorBDirectionBackward() {motorBDirection = -1;}

    @Override
    public void emergencyStop() {
        System.out.println("emergency stop");
    }

    @Override
    public void moveForward() {
        System.out.println("Moving forward");
    }

    @Override
    public void moveReverse() {
        System.out.println("Moving reverse");
    }

    @Override
    public void turnLeft() {
        System.out.println("Turning left");
    }

    @Override
    public void turnRight() {
        System.out.println("Turning right");
    }

    @Override
    public void setMovmentSpeed(int motorA, int motorB) {
        System.out.println("Setting movement speed");
    }

    @Override
    public void readRobotData() {
        System.out.println("Strategy 1");
    }

    @Override
    public void showRobotData() throws JsonProcessingException {

    }

    @Override
    public void strategy_1() throws JsonProcessingException {

    }

    @Override
    public void saveDataRobot() throws JsonProcessingException {

    }

    @Override
    public String getRobotData() {
        return "10101";
    }

    @Override
    public void sendCommand(String command) {
        System.out.println("Sending command: " + command);
    }

    @Override
    public void delay(int delayInt) {
        try {
            Thread.sleep(delayInt);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void leftWheelForward() {
        System.out.println("Left wheel forward");
    }

    @Override
    public void rightWheelForward() {
        System.out.println("Right wheel forward");
    }

    @Override
    public void setFlashlightBrightness(int brightness) {
        System.out.println("Setting flashlight brightness");
    }
}