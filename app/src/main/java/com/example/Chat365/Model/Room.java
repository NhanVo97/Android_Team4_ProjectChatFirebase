package com.example.Chat365.Model;

public class Room {
    private int Icon;
    private String Str;

    public Room(int icon, String str) {
        Icon = icon;
        Str = str;
    }

    public int getIcon() {
        return Icon;
    }

    public void setIcon(int icon) {
        Icon = icon;
    }

    public String getStr() {
        return Str;
    }

    public void setStr(String str) {
        Str = str;
    }
}
