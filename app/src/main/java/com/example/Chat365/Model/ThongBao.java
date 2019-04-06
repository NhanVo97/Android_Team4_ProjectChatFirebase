package com.example.Chat365.Model;

public class ThongBao  {
    private String ID;
    private String NDTB;
    private long timestamp;

    public ThongBao() {
    }

    public ThongBao(String ID, String NDTB,long timestamp) {
        this.ID = ID;
        this.NDTB = NDTB;
        this.timestamp=timestamp;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getNDTB() {
        return NDTB;
    }

    public void setNDTB(String NDTB) {
        this.NDTB = NDTB;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
