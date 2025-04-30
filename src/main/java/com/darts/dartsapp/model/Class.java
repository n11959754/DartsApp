package com.darts.dartsapp.model;

public class Class {

    private int classID;
    private int userID;
    private String className;
    private String classColour;
    private String examColour;
    private String assignmentColour;

    public Class (int id, String className) {
        this.userID = id;
        this.className = className;
    }

    public void setID(int id) { this.classID = id; }

    public int getClassID() { return classID; }

    public void setUserID(int id) { this.userID = id; }

    public int getUserID() { return userID; }

    public void setClassName(String className) { this.className = className; }

    public String getClassName() { return className; }

    public void setClassColour(String classColour) { this.classColour = classColour; }

    public String getClassColour() { return classColour; }

    public void setExamColour(String classColour) { this.examColour = examColour; }

    public String getExamColour() { return examColour; }

    public void setAssignmentColour(String classColour) { this.assignmentColour = assignmentColour; }

    public String getAssignmentColour() { return assignmentColour; }


}