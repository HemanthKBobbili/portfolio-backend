package com.hkb.portfolio_backend.controller;


import com.hkb.portfolio_backend.dto.TaskDto;
import com.hkb.portfolio_backend.entity.Task;
import com.hkb.portfolio_backend.repository.UserRepository;
import com.hkb.portfolio_backend.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "http://localhost:4200")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserRepository userRepository;

    // GET /api/tasks (paginated for user)
    @GetMapping
    @PreAuthorize("isAuthenticated()")  // JWT required
    public Page<TaskDto> getTasks(Authentication authentication, Pageable pageable) {
        Long userId = getUserIdFromAuth(authentication);
        return taskService.getTasksForUser(userId, pageable);
    }

    // GET /api/tasks/{id}
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskDto> getTask(@PathVariable Long id, Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        return taskService.getTaskById(id, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/tasks
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public TaskDto createTask(@Valid @RequestBody TaskDto dto, Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        return taskService.createTask(dto, userId);
    }

    // PUT /api/tasks/{id}
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskDto> updateTask(@PathVariable Long id, @Valid @RequestBody TaskDto dto, Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        return taskService.updateTask(id, dto, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/tasks/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        taskService.deleteTask(id, userId);
        return ResponseEntity.noContent().build();
    }

    // GET /api/tasks/search?keyword=high
    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public List<TaskDto> searchTasks(@RequestParam String keyword, Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        return taskService.searchTasksForUser(userId, keyword);
    }

    // GET /api/tasks/pending?priority=HIGH (admin or user)
    @GetMapping("/pending")
    @PreAuthorize("isAuthenticated()")
    public List<TaskDto> getPendingByPriority(@RequestParam Task.Priority priority, Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        return taskService.getPendingByPriority(userId, priority);
    }

    // Helper: Extract userId from JWT token (via username from UserDetails)
    private Long getUserIdFromAuth(Authentication authentication) {
        // Get the UserDetails object from principal
        org.springframework.security.core.userdetails.User userDetails =
                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

        // Extract username from UserDetails
        String username = userDetails.getUsername();

        // Fetch userId from repository
        return userRepository.findByUsername(username)
                .map(com.hkb.portfolio_backend.entity.User::getId)
                .orElseThrow(() -> new RuntimeException("User not found from token: " + username));
    }
}
