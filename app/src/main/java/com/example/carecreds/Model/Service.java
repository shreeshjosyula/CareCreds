package com.example.carecreds.Model;

import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;

public class Service {
    private String id;
    @PropertyName("title")
    private String title;
    @PropertyName("description")
    private String description;
    @PropertyName("volunteersNeeded")
    private int volunteersNeeded;
    @PropertyName("hours")
    private int hours;
    @PropertyName("points")
    private int points;
    @PropertyName("extraNotes")
    private String extraNotes;

    @PropertyName("enrolledVolunteers")
    private ArrayList<String> enrolledVolunteers;

    @PropertyName("isCompleted")
    private boolean isCompleted;

    @PropertyName("clientEmail")
    private String clientEmail;

    public Service() {
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public Service(String id, String title, String description, int volunteersNeeded, int hours, int points, String extraNotes, ArrayList<String> enrolledVolunteers, boolean isCompleted, String clientEmail) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.volunteersNeeded = volunteersNeeded;
        this.hours = hours;
        this.points = points;
        this.extraNotes = extraNotes;
        this.enrolledVolunteers = enrolledVolunteers;
        this.isCompleted = isCompleted;
        this.clientEmail = clientEmail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getVolunteersNeeded() {
        return volunteersNeeded;
    }

    public int getHours() {
        return hours;
    }

    public int getPoints() {
        return points;
    }

    public String getExtraNotes() {
        return extraNotes;
    }

    public ArrayList<String> getEnrolledVolunteers() {
        return enrolledVolunteers;
    }

    public boolean isCompleted() {
        return isCompleted;
    }
}
