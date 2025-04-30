package com.darts.dartsapp.model;

public class ClassTimeSlot {

    private int timeSlotID;
    private int classID;
    private String lectureTime;
    private String tutTime;

    public ClassTimeSlot(int classID, String lectureTime, String tutTime) {
        this.classID = classID;
        this.lectureTime = lectureTime;
        this.tutTime = tutTime;
    }

    public void setTimeSlotID(int id) { this.timeSlotID = id; }

    public int getTimeSlotID() { return timeSlotID; }

    public void setClassID(int id) { this.classID = id; }

    public int getClassID() { return classID; }

    public void setLectureTime(String lectureTime) { this.lectureTime = lectureTime; }

    public String getLectureTime() { return lectureTime; }

    public void setTutTime(String tutTime) { this.tutTime = tutTime; }

    public String getTutTime() { return tutTime; }
}