package com.darts.dartsapp.model;

public class Settings {

    private int settingsID;
    private int userID;
    private String theme;
    private String dateFormat;
    private String timeFormat;

    public Settings(int id) {
        this.userID = id;
    }

    public void setSettingsID(int settingsID) { this.settingsID = settingsID; }

    public int getSettingsID() { return settingsID; }

    public void setUserID(int userID) { this.userID = userID; }

    public int getUserID() { return userID; }

    public void setTheme(String theme) { this.theme = theme; }

    public String getTheme() { return theme; }

    public void setDateFormat(String dateFormat) { this.dateFormat = dateFormat; }

    public String getDateFormat() { return dateFormat; }

    public void setTimeFormat(String timeFormat) { this.timeFormat = timeFormat; }

    public String getTimeFormat() { return timeFormat; }
}