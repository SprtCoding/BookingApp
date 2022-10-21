package com.sprtcoding.okidoc.Model;

public class ReminderModel {
    String ID;
    String Reminder;
    String SenderName;

    public ReminderModel() {
    }

    public ReminderModel(String ID, String reminder, String senderName) {
        this.ID = ID;
        Reminder = reminder;
        SenderName = senderName;
    }

    public String getID() {
        return ID;
    }

    public String getReminder() {
        return Reminder;
    }

    public String getSenderName() {
        return SenderName;
    }
}
