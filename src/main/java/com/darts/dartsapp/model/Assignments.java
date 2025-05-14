package com.darts.dartsapp.model;

public class Assignments {

    private int assignmentID;
    private int classID;
    private String time;
    private int weight;
    private String type;
    private String colour;


    public Assignments(int classID, String time, int weight,  String type) {
        this.classID = classID;
        this.time = time;
        this.weight = weight;
        this.type = type;
    }

    public void setAssignmentID(int id) { this.assignmentID = id; }

    public int getAssignmentID() { return assignmentID; }

    public int getClassID() { return classID; }

    public String getTime() { return time; }

    public int getWeight() { return weight; }

    public void setType(String type) {this.type = type; }

    public String getType() { return type; }

    public void setColour(String colour) {this.colour = colour; }

    public String getColour() { return colour; }
}