package org.example.Experiment;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;

public class Usage {
    public double usedMemoryMB = 0;
    public double usedMemoryGB = 0;
    public double usedMemoryPercentage = 0;
    public double totalMemoryGB = 0;
    public double availableMemoryGB = 0;
    public double cpuLoad = 0;

        public void getUsage() {
            // Create a SystemInfo object to access hardware data
            SystemInfo systemInfo = new SystemInfo();

            // Get CPU information
            CentralProcessor processor = systemInfo.getHardware().getProcessor();

            // Get memory information
            GlobalMemory memory = systemInfo.getHardware().getMemory();

            // Get CPU load (Usage) as a percentage
            cpuLoad = processor.getSystemCpuLoad(1) * 100; // CPU load in percentage
            //System.out.println("CPU Load: " + cpuLoad + "%");

            // Get total and available memory in GB
            totalMemoryGB = memory.getTotal() / (1024.0 * 1024.0 * 1024.0); // Convert bytes to GB
            availableMemoryGB = memory.getAvailable() / (1024.0 * 1024.0 * 1024.0); // Convert bytes to GB
            //System.out.println("Total Memory: " + String.format("%.2f", totalMemoryGB) + " GB");
            //System.out.println("Available Memory: " + String.format("%.2f", availableMemoryGB) + " GB");



            // Get the runtime object associated with the Java application
            Runtime runtime = Runtime.getRuntime();

            // Get total memory (in bytes)
            long totalMemory = runtime.totalMemory();

            // Get free memory (in bytes)
            long freeMemory = runtime.freeMemory();

            // Calculate the used memory by the program
            long usedMemory = totalMemory - freeMemory;

            // Convert bytes to MB and GB for easier readability
            usedMemoryMB = usedMemory / (1024.0 * 1024.0);  // Convert bytes to MB
            usedMemoryGB = usedMemory / (1024.0 * 1024.0 * 1024.0);  // Convert bytes to GB

            usedMemoryPercentage = (usedMemoryGB/availableMemoryGB)*100;

            // Print the memory used by the program
            //System.out.println("Memory Used by Program: " + String.format("%.2f", usedMemoryMB) + " MB" + "or"+ String.format("%.2f", usedMemoryGB) + " GB");

        }
}

