package org.example.Services;

import com.fazecast.jSerialComm.SerialPort;

public class SerialPortService {
    private final SerialPort serialPort;

    public SerialPortService(SerialPort serialPort) {
        this.serialPort = serialPort;
    }

    public synchronized void write(String data) {
        System.out.println(data);
        serialPort.writeBytes(data.getBytes(), data.length());
    }

    public synchronized int read(byte[] buffer) {
        return serialPort.readBytes(buffer, buffer.length);
    }
}