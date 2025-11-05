package com.hkb.portfolio_backend.service;

import com.hkb.portfolio_backend.dto.MessageDto;
import com.hkb.portfolio_backend.entity.Message;
import com.hkb.portfolio_backend.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Service
public class MessageService {

    @Autowired private MessageRepository messageRepository;

    private static final Logger log = LoggerFactory.getLogger(MessageService.class);

    // Get messages for a room
    public List<MessageDto> getMessagesForRoom(String room) {
        return messageRepository.findByRoomOrderByTimestampAsc(room);
    }

    // Save message asynchronously (background task, non-blocking)
    @Async
    public void saveMessageAsync(String content, String senderUsername, String room, Long userId) {
        Message message = new Message();
        message.setContent(content);
        message.setSenderUsername(senderUsername);
        message.setRoom(room);
        message.setUser(new com.hkb.portfolio_backend.entity.User());
        message.getUser().setId(userId);

        // Simulate delay (e.g., for processing)
        try { Thread.sleep(100); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        messageRepository.save(message);
        log.info("Message saved asynchronously:  {}", content); // Log for demo


    }
}

