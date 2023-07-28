package com.example.goldentoads;

import java.io.Serializable;

public class User {
    String key;
   String userID;
   String userPassword;


    public User(String userID, String userPassword){

        this.userID = userID;
        this. userPassword =userPassword;
    }

    public User(String key, String userID, String userPassword){

        this.key = key;
        this.userID = userID;
        this. userPassword =userPassword;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
}
