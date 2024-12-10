package org.example.RobotController;

/**
 * Interfejs do obsługi wykrycia portu szeregowego
 */
public interface IRobotController {

    public void setMotorADirection(int direction);
    public void setMotorBDirection(int direction);

    public void setMotorADirectionForward();
    public void setMotorADirectionBackward();

    public void setMotorBDirectionForward();
    public void setMotorBDirectionBackward();

    public void emergencyStop();

    public void setMovmentSpeed(int motorA, int motorB);

    public void moveForward();

    public void moveReverse();

    public void turnLeft();

    public void turnRight();

    public void strategy_1();

    public String getSensorValues();

    public void sendCommand(String command);

    public void delay(int delayInt);

    public void leftWheelForward();
    public void rightWheelForward();



}
