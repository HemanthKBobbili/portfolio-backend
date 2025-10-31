package com.hkb.portfolio_backend.service;

import com.hkb.portfolio_backend.dto.ProjectDto;
import com.hkb.portfolio_backend.entity.Project;
import com.hkb.portfolio_backend.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service  // Marks as Spring-managed bean
public class ProjectService {

    @Autowired  // Dependency Injection
    private ProjectRepository projectRepository;

    // Get all projects
    public List<ProjectDto> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::convertToDto)  // Map entity to DTO
                .collect(Collectors.toList());
    }

    // Get project by ID
    public Optional<ProjectDto> getProjectById(Long id) {
        return projectRepository.findById(id)
                .map(this::convertToDto);
    }

    // Create new project (basic; add user link later)
    public ProjectDto createProject(ProjectDto dto) {
        Project project = convertToEntity(dto);
        project = projectRepository.save(project);
        return convertToDto(project);
    }

    // Update project
    public Optional<ProjectDto> updateProject(Long id, ProjectDto dto) {
        return projectRepository.findById(id)
                .map(existing -> {
                    existing.setTitle(dto.getTitle());
                    existing.setDescription(dto.getDescription());
                    existing.setTechStack(dto.getTechStack());
                    existing.setGithubUrl(dto.getGithubUrl());
                    existing.setLiveDemoUrl(dto.getLiveDemoUrl());
                    return convertToDto(projectRepository.save(existing));
                });
    }

    // Delete project
    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    // Search projects (using Streams for filtering)
    public List<ProjectDto> searchProjects(String keyword) {
        return projectRepository.searchByTitle(keyword).stream()
                .filter(p -> p.getTitle().toLowerCase().contains(keyword.toLowerCase()))  // Extra filter example
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Helper: Entity <-> DTO conversion (manual mapping)
    private ProjectDto convertToDto(Project project) {
        ProjectDto dto = new ProjectDto();
        dto.setId(project.getId());
        dto.setTitle(project.getTitle());
        dto.setDescription(project.getDescription());
        dto.setTechStack(project.getTechStack());
        dto.setGithubUrl(project.getGithubUrl());
        dto.setLiveDemoUrl(project.getLiveDemoUrl());
        dto.setCreatedAt(project.getCreatedAt());
        if (project.getUser() != null) {
            dto.setUserId(project.getUser().getId());
        }
        return dto;
    }

    private Project convertToEntity(ProjectDto dto) {
        Project project = new Project();
        project.setTitle(dto.getTitle());
        project.setDescription(dto.getDescription());
        project.setTechStack(dto.getTechStack());
        project.setGithubUrl(dto.getGithubUrl());
        project.setLiveDemoUrl(dto.getLiveDemoUrl());
        // User link: Assume from dto.userId; fetch if needed
        return project;
    }
}
