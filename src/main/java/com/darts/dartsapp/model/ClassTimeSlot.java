package com.darts.dartsapp.model;

public class ClassTimeSlot {

    private int timeSlotID;
    private int classID;
    private String time;
    private String day;
    private String type;
    private String colour;


    public ClassTimeSlot(int classID, String time, String day, String type) {
        this.classID = classID;
        this.time = time;
        this.day = day;
        this.type = type;
    }

    public void setTimeSlotID(int id) { this.timeSlotID = id; }

    public int getTimeSlotID() { return timeSlotID; }

    public void setClassID(int id) { this.classID = id; }

    public int getClassID() { return classID; }

    public void setTime(String time) { this.time = time; }

    public String getTime() { return time; }

    public void setType(String type) { this.type = type; }

    public String getDay() { return day; }

    public void setDay(String day) { this.day = day; }

    public String getType() { return type; }

    public void setColour(String colour) { this.colour = colour; }

    public String getColour() { return colour; }
}