package com.example.Chat365.Model;

import java.io.Serializable;
import java.util.List;

public class GroupFriends implements Serializable {
    private String key;
    private String nameGroup;
    private List<String> listFriend;
    private String linkAvatar;

    public GroupFriends() {
    }

    public GroupFriends(String key, String nameGroup, List<String> listFriend, String linkAvatar) {
        this.nameGroup = nameGroup;
        this.listFriend = listFriend;
        this.linkAvatar = linkAvatar;
        this.key = key;
    }

    public String getNameGroup() {
        return nameGroup;
    }

    public void setNameGroup(String nameGroup) {
        this.nameGroup = nameGroup;
    }

    public List<String> getListFriend() {
        return listFriend;
    }

    public void setListFriend(List<String> listFriend) {
        this.listFriend = listFriend;
    }

    public String getLinkAvatar() {
        return linkAvatar;
    }

    public void setLinkAvatar(String linkAvatar) {
        this.linkAvatar = linkAvatar;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
