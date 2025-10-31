package com.hkb.portfolio_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Entity
@Table(name = "messages")

public class Message {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;  // Message text

    @Column(name = "sender_username", nullable = false)
    private String senderUsername;  // Username of sender

    @Column(nullable = false)
    private String room;  // Chat room (e.g., 'general')

    @Column(name = "timestamp")
    private LocalDateTime timestamp = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // Link to sender

}
