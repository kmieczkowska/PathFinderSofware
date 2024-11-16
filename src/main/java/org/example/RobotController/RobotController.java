package org.example.RobotController;


import com.fazecast.jSerialComm.SerialPort;

/**
 * Klasa stosowana do sterowania robotem
 * @param
 * @return
 */
public class RobotController {
    SerialPort serialPort;

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

    /**
     * Ustawienie predkosci na kazdym silniku
     * @param motorA
     * @param motorB
     */
    private void setMovmentSpeed(int motorA, int motorB){
        String message = "5 " + Integer.toString(motorA) + " " + Integer.toString(motorB) +"\n";
        serialPort.writeBytes(message.getBytes(), message.length());
    }


    /**
     * Ustawienie predkosci na kazdym silniku
     * @return string z portu szeregowego
     */
    private String getSensorValues() {

        String command = "7\n";
        int bytesSent = serialPort.writeBytes(command.getBytes(), command.length());
        return String.valueOf(bytesSent);
    }

    /**
     * Zatrzymanie robota na czas delayInt
     * @param delayInt
     */
    private void delay(int delayInt){
        try {
            Thread.sleep(delayInt);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Strategia dzialania robota
     * */
    public void strategy_1() {
        while (true) {
            System.out.println("tick.");
            String sensorValues = getSensorValues();
            delay(1000);
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
