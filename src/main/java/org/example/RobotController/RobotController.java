package org.example.RobotController;


import com.fazecast.jSerialComm.SerialPort;

/**
 * Sterowanie robotem
 */
public class RobotController implements IRobotController {
    
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
    @Override
    public void emergencyStop() {
        serialPort.writeBytes("0\n".getBytes(), 2);
    }
    @Override
    public void moveForward() {
        serialPort.writeBytes("1\n".getBytes(), 2);
    }
    @Override
    public void moveReverse(){
        serialPort.writeBytes("2\n".getBytes(), 2);
    }
    @Override
    public void turnLeft(){
        serialPort.writeBytes("3\n".getBytes(), 2);
    }
    @Override
    public void turnRight(){
        serialPort.writeBytes("4\n".getBytes(), 2);
    }

    /**
     * Ustawienie predkosci na kazdym silniku
     * @param motorA
     * @param motorB
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
    public String getSensorValues() {

        String command = "7\n";
        int bytesSent = serialPort.writeBytes(command.getBytes(), command.length());
        return String.valueOf(bytesSent);
    }

    /**
     * Zatrzymanie robota na czas delayInt
     * @param delayInt
     */
    public void delay(int delayInt){
        try {
            Thread.sleep(delayInt);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void sendCommand(String command){
        serialPort.writeBytes((command + "\n").getBytes(), 2);
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
    public void debug(){

        System.out.println("[DEBUG] send move forward.");
        moveForward();
        delay(3000);

        System.out.println("[DEBUG] send emergency stop.");
        emergencyStop();
        delay(3000);



        for(int i =0;i<3;i++){
            System.out.println("[DEBUG] send get sensor values.");
            System.out.println("[DEBUG] sensor values: "+getSensorValues());
            delay(1000);
        }
        delay(5000);
    }
}
