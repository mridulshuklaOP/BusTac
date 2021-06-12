package com.example.driverapp;

public class LocationModel {
    private Double longitude,latitude;

    public LocationModel(Double longitude, Double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public LocationModel() {
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
}
