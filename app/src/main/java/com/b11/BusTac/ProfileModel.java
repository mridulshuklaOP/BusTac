package com.b11.BusTac;

public class ProfileModel {

    String FullName,Email,EnrollmentNumber;

    public ProfileModel(String fullName, String email, String enrollmentNumber) {
        FullName = fullName;
        Email = email;
        EnrollmentNumber = enrollmentNumber;
    }

    public ProfileModel() {
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getEnrollmentNumber() {
        return EnrollmentNumber;
    }

    public void setEnrollmentNumber(String enrollmentNumber) {
        EnrollmentNumber = enrollmentNumber;
    }

}
