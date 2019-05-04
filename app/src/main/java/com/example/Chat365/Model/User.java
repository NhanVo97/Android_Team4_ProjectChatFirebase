package com.example.Chat365.Model;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
@IgnoreExtraProperties
public class User implements Serializable{
    private String name;
    private  String password;
    private  String email;
    private String sex;
    private String birthday;
    private String role;
    private String homeTown;
    private String linkAvatar;
    private String live;
    private String work;
    private String study;
    private String relationship;
    private String id;
    private String language;
    private String isOnline;
    private String status;
    private Long timestamp;
    private String history;
    private LocationUser locationUser;
    public User() {

    }

    public User(String name, String password, String email, String sex, String birthday, String role,
                String homeTown, String linkAvatar, String live, String work, String study,
                String relationship, String id,String language, String isOnline, String status, Long timestamp,
                String history,LocationUser locationUser) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.sex = sex;
        this.birthday = birthday;
        this.role = role;
        this.homeTown = homeTown;
        this.linkAvatar = linkAvatar;
        this.live = live;
        this.work = work;
        this.study = study;
        this.relationship = relationship;
        this.id = id;
        this.language = language;
        this.isOnline = isOnline;
        this.status = status;
        this.timestamp = timestamp;
        this.history = history;
        this.locationUser = locationUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getHomeTown() {
        return homeTown;
    }

    public void setHomeTown(String homeTown) {
        this.homeTown = homeTown;
    }

    public String getLinkAvatar() {
        return linkAvatar;
    }

    public void setLinkAvatar(String linkAvatar) {
        this.linkAvatar = linkAvatar;
    }

    public String getLive() {
        return live;
    }

    public void setLive(String live) {
        this.live = live;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getStudy() {
        return study;
    }

    public void setStudy(String study) {
        this.study = study;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(String isOnline) {
        this.isOnline = isOnline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public LocationUser getLocationUser() {
        return locationUser;
    }

    public void setLocationUser(LocationUser locationUser) {
        this.locationUser = locationUser;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }
}
