package com.example.carecreds.Model;

import com.google.firebase.firestore.PropertyName;

public class CurrentlyEnrolled {
    private int points;

    @PropertyName("serviceId")
    private String serviceID;
    private String volunteerEmail;
    private String rating;
    private String volunteerName;
    private String clientEmail;
    private String serviceDescription;
    private String serviceTitle;

    private String id;

    public CurrentlyEnrolled() {
    }

    public CurrentlyEnrolled(int points, String serviceID, String volunteerEmail, String rating, String volunteerName, String clientEmail, String serviceDescription, String serviceTitle) {
        this.points = points;
        this.serviceID = serviceID;
        this.volunteerEmail = volunteerEmail;
        this.rating = rating;
        this.volunteerName = volunteerName;
        this.clientEmail = clientEmail;
        this.serviceDescription = serviceDescription;
        this.serviceTitle = serviceTitle;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPoints() {
        return points;
    }

    public String getServiceID() {
        return serviceID;
    }

    public String getVolunteerEmail() {
        return volunteerEmail;
    }

    public String getRating() {
        return rating;
    }

    public String getVolunteerName() {
        return volunteerName;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public String getServiceTitle() {
        return serviceTitle;
    }

    public String getId() {
        return id;
    }
}
