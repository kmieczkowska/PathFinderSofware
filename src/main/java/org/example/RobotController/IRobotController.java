package org.example.RobotController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.protobuf.InvalidProtocolBufferException;
import org.example.Services.ClockService;

import java.io.IOException;

/**
 * Interfejs do wyboruy czy jesteśmy w stanie sterować robotem,
 * czy wolimy tylko wypisywac co by sie dzialo z robotem do konsli.
 */
public interface IRobotController {

    public void setMotorADirection(int direction);
    public void setMotorBDirection(int direction);
    public void setMotorBPower(int power);
    public void setMotorAPower(int power);

    public void setMotorADirectionForward();
    public void setMotorADirectionBackward();

    public void setMotorBDirectionForward();
    public void setMotorBDirectionBackward();

    public void emergencyStop();

    public void setMovmentSpeed(int motorA, int motorB);
    public void setMovmentSpeed();

    public void moveForward();

    public void moveReverse();

    public void turnLeft();

    public void turnRight();

    public String getRobotData();

    public void sendCommand(String command);

    public void delay(int delayInt);

    public void leftWheelForward();

    public void rightWheelForward();

    public void setFlashlightBrightness(int brightness);

    public void readRobotData() throws JsonProcessingException;

    public void showRobotData() throws JsonProcessingException;

    public void strategy_1() throws JsonProcessingException;

    public void saveDataRobot(ClockService clockService,String NAME_OF_CVS_FILE) throws JsonProcessingException, InterruptedException;


}
