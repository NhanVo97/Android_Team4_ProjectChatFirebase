package com.example.Chat365.Model;

import java.util.List;
import java.util.Map;

public class PostStatus {
    private String key;
    private String content;
    private long time;
    private String access;
    private String id;
    private List<String> linkAnh;
    private Map<String,String> listShare,listLike;
    private Map<String,Comment> listBinhLuan;
    private boolean check;
    public PostStatus() {
    }

    public PostStatus(String key, String content, long time,String access,String id,List<String> linkAnh,boolean check) {
        this.key = key;
        this.content = content;
        this.time = time;
        this.access=access;
        this.id=id;
        this.linkAnh =linkAnh;
        this.check=check;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getLinkAnh() {
        return linkAnh;
    }

    public void setLinkAnh(List<String> linkAnh) {
        this.linkAnh = linkAnh;
    }

    public Map<String, String> getListShare() {
        return listShare;
    }

    public void setListShare(Map<String, String> listShare) {
        this.listShare = listShare;
    }

    public Map<String, String> getListLike() {
        return listLike;
    }

    public void setListLike(Map<String, String> listLike) {
        this.listLike = listLike;
    }

    public Map<String, Comment> getListBinhLuan() {
        return listBinhLuan;
    }

    public void setListBinhLuan(Map<String, Comment> listBinhLuan) {
        this.listBinhLuan = listBinhLuan;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}
