package com.example.Chat365.Model;

public class Quyen {
    private String Name;
    private String Cap;

    public Quyen(String name, String cap) {
        Name = name;
        Cap = cap;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCap() {
        return Cap;
    }

    public void setCap(String cap) {
        Cap = cap;
    }
}
