package com.darts.dartsapp.model;

public class Tasks {

    private int id;
    private String details;
    private int duration;
    private int assignmentID;

    public Tasks(int assignmentID, String details, int duration) {
        this.details = details;
        this.duration = duration;
        this.assignmentID = assignmentID;
    }

    public void setID( int id) {this.id = id;}

    public int getID() {return id; }

    public String getDetails() {return details; }

    public int getDuration() {return duration; }

    public int getAssignmentID() {return assignmentID; }
}
