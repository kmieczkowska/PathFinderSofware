package org.example.ServerCommunication;

import org.example.RobotController.IRobotController;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class ServerCommunication {

    private IRobotController robotController;
    private InputStream inputStream;
    private OutputStream outputStream;

    public ServerCommunication(IRobotController _robotController, InputStream _inputStream, OutputStream _outputStream) {

        robotController = _robotController;
        outputStream = _outputStream;
        inputStream = _inputStream;
    }

    public void start(){
        Thread responseHandler = new Thread(() -> {
            while (true) {
                try {
                    ClientPackageClass.ClientPackage clientPackage = receiveClientPackage();
                    //imageProcesor.setTreshold(clientPackage.getTreshold());
                    robotController.setFlashlightBrightness(clientPackage.getFlashlightBrightness());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
//                String clientResponse = readClientResponse(inputStream);
//                if (clientResponse != null) {
//                    if (clientResponse.equals("manual")) imageProcesor.setStop();
//                    else if (clientResponse.equals("server")) imageProcesor.setStart();
//                }
            }
        });
        responseHandler.start();
    }

    public ClientPackageClass.ClientPackage receiveClientPackage() throws IOException {
        byte[] sizeBuffer = new byte[4];
        if (inputStream.read(sizeBuffer) != 4) {
            throw new IOException("Failed to read message size.");
        }
        int messageSize = ByteBuffer.wrap(sizeBuffer).getInt();

        // Read the actual message
        byte[] messageBuffer = new byte[messageSize];
        int bytesRead = 0;
        while (bytesRead < messageSize) {
            int result = inputStream.read(messageBuffer, bytesRead, messageSize - bytesRead);
            if (result == -1) {
                throw new IOException("Stream closed before reading the full message.");
            }
            bytesRead += result;
        }
        return ClientPackageClass.ClientPackage.parseFrom(messageBuffer);
    }
    private String readClientResponse(InputStream inputStream) {
        try {
            // Read the size of the response (4 bytes)
            byte[] sizeBuffer = new byte[4];
            if (inputStream.read(sizeBuffer) != 4) return null;
            int responseSize = ByteBuffer.wrap(sizeBuffer).getInt();

            // Read the actual response
            byte[] responseBuffer = new byte[responseSize];
            int bytesRead = 0;
            while (bytesRead < responseSize) {
                int result = inputStream.read(responseBuffer, bytesRead, responseSize - bytesRead);
                if (result == -1) break;
                bytesRead += result;
            }

            return new String(responseBuffer, 0, bytesRead);
        } catch (Exception e) {
            System.err.println("Error reading client response:" + e.getMessage());
            return null;
        }
    }
    private void invokeSetMovementSpeed(String response) {
        try {
            // Split the response by commas (expected format: "motorA,motorB,delay")
            String[] parts = response.split(",");
            if (parts.length != 3) {
                System.err.println("Invalid response format:" + response);
                return;
            }

            int motorA = Integer.parseInt(parts[0].trim());
            int motorB = Integer.parseInt(parts[1].trim());
            int delay = Integer.parseInt(parts[2].trim());

            // Call the robot controller method
            setSpeed(motorA, motorB, delay);

            System.out.println("Set movement speed: motorA=" + motorA + ", motorB=" + motorB + ", delay=" + delay);
        } catch (Exception e) {
            System.err.println("Error parsing response or invoking setMovementSpeed: " + e.getMessage());
        }
    }
    private void setSpeed(int motorA, int motorB, int delay){
        robotController.setMovmentSpeed(motorA,motorB);
        robotController.delay(delay);
        robotController.setMovmentSpeed(0,0);
    }
}
