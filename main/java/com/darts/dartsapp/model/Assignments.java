package com.darts.dartsapp.model;

public class Assignments {

    private int assignmentID;
    private int classID;
    private String time;
    private int weight;

    public Assignments(int classID, String time, int weight ) {
        this.classID = classID;
        this.time = time;
        this.weight = weight;


    }

    public void setAssignmentID(int id) { this.assignmentID = id; }

    public int getAssignmentID() { return assignmentID; }

    public int getClassID() { return classID; }

    public String getTime() { return time; }

    public int getWeight() { return weight; }
}
