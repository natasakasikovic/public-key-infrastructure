INSERT INTO users (email, password, first_name, last_name, organization, verified, role) VALUES
    ('unverified@example.com', '$2a$10$Z3JiBldbaNQ4qGPjtr7TV.FeT2He/KgqxT68impZ9.H3XeyQAZ03W', 'Emily', 'David', 'TechCorp', false, 'REGULAR_USER'),
    ('admin@example.com', '$2a$10$Z3JiBldbaNQ4qGPjtr7TV.FeT2He/KgqxT68impZ9.H3XeyQAZ03W', 'Emily', 'David', 'TechCorp', true, 'ADMIN'),
    ('verified@example.com', '$2a$10$Z3JiBldbaNQ4qGPjtr7TV.FeT2He/KgqxT68impZ9.H3XeyQAZ03W', 'John', 'Doe', 'InnovateLtd', true, 'REGULAR_USER');
