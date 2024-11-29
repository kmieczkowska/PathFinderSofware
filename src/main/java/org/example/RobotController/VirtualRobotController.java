package org.example.RobotController;

public class VirtualRobotController implements IRobotController {
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
    public void strategy_1() {
        System.out.println("Strategy 1");
    }

    @Override
    public String getSensorValues() {
        return "10101";
    }

    @Override
    public void sendCommand(String command) {
        System.out.println("Sending command: " + command);
    }
}
