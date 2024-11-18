package org.example.Examples;

import com.fazecast.jSerialComm.SerialPort;

public class Tests {

    public static void delay(int delayInt){
        try {
            Thread.sleep(delayInt);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        SerialPort serialPort;
        serialPort = SerialPort.getCommPort("COM3");
        serialPort.setComPortParameters(115200, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);
        serialPort.openPort();
        serialPort.writeBytes("1\n".getBytes(), 2);
        delay(1000);
        serialPort.writeBytes("0\n".getBytes(), 2);
        delay(1000);
        serialPort.closePort();
    }

}
