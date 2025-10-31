-- V2: Seed initial data (safe inserts; users first for FK)
-- Note: ON CONFLICT uses UNIQUE columns from V1

-- Seed default admin user
INSERT INTO users (username, email, password, role)
VALUES ('admin', 'admin@portfolio.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN')  -- BCrypt for 'password'
ON CONFLICT (username) DO NOTHING;

-- Seed sample skills (conflict on name UNIQUE)
INSERT INTO skills (name, category, proficiency_level) VALUES
('Java', 'Backend', 10),
('Spring Boot', 'Backend', 9),
('Angular', 'Frontend', 8),
('PostgreSQL', 'Database', 8)
ON CONFLICT (name) DO NOTHING;

-- Seed sample project (conflict on title UNIQUE; user_id=1)
INSERT INTO projects (title, description, tech_stack, github_url, live_demo_url, user_id) VALUES
('Secure Task Manager', 'A full-stack task app with JWT auth.', '["Java", "Spring Boot", "Angular"]'::JSONB, 'https://github.com/yourname/task-app', 'https://task-demo.herokuapp.com', 1)
ON CONFLICT (title) DO NOTHING;

-- Link project to skills (project_id=1, skill_ids=1,2; conflict on PK)
INSERT INTO project_skill (project_id, skill_id) VALUES
(1, 1),  -- Java
(1, 2)   -- Spring Boot
ON CONFLICT (project_id, skill_id) DO NOTHING;

-- Seed sample blog post (conflict on title UNIQUE; author_id=1)
INSERT INTO blog_posts (title, content, author_id) VALUES
('Getting Started with Spring Boot', 'This post covers JPA and REST basics in Spring Boot.', 1)
ON CONFLICT (title) DO NOTHING;