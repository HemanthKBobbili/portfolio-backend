-- V1: Create core tables for portfolio entities
-- Note: Run in order; adds UNIQUE for seed conflicts

-- Users table (UNIQUE on username/email already)
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'USER', 'VISITOR')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Projects table (add UNIQUE on title for seed)
CREATE TABLE IF NOT EXISTS projects (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL UNIQUE,  -- UNIQUE added for ON CONFLICT in V2
    description TEXT,
    tech_stack JSONB,
    github_url VARCHAR(500),
    live_demo_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

-- Skills table (add UNIQUE on name for seed)
CREATE TABLE IF NOT EXISTS skills (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,  -- UNIQUE added for ON CONFLICT in V2
    category VARCHAR(50),
    proficiency_level INTEGER CHECK (proficiency_level BETWEEN 1 AND 10)
);

-- Blog Posts table (add UNIQUE on title for seed)
CREATE TABLE IF NOT EXISTS blog_posts (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL UNIQUE,  -- UNIQUE added for ON CONFLICT in V2
    content TEXT NOT NULL,
    publish_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    author_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

-- Join table for Project-Skill M:M
CREATE TABLE IF NOT EXISTS project_skill (
    project_id BIGINT NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    skill_id BIGINT NOT NULL REFERENCES skills(id) ON DELETE CASCADE,
    PRIMARY KEY (project_id, skill_id)
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_projects_user_id ON projects(user_id);
CREATE INDEX IF NOT EXISTS idx_blog_posts_author_id ON blog_posts(author_id);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_project_skill_project ON project_skill(project_id);
CREATE INDEX IF NOT EXISTS idx_project_skill_skill ON project_skill(skill_id);


