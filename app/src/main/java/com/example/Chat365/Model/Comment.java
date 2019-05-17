package com.example.Chat365.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    private String Key;
    private String ID;
    private String User;
    private String ND;
    private long Time;
}
