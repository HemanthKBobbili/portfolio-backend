package com.hkb.portfolio_backend.repository;

import com.hkb.portfolio_backend.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    // Find tasks by user_id (owner)
    Page<Task> findByUserId(Long userId, Pageable pageable);

    // Custom query: Find completed tasks by priority
    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.priority = :priority AND t.completed = false")
    List<Task> findPendingByUserAndPriority(@Param("userId") Long userId, @Param("priority") Task.Priority priority);

    // Search by title (case-insensitive)
    @Query("SELECT t FROM Task t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) AND t.user.id = :userId")
    List<Task> searchByTitleForUser(@Param("userId") Long userId, @Param("keyword") String keyword);
}
