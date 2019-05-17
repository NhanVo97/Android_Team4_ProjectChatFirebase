package com.example.Chat365.Model;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupFriends implements Serializable {
    private String key;
    private String nameGroup;
    private List<String> listFriend;
    private String linkAvatar;
}
