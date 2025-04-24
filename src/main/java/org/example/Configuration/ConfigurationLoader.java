package org.example.Configuration;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Wczytanie pliku konfiguracyjnego oraz dostep do wartości z pliku
 */
public class ConfigurationLoader {

    private Properties properties;

    public ConfigurationLoader(String configFilePath) {
        properties = new Properties();
        try (InputStream input = new FileInputStream(configFilePath)) {
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // Getter methods to retrieve configuration values
    public String getSerialMode() { return properties.getProperty("SERIAL_LOCAL_MODE"); }

    public String getRobotStretegy() { return properties.getProperty("ROBOT_STRATEGY"); }

    public String getMotorADirection() { return properties.getProperty("MOTOR_A_DIRECTION"); }
    public String getMotorBDirection() { return properties.getProperty("MOTOR_B_DIRECTION"); }

    public String getMotorAPower() { return properties.getProperty("MOTOR_A_POWER"); }
    public String getMotorBPower() { return properties.getProperty("MOTOR_B_POWER"); }

    public String getNameOfCvsFile() { return properties.getProperty("NAME_OF_CVS_FILE"); }
    public String getRunningDuration() { return properties.getProperty("RUNNING_DURATION"); }
    public String getSavingProperty() {return  properties.getProperty("ENABLE_SAVING");}

}
