package com.example.carecreds.Model;

public class Volunteer {
    private String address;
    private int age;
    private String availability;
    private String email;
    private String fullName;
    private String gender;
    private String phone;
    private int rating;
    private String primaryEducation;
    private String postSecondaryEducation;
    private String secondaryEducation;
    private String workExperience;

    public Volunteer() {
    }

    public Volunteer(String address, int age, String availability, String email, String fullName, String gender, String phone, int rating, String primaryEducation, String postSecondaryEducation, String secondaryEducation, String workExperience) {
        this.address = address;
        this.age = age;
        this.availability = availability;
        this.email = email;
        this.fullName = fullName;
        this.gender = gender;
        this.phone = phone;
        this.rating = rating;
        this.primaryEducation = primaryEducation;
        this.postSecondaryEducation = postSecondaryEducation;
        this.secondaryEducation = secondaryEducation;
        this.workExperience = workExperience;
    }

    public String getAddress() {
        return address;
    }

    public int getAge() {
        return age;
    }

    public String getAvailability() {
        return availability;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getGender() {
        return gender;
    }

    public String getPhone() {
        return phone;
    }

    public int getRating() {
        return rating;
    }

    public String getPrimaryEducation() {
        return primaryEducation;
    }

    public String getPostSecondaryEducation() {
        return postSecondaryEducation;
    }

    public String getSecondaryEducation() {
        return secondaryEducation;
    }

    public String getWorkExperience() {
        return workExperience;
    }
}
