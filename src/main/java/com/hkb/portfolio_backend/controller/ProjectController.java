package com.hkb.portfolio_backend.controller;

import com.hkb.portfolio_backend.dto.ProjectDto;
import com.hkb.portfolio_backend.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController  // JSON responses
@RequestMapping("/api/projects")  // Base path
@CrossOrigin(origins = "http://localhost:4200")  // For future frontend
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    // GET /api/projects - List all
    @GetMapping
    public List<ProjectDto> getAllProjects() {
        return projectService.getAllProjects();
    }

    // GET /api/projects/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> getProjectById(@PathVariable Long id) {
        return projectService.getProjectById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/projects - Create
    @PostMapping
    public ProjectDto createProject(@Valid @RequestBody ProjectDto dto) {
        return projectService.createProject(dto);
    }

    // PUT /api/projects/{id} - Update
    @PutMapping("/{id}")
    public ResponseEntity<ProjectDto> updateProject(@PathVariable Long id, @Valid @RequestBody ProjectDto dto) {
        return projectService.updateProject(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/projects/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/projects/search?keyword=java - Search
    @GetMapping("/search")
    public List<ProjectDto> searchProjects(@RequestParam String keyword) {
        return projectService.searchProjects(keyword);
    }
}
