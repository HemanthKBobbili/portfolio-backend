package com.hkb.portfolio_backend.controller;

import com.hkb.portfolio_backend.entity.Message;
import com.hkb.portfolio_backend.repository.UserRepository;
import com.hkb.portfolio_backend.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController  // For WebSocket; keep as is
@RequestMapping("api/chat")
public class MessageController {

    @Autowired private MessageService messageService;
    @Autowired private SimpMessagingTemplate messagingTemplate;
    @Autowired private UserRepository userRepository;  // Inject instead of new

    // REST: Get message history for a room
    @GetMapping("/rooms/{room}")
    public List<com.hkb.portfolio_backend.dto.MessageDto> getMessages(@PathVariable String room) {
        return messageService.getMessagesForRoom(room);
    }

    // WebSocket: Handle incoming messages
    @MessageMapping("/chat/{room}/sendMessage")
    @SendTo("/topic/chat/{room}")
    public Message sendMessage(@DestinationVariable String room, Message message, Authentication auth) {  // Use your Message entity
        String username = ((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername();
        Long userId = userRepository.findByUsername(username)
                .map(com.hkb.portfolio_backend.entity.User::getId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        message.setSenderUsername(username);  // Now resolves with @Data
        message.setRoom(room);
        message.setUser(new com.hkb.portfolio_backend.entity.User());
        message.getUser().setId(userId);

        messageService.saveMessageAsync(message.getContent(), username, room, userId);

        return message;
    }
}