package com.example.Chat365.Model;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
@IgnoreExtraProperties
public class User implements Serializable{
    private String Name;
    private  String Password;
    private  String Email;
    private String Sex;
    private String Birthday;
    private String level;
    private String History;
    private String Province;
    private String avatar;
    private String HomeTown;
    private String Work;
    private String Study;
    private String Relationship;
    private String id;
    private String isOnline;
    private String Status;
    private Long timestamp;

    public User() {

    }

    public User(String name, String password, String email, String sex, String birthday, String level, String history, String province, String avatar, String homeTown, String work, String study, String relationship,String id,String isOnline,String status,long timestamp) {
        Name = name;
        Password = password;
        Email = email;
        Sex = sex;
        Birthday = birthday;
        this.level = level;
        History = history;
        Province = province;
        this.avatar = avatar;
        HomeTown = homeTown;
        Work = work;
        Study = study;
        Relationship = relationship;
        this.id = id;
        this.isOnline=isOnline;
        this.Status=status;
        this.timestamp=timestamp;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getSex() {
        return Sex;
    }

    public void setSex(String sex) {
        Sex = sex;
    }

    public String getBirthday() {
        return Birthday;
    }

    public void setBirthday(String birthday) {
        Birthday = birthday;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getHistory() {
        return History;
    }

    public void setHistory(String history) {
        History = history;
    }

    public String getProvince() {
        return Province;
    }

    public void setProvince(String province) {
        Province = province;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getHomeTown() {
        return HomeTown;
    }

    public void setHomeTown(String homeTown) {
        HomeTown = homeTown;
    }

    public String getWork() {
        return Work;
    }

    public void setWork(String work) {
        Work = work;
    }

    public String getStudy() {
        return Study;
    }

    public void setStudy(String study) {
        Study = study;
    }

    public String getRelationship() {
        return Relationship;
    }

    public void setRelationship(String relationship) {
        Relationship = relationship;
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

    public String getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(String isOnline) {
        this.isOnline = isOnline;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
