package com.example.driverapp;


public class DriverModel {
    private int driverId;
    private Integer driverNumber;
    private String driverName;

    public DriverModel(int driverId, Integer driverNumber, String driverName) {
        this.driverId = driverId;
        this.driverNumber = driverNumber;
        this.driverName = driverName;
    }

    public DriverModel() {
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public Integer getDriverNumber() {
        return driverNumber;
    }

    public void setDriverNumber(Integer driverNumber) {
        this.driverNumber = driverNumber;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }
}
