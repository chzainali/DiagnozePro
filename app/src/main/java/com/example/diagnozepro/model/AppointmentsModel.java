package com.example.diagnozepro.model;

public class AppointmentsModel {
    String id, userId, doctorId, userName, doctorName, userPhone, title, dateTime, details, status;

    public AppointmentsModel() {
    }

    public AppointmentsModel(String id, String userId, String doctorId, String userName, String doctorName, String userPhone, String title, String dateTime, String details, String status) {
        this.id = id;
        this.userId = userId;
        this.doctorId = doctorId;
        this.userName = userName;
        this.doctorName = doctorName;
        this.userPhone = userPhone;
        this.title = title;
        this.dateTime = dateTime;
        this.details = details;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
