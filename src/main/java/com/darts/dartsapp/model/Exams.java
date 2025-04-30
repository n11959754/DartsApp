package com.darts.dartsapp.model;

public class Exams {

    private int examID;
    private int classID;
    private String time;
    private int weight;

    public Exams(int classID, String time, int weight ) {
        this.classID = classID;
        this.time = time;
        this.weight = weight;


    }

    public void setExamID(int id) { this.examID = id; }

    public int getExamID() { return examID; }

    public int getClassID() { return classID; }

    public String getTime() { return time; }

    public int getWeight() { return weight; }

}