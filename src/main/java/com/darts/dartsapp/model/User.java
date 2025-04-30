package com.darts.dartsapp.model;

public class User {

    private int userID;
    private String userName;
    private String email;
    private String phoneNumber;
    private String password;

    public User(String userName, String email, String phoneNumber, String password ) {
        this.userName = userName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;

    }

    public void setUserID(int id) { this.userID = id; }

    public int getUserID() { return userID; }

    public void setUserName(String userName) { this.userName = userName; }

    public String getUserName() { return userName; }

    public void setEmail(String email) { this.email = email; }

    public String getEmail() { return email; }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getPhoneNumber() { return phoneNumber; }

    public void setPassword(String password) { this.password = password; }

    public String getPassword() { return password; }

}