package de.materna.ar.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bmolnar on 07.09.2017.
 */

public class Vehicle {

    private String introShort;
    private String name;
    private String topSpeed;
    private String power;
    private String financing;
    private String acceleration;
    private String videoTitle;
    private String x;
    private String y;
    private String z;

    public void setIntroShort(String intro) {
        this.introShort = intro;
    }

    public void setName(String n) {
        this.name = n;
    }

    public void setTopSpeed(int s) {
        this.topSpeed = s + " km/h";
    }

    public void setPower(int p) {
        this.power = p + " PS";
    }

    public void setFinancing(int f) {
        this.financing = "ab " + f + "Euro/Monat";
    }

    public void setAcceleration(float a) {
        this.acceleration = a + " Sek. 0-100";
    }

    public void setVideoTitle(String t){this.videoTitle = t;}

    public void setX(String x) {
        this.x = x;
    }

    public void setY(String y) {
        this.y = y;
    }

    public void setZ(String z) {
        this.z = z;
    }

    public String getIntroShort() {
        return introShort;
    }

    public String getName() {
        return name;
    }

    public String getTopSpeed() {
        return topSpeed;
    }

    public String getPower() {
        return power;
    }

    public String getFinancing() {
        return financing;
    }

    public String getAcceleration() {
        return acceleration;
    }

    public String getVideoTitle(){ return videoTitle; }

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }

    public String getZ() {
        return z;
    }



    }

