package com.hkb.portfolio_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "skills")
@Data
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;  // e.g., "Spring Boot"

    private String category;  // e.g., "Backend"

    @Column(name = "proficiency_level")
    private Integer proficiencyLevel;  // 1-10
}
