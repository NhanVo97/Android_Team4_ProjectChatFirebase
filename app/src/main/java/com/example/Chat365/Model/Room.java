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
public class Room implements Serializable {
    private int icon;
    private String str;
}
