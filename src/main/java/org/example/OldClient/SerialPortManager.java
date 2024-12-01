package org.example.OldClient;

import com.fazecast.jSerialComm.SerialPort;

public class SerialPortManager {
    private SerialPort serialPort;

    public SerialPortManager(String portName, int baudRate) {
        // Wybór portu szeregowego
        serialPort = SerialPort.getCommPort(portName);
        // Ustawienie parametrów transmisji
        serialPort.setComPortParameters(baudRate, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
    }

    public boolean openPort() {
        if (serialPort.openPort()) {
            System.out.println("Port został otwarty.");
            // Ustaw timeout na odczyt i zapis
            serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 1000, 1000);
            return true;
        } else {
            System.out.println("Nie można otworzyć portu.");
            return false;
        }
    }

    public void writeData(String data) {
        byte[] writeBuffer = data.getBytes();
        serialPort.writeBytes(writeBuffer, writeBuffer.length);
        System.out.println("Wysłano: " + data);
    }

    public String readData() {
        byte[] readBuffer = new byte[1024];
        int numRead = serialPort.readBytes(readBuffer, readBuffer.length);
        String receivedData = new String(readBuffer, 0, numRead);
        System.out.println("Odczytano: " + receivedData);
        return receivedData;
    }

    public SerialPort getSerialPort() {
        return serialPort;
    }
}
