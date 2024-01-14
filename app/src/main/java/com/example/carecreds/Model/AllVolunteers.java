package com.example.carecreds.Model;

public class AllVolunteers {
    private String address;
    private int age;
    private String availability;
    private String email;
    private String fullName;
    private String gender;
    private String phone;
    private String postSecondaryEducation;
    private String primaryEducation;
    private int rating;
    private String secondaryEducation;
    private String workExperience;

    public AllVolunteers() {
    }

    public AllVolunteers(String address, int age, String availability, String email, String fullName, String gender, String phone, String postSecondaryEducation, String primaryEducation, int rating, String secondaryEducation, String workExperience) {
        this.address = address;
        this.age = age;
        this.availability = availability;
        this.email = email;
        this.fullName = fullName;
        this.gender = gender;
        this.phone = phone;
        this.postSecondaryEducation = postSecondaryEducation;
        this.primaryEducation = primaryEducation;
        this.rating = rating;
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

    public String getPostSecondaryEducation() {
        return postSecondaryEducation;
    }

    public String getPrimaryEducation() {
        return primaryEducation;
    }

    public int getRating() {
        return rating;
    }

    public String getSecondaryEducation() {
        return secondaryEducation;
    }

    public String getWorkExperience() {
        return workExperience;
    }
}

