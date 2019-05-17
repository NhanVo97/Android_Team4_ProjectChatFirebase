package com.example.Chat365.Model;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Album {
    private String key;
    private String name, caption, quyen;
    private List<String> listHinh;
    private List<String> listFriends;
}