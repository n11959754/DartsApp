package com.darts.dartsapp.model;

public class Class {

    private int classID;
    private int userID;
    private String className;

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

    @Override
    public String toString(){
        return className;
    }
}
