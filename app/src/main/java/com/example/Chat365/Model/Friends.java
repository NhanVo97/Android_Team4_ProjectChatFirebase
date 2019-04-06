package com.example.Chat365.Model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Friends {
    private String id;
    private String Status;

    public Friends() {
    }

    public Friends(String id, String status,String caption) {
        this.id = id;
        Status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

}
