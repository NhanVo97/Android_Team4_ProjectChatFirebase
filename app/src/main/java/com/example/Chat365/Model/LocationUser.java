package com.example.Chat365.Model;

import java.io.Serializable;

public class LocationUser implements Serializable {
    private String address;
    private String stateName;
    private String countryName;
    private double latitude;
    private double longitude;
    private boolean isHide;

    public LocationUser() {
    }

    public LocationUser(String address, String stateName, String countryName, double latitude,
                        double longitude,boolean isHide) {
        this.address = address;
        this.stateName = stateName;
        this.countryName = countryName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isHide = isHide;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isHide() {
        return isHide;
    }
    public void setHide(boolean hide) {
        isHide = hide;
    }
}
