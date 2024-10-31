package org.example.Client;

import com.fazecast.jSerialComm.SerialPort;
import org.example.SerialPort.SerialPortManager;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientMain {

    public static void main(String[] args) {
        SerialPortManager portManager = new SerialPortManager("COM3", 9600);
        SerialPort serialPort = SerialPort.getCommPort("COM3");

        Socket socket = null;
        InputStreamReader inputStreamReader = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;

        try {
            socket = new Socket("localhost", 1234);  // Connect to server at localhost on port 1234
            System.out.println("Connected to the server.");

            inputStreamReader = new InputStreamReader(socket.getInputStream());
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());

            bufferedReader = new BufferedReader(inputStreamReader);
            bufferedWriter = new BufferedWriter(outputStreamWriter);

            Scanner scanner = new Scanner(System.in);

            // Create a new thread to handle server messages
            BufferedReader finalBufferedReader = bufferedReader;
            Thread serverHandler = new Thread(() -> {
                try {
                    String msgFromServer;
                    while ((msgFromServer = finalBufferedReader.readLine()) != null) {
                        System.out.println("Server: " + msgFromServer);
                        if (msgFromServer.equalsIgnoreCase("Exit")) {
                            System.out.println("Server disconnected.");
                            break;
                        }
                        if (msgFromServer.equalsIgnoreCase("OSP")) {
                            if (serialPort.openPort()) {

                                String message = "tu wiadomosc z przetworzonego obrazu - serwer";
                                byte[] messageBytes = message.getBytes(); // konwersja wiadomości na tablicę bajtów

                                // Wysłanie wiadomości
                                int bytesWritten = serialPort.writeBytes(messageBytes, messageBytes.length);
                                if (bytesWritten > 0) {
                                    System.out.println("Message sent: " + message);
                                } else {
                                    System.out.println("Wysłanie wiadomości nie powiodło się.");
                                }
                            } else {
                                System.out.println("Cant open serial port.");
                                return;
                            }
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            serverHandler.start();  // Start the server message handling thread

            // Client can send messages to the server
            while (true) {
                System.out.print("You (Client): ");
                String msgToSend = scanner.nextLine();

                bufferedWriter.write(msgToSend);
                bufferedWriter.newLine();  // Ensure proper line break
                bufferedWriter.flush();    // Flush to send the message immediately

                if (msgToSend.equalsIgnoreCase("BYE")) {
                    System.out.println("Connection closed by client.");
                    break;
                }
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null) socket.close();
                if (inputStreamReader != null) inputStreamReader.close();
                if (outputStreamWriter != null) outputStreamWriter.close();
                if (bufferedReader != null) bufferedReader.close();
                if (bufferedWriter != null) bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public SerialPort getSerialPort(){
         SerialPort serialPort = SerialPort.getCommPort("COM3");
         return serialPort;
    }
}

