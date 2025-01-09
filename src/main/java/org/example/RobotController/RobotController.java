package org.example.RobotController;


import com.fazecast.jSerialComm.SerialPort;

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
    public String getSensorValues() {

        String command = "7\n";
        int bytesSent = serialPort.writeBytes(command.getBytes(), command.length());
        return String.valueOf(bytesSent);
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
