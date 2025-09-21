INSERT INTO users (email, password, first_name, last_name, organization, verified, role) VALUES
    ('unverified@example.com', '$2a$10$DowJq3yK3g9cVxv1P7tU6O9k8eF6vR6l5WmZs5YXs3f4p5kTq5k7K', 'Emily', 'David', 'TechCorp', false, 'REGULAR_USER'),
    ('verified@example.com', '$2a$10$DowJq3yK3g9cVxv1P7tU6O9k8eF6vR6l5WmZs5YXs3f4p5kTq5k7K', 'John', 'Doe', 'InnovateLtd', true, 'REGULAR_USER');
