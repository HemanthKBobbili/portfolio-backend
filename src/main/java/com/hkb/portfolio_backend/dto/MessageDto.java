package com.hkb.portfolio_backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MessageDto {
    private Long id;
    private String content;
    private String senderUsername;
    private String room;
    private LocalDateTime timestamp;
    private Long userId;

    // Constructor for projection query
    public MessageDto(Long id, String content, String senderUsername, String room, LocalDateTime timestamp, Long userId) {
        this.id = id;
        this.content = content;
        this.senderUsername = senderUsername;
        this.room = room;
        this.timestamp = timestamp;
        this.userId = userId;
    }
}