package com.hkb.portfolio_backend.service;



import com.hkb.portfolio_backend.dto.TaskDto;
import com.hkb.portfolio_backend.entity.Task;
import com.hkb.portfolio_backend.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    // Get all tasks for current user (paginated)
    public Page<TaskDto> getTasksForUser(Long userId, Pageable pageable) {
        return taskRepository.findByUserId(userId, pageable)
                .map(this::convertToDto);
    }

    // Get task by ID (owner only)
    public Optional<TaskDto> getTaskById(Long id, Long userId) {
        return taskRepository.findById(id)
                .filter(task -> task.getUser().getId().equals(userId))  // Owner check
                .map(this::convertToDto);
    }

    // Create task (assign to current user)
    @Transactional
    public TaskDto createTask(TaskDto dto, Long userId) {
        Task task = convertToEntity(dto);
        task.setUser(new com.hkb.portfolio_backend.entity.User());  // Temp; set ID
        task.getUser().setId(userId);  // Assign to user from token
        task = taskRepository.save(task);
        return convertToDto(task);
    }

    // Update task (owner only)
    @Transactional
    public Optional<TaskDto> updateTask(Long id, TaskDto dto, Long userId) {
        return taskRepository.findById(id)
                .filter(task -> task.getUser().getId().equals(userId))
                .map(task -> {
                    task.setTitle(dto.getTitle());
                    task.setDescription(dto.getDescription());
                    task.setPriority(dto.getPriority());
                    task.setDueDate(dto.getDueDate());
                    task.setCompleted(dto.getCompleted());
                    return convertToDto(taskRepository.save(task));
                });
    }

    // Delete task (owner only)
    @Transactional
    public void deleteTask(Long id, Long userId) {
        taskRepository.findById(id)
                .filter(task -> task.getUser().getId().equals(userId))
                .ifPresent(taskRepository::delete);
    }

    // Search tasks for user (using Streams for extra filter)
    public List<TaskDto> searchTasksForUser(Long userId, String keyword) {
        return taskRepository.searchByTitleForUser(userId, keyword).stream()
                .filter(task -> !task.getCompleted())  // Only pending (Stream example)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get pending by priority (custom query)
    public List<TaskDto> getPendingByPriority(Long userId, Task.Priority priority) {
        return taskRepository.findPendingByUserAndPriority(userId, priority).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Helpers: DTO <-> Entity
    private TaskDto convertToDto(Task task) {
        TaskDto dto = new TaskDto();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setPriority(task.getPriority());
        dto.setDueDate(task.getDueDate());
        dto.setCompleted(task.getCompleted());
        dto.setUserId(task.getUser().getId());
        return dto;
    }

    private Task convertToEntity(TaskDto dto) {
        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPriority(dto.getPriority());
        task.setDueDate(dto.getDueDate());
        task.setCompleted(dto.getCompleted());
        return task;
    }
}
