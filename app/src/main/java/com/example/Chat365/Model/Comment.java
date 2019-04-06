package com.example.Chat365.Model;

public class Comment {
    private String Key;
    private String ID;
    private String User;
    private String ND;
    private long Time;
    public Comment() {
    }

    public Comment(String Key,String ID, String user, String ND,long Time) {
        this.Key=Key;
        this.ID = ID;
        User = user;
        this.ND = ND;
        this.Time=Time;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public String getND() {
        return ND;
    }

    public void setND(String ND) {
        this.ND = ND;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public long getTime() {
        return Time;
    }

    public void setTime(long time) {
        Time = time;
    }

}
