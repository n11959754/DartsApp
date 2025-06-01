package com.darts.dartsapp.model;

import java.time.LocalDate;

public class User {

    private int userID;
    private String userName;
    private String email;
    private String phoneNumber;
    private String password;
    private LocalDate birthday;


    public User(String userName, String email, String phoneNumber, String password) {
        this.userName = userName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }


    public User(String userName, String email, String phoneNumber, String password, LocalDate birthday) {
        this.userName = userName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.birthday = birthday;
    }

    public void setUserID(int id) {
        this.userID = id;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }
}
