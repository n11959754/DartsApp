package com.darts.dartsapp.model;

public class Units {

    private int classID;
    private String className;
    private int timeSlotID;
    private String time;
    private String day;
    private String classType;
    private String classColour;
    private int assignmentID;
    private String assignmentTime;
    private String assignmentDay;
    private int weight;
    private String assignmentType;
    private String assignmentColour;

    public Units(int classID, String className, int timeSlotID, String time, String day, String classType, String classColour, int assignmentID, String assignmentTime, String assignmentDay, int weight, String assignmentType, String assignmentColour) {
        this.classID = classID;
        this.className = className;
        this.timeSlotID = timeSlotID;
        this.time = time;
        this.day = day;
        this.classType = classType;
        this.classColour = classColour;
        this.assignmentID = assignmentID;
        this.assignmentTime = assignmentTime;
        this.assignmentDay = assignmentDay;
        this.weight = weight;
        this.assignmentType = assignmentType;
        this.assignmentColour = assignmentColour;
    }

    public int getClassID() {return classID;}

    public String getClassName() {return className;}

    public int getTimeSlotID() {return timeSlotID;}

    public String getTime() {return time;}

    public String getDay() {return day;}

    public String getClassType() {return classType;}

    public String getClassColour() {return classColour;}

    public int getAssignmentID() {return assignmentID;}

    public String getAssignmentTime() {return assignmentTime;}

    public String getAssignmentDay() {return assignmentDay;}

    public int getWeight() {return weight;}

    public String getAssignmentType() {return assignmentType;}

    public String getAssignmentColour() {return assignmentColour;}
}
