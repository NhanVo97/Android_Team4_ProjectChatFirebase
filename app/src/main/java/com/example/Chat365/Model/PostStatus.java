package com.example.Chat365.Model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@IgnoreExtraProperties
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostStatus {
    private String key;
    private String content;
    private long time;
    private String access;
    private String id;
    private List<String> linkGalery;
    private Map<String,String> listShare,listLike;
    private Map<String,Comment> listComment;
    private boolean isCheck;
    public PostStatus(String key, String content, long time,String access,String id,List<String> linkGalery,boolean isCheck) {
        this.key = key;
        this.content = content;
        this.time = time;
        this.access = access;
        this.id = id;
        this.linkGalery = linkGalery;
        this.isCheck = isCheck;
    }
}
