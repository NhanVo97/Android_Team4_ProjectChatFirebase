package com.example.Chat365.Model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationUser implements Serializable {
    private String address;
    private String stateName;
    private String countryName;
    private double latitude;
    private double longitude;
    private boolean isHide;
}
