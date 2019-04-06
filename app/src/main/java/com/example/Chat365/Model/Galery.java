package com.example.Chat365.Model;

import java.io.Serializable;

public class Galery implements Serializable {
    private String link;
    private String name;
    private boolean check;
    private String Num;
    private byte[] byteArray;

    public Galery() {
    }

    public Galery(String link, String name, boolean check, String Num) {
        this.link = link;
        this.name = name;
        this.check =check;
        this.Num=Num;
    }

    public Galery(byte[] byteArray) {
        this.byteArray = byteArray;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public String getNum() {
        return Num;
    }

    public void setNum(String num) {
        Num = num;
    }

    public byte[] getByteArray() {
        return byteArray;
    }

    public void setByteArray(byte[] byteArray) {
        this.byteArray = byteArray;
    }
}
