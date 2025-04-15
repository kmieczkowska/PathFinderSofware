package org.example.RobotController;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.Services.ClockService;

/**
 * W przypadku gdy nie wykryto portu szeregowego - obsługa konsoli
 */
public class VirtualRobotController implements IRobotController {

    private int motorADirection = 1;
    private int motorBDirection = 1;

    @Override
    public void join() throws InterruptedException {

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
    public void setMotorBPower(int power) {

    }

    @Override
    public void setMotorAPower(int power) {

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
    public void moveForward() {}

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
//        System.out.println("Setting movement speed");
    }

    @Override
    public void setMovmentSpeed() {

    }

    @Override
    public void readRobotData() {
        System.out.println("Strategy 1");
    }

    @Override
    public void showRobotData() throws JsonProcessingException {}

    @Override
    public void strategy_1() throws JsonProcessingException {}

    @Override
    public void saveDataRobot(ClockService clockService, String NAME_OF_CVS_FILE) throws JsonProcessingException {}
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
    public void leftWheelForward() {}

    @Override
    public void rightWheelForward() {}

    @Override
    public void setFlashlightBrightness(int brightness) {
        System.out.println("Setting flashlight brightness");
    }
}