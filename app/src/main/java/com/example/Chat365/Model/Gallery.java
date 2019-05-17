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
public class Gallery implements Serializable {
    private String link;
    private String name;
    private int number;
    private boolean isCheck;
    private byte[] byteArray;
    public Gallery(byte[] byteArray) {
        this.byteArray = byteArray;
    }
}
