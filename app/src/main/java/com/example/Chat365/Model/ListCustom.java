package com.example.Chat365.Model;

public class ListCustom {
    private String url;
    private String Name;

    public ListCustom(String url, String name) {
        this.url = url;
        Name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
