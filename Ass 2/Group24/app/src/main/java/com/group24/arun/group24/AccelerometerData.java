package com.group24.arun.group24;

import java.util.Date;

/**
 * Created by Arun Subramanian on 06-03-2017.
 */

public class AccelerometerData {
    String timeStamp;
    double xVal,yVal,zVal;

    public AccelerometerData(String timeStamp, double xVal, double yVal, double zVal)
    {
        this.timeStamp = timeStamp;
        this.xVal = xVal;
        this.yVal = yVal;
        this.zVal = zVal;
    }
    public double getxVal()
    {
        return xVal;
    }

    public double getyVal()
    {
        return yVal;
    }

    public double getzVal()
    {
        return zVal;
    }

    public String getTimeStamp(){
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp){
        this.timeStamp = timeStamp;
    }

    public void setxVal(double xVal){
        this.xVal = xVal;
    }

    public void setyVal(double yVal){
        this.yVal = yVal;
    }

    public void setzVal(double zVal){
        this.zVal = zVal;
    }
}
