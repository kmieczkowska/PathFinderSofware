package org.example.RobotController;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

class RobotDataJson{
    @JsonCreator
    public RobotDataJson(@JsonProperty("sensorValue1") int sensorValue1,
                         @JsonProperty("sensorValue2") int sensorValue2,
                         @JsonProperty("sensorValue3") int sensorValue3,
                         @JsonProperty("sensorValue4") int sensorValue4,
                         @JsonProperty("sensorValue5") int sensorValue5,

                         @JsonProperty("xPos") float xPos,
                         @JsonProperty("yPos") float yPos,
                         @JsonProperty("theta") float theta,

                         @JsonProperty("rawAngle1") int rawAngle1,
                         @JsonProperty("rawAngle2") int rawAngle2,

                         @JsonProperty("xGyro") float xGyro,
                         @JsonProperty("yGyro") float yGyro,
                         @JsonProperty("zGyro") float zGyro,

                         @JsonProperty("xAccel") float xAccel,
                         @JsonProperty("yAccel") float yAccel,
                         @JsonProperty("zAccel") float zAccel,

                         @JsonProperty("Temp") float Temp)
    {
        this.sensorValue1 = sensorValue1;
        this.sensorValue2 = sensorValue2;
        this.sensorValue3 = sensorValue3;
        this.sensorValue4 = sensorValue4;
        this.sensorValue5 = sensorValue5;
        this.xPos = xPos;
        this.yPos = yPos;
        this.theta = theta;
        this.rawAngle1 = rawAngle1;
        this.rawAngle2 = rawAngle2;
        this.xGyro = xGyro;
        this.yGyro = yGyro;
        this.zGyro = zGyro;
        this.xAccel = xAccel;
        this.yAccel = yAccel;
        this.zAccel = zAccel;
        this.Temp = Temp;
    }

    // czujniki podczerwone - 5
    // daja wartosc zakresu do stweirdzenia koloru czanrego/bialego
    private int sensorValue1;
    private int sensorValue2;
    private int sensorValue3;
    private int sensorValue4;
    private int sensorValue5;

    // aktualna pozycja robota po wyliczeniu
    private float xPos;
    private float yPos;
    private float theta;

    // aktualna pozycja koła 1/2
    private int rawAngle1;
    private int rawAngle2;

    // żyroskop- pozycja robota na świecie
    private float xGyro;
    private float yGyro;
    private float zGyro;

    // akcelerometr - przeciążenia
    private float xAccel;
    private float yAccel;
    private float zAccel;

    // temperatura
    private float Temp;

    public int getSensorValue1() {
        return sensorValue1;
    }

    public int getSensorValue2() {
        return sensorValue2;
    }

    public int getSensorValue3() {
        return sensorValue3;
    }

    public int getSensorValue4() {
        return sensorValue4;
    }

    public int getSensorValue5() {
        return sensorValue5;
    }
    public float getXPos() {
        return xPos;
    }
    public float getYPos() {
        return yPos;
    }
    public float getTheta() {
        return theta;
    }
    public int getRawAngle1() {
        return rawAngle1;
    }
    public int getRawAngle2() {
        return rawAngle2;
    }
    public float getXGyro() {
        return xGyro;
    }
    public float getYGyro() {
        return yGyro;
    }
    public float getZGyro() {
        return zGyro;
    }
    public float getXAccel() {
        return xAccel;
    }
    public float getYAccel() {
        return yAccel;
    }
    public float getZAccel() {
        return zAccel;
    }
    public float getTemp() {
        return Temp;
    }

}