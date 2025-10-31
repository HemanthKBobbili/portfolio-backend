package com.hkb.portfolio_backend.repository;

import com.hkb.portfolio_backend.dto.MessageDto;
import com.hkb.portfolio_backend.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    // Projection query to avoid proxies
    @Query("SELECT new com.hkb.portfolio_backend.dto.MessageDto(m.id, m.content, m.senderUsername, m.room, m.timestamp, m.user.id) " +
            "FROM Message m WHERE m.room = :room ORDER BY m.timestamp ASC")
    List<MessageDto> findByRoomOrderByTimestampAsc(@Param("room") String room);
}