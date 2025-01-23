package org.example.Tests;

import com.fazecast.jSerialComm.SerialPort;
import org.example.Configuration.ConfigurationLoader;

public class Tests {

    public static void main(String[] args) {
        readingConfigurationTest();
    }

    public static void readingConfigurationTest() {
        ConfigurationLoader config = new ConfigurationLoader("configuration.properties");
        System.out.println("Database URL: " + config.getSerialMode());

    }
}
