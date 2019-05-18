package com.example.Chat365.Model;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@IgnoreExtraProperties
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    private String tokenNotification;
    private String linkBackground;
}
