package com.example.Chat365.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CallVideo {
    private String id;
    private String senderId;
    private String statusSender;
    private String receivedId;
    private String statusReceivedId;
}
