package org.example.Services;

import com.fazecast.jSerialComm.SerialPort;

public class SerialPortService {
    private SerialPort serialPort;
    private final String portDescriptor; // Needed to reopen port
    private final int baudRate;

    public SerialPortService(SerialPort serialPort) {
        this.serialPort = serialPort;
        this.portDescriptor = serialPort.getSystemPortName(); // like "COM3" or "/dev/ttyUSB0"
        this.baudRate = serialPort.getBaudRate();
    }

    public synchronized void write(String data) {
        try {
            if (!serialPort.isOpen()) {
                System.err.println("⚠️ Serial port closed before write! Attempting to reopen...");
                tryReopenPort();
            }

            System.out.println("→ " + data.trim());
            serialPort.writeBytes(data.getBytes(), data.length());
        } catch (Exception e) {
            System.err.println("❌ Failed to write to serial port: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public synchronized int read(byte[] buffer) {
        try {
            if (!serialPort.isOpen()) {
                System.err.println("⚠️ Serial port closed before read! Attempting to reopen...");
                tryReopenPort();
            }

            return serialPort.readBytes(buffer, buffer.length);
        } catch (Exception e) {
            System.err.println("❌ Failed to read from serial port: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    private void tryReopenPort() {
        // Attempt to safely reopen the serial port
        try {
            serialPort = SerialPort.getCommPort(portDescriptor);
            serialPort.setBaudRate(baudRate);
            serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 1000, 0);

            if (serialPort.openPort()) {
                System.out.println("✅ Successfully reopened serial port: " + portDescriptor);
            } else {
                System.err.println("❌ Failed to reopen serial port: " + portDescriptor);
            }
        } catch (Exception e) {
            System.err.println("❌ Error while trying to reopen port: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public synchronized void close() {
        if (serialPort != null) {
            serialPort.closePort();
        }
    }
}
