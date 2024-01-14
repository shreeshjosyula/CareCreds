package com.example.carecreds.Model;

import com.google.firebase.firestore.PropertyName;

public class Request {
    @PropertyName("clientEmail")
    private String clientEmail;
    @PropertyName("volunteerEmail")
    private String volunteerEmail;
    @PropertyName("serviceID")
    private String serviceId;
    @PropertyName("serviceName")
    private String serviceName;

    @PropertyName("description")
    private String description;

    private String requestId;
    @PropertyName("points")
    private int points;

    public Request() {
    }

    public Request(String clientEmail, String volunteerEmail, String serviceId, String serviceName, String description, int points) {
        this.clientEmail = clientEmail;
        this.volunteerEmail = volunteerEmail;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.description = description;
        this.points = points;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public String getVolunteerEmail() {
        return volunteerEmail;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getDescription() {
        return description;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public int getPoints() {
        return points;
    }
}
