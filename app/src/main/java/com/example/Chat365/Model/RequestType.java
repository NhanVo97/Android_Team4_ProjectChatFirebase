package com.example.Chat365.Model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
@IgnoreExtraProperties

public class RequestType implements Serializable {
    private String id;
    private String request_type;

    public RequestType() {

    }

    public RequestType(String id, String request_type) {
        this.id = id;
        this.request_type = request_type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }
}
