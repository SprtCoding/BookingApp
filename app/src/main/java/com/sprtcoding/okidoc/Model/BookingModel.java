package com.sprtcoding.okidoc.Model;

public class BookingModel {
    String who;
    String reason;
    String dateOfBooking;
    String timeOfBooking;
    String status;
    String userID;
    String bookID;
    String Name;

    public BookingModel() {
    }

    public BookingModel(String who, String reason, String dateOfBooking, String timeOfBooking, String status, String userID, String bookID, String name) {
        this.who = who;
        this.reason = reason;
        this.dateOfBooking = dateOfBooking;
        this.timeOfBooking = timeOfBooking;
        this.status = status;
        this.userID = userID;
        this.bookID = bookID;
        this.Name = name;
    }

    public String getName() {
        return Name;
    }

    public String getBookID() {
        return bookID;
    }

    public String getWho() {
        return who;
    }

    public String getReason() {
        return reason;
    }

    public String getDateOfBooking() {
        return dateOfBooking;
    }

    public String getTimeOfBooking() {
        return timeOfBooking;
    }

    public String getStatus() {
        return status;
    }

    public String getUserID() {
        return userID;
    }
}
