package com.example.Chat365.Model;

import java.util.Map;

public class Album {
    private String name, caption, quyen;
    Map<String,String> listFriends;
    Map<String,String> listHinh;
    public Album() {
    }

    public Album(String name, String caption, String quyen, Map<String,String> listFriends, Map<String,String> listHinh) {
        this.name = name;
        this.caption = caption;
        this.quyen = quyen;
        this.listFriends = listFriends;
        this.listHinh=listHinh;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getQuyen() {
        return quyen;
    }

    public void setQuyen(String quyen) {
        this.quyen = quyen;
    }

    public Map<String, String> getListFriends() {
        return listFriends;
    }

    public void setListFriends(Map<String, String> listFriends) {
        this.listFriends = listFriends;
    }

    public Map<String, String> getListHinh() {
        return listHinh;
    }

    public void setListHinh(Map<String, String> listHinh) {
        this.listHinh = listHinh;
    }
}