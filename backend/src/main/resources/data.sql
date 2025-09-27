INSERT INTO users (email, password, first_name, last_name, organization, verified, role) VALUES
    ('unverified@example.com', '$2a$10$Z3JiBldbaNQ4qGPjtr7TV.FeT2He/KgqxT68impZ9.H3XeyQAZ03W', 'Emily', 'David', 'TechCorp', false, 'REGULAR_USER'),
    ('admin@example.com', '$2a$10$Z3JiBldbaNQ4qGPjtr7TV.FeT2He/KgqxT68impZ9.H3XeyQAZ03W', 'Emily', 'David', 'TechCorp', true, 'ADMIN'),
    ('verified@example.com', '$2a$10$Z3JiBldbaNQ4qGPjtr7TV.FeT2He/KgqxT68impZ9.H3XeyQAZ03W', 'John', 'Doe', 'InnovateLtd', true, 'REGULAR_USER'),
    ('ca@example.com', '$2a$10$Z3JiBldbaNQ4qGPjtr7TV.FeT2He/KgqxT68impZ9.H3XeyQAZ03W', 'John', 'Doe', 'InnovateLtd', true, 'CA_USER');


INSERT INTO certificate (can_sign, owner_id, valid_from, valid_to, id, parent_id, issuer_principal_name, serial_number, status, subject_principal_name, certificate_data, encrypted_private_key, wrapped_dek)
VALUES
    -- ROOT CERTIFICATES
    (true, 2, '2025-09-27 02:00:00', '2027-07-27 02:00:00', '3536a2a9-4dc4-4fe0-a0a2-ab5fc440f54a', NULL, 'CN=Test,O=Test,OU=MyUnit,C=RS,ST=Novi Sad,L=Serbia', '313798603550826436899931208603720497544', 'ACTIVE', 'CN=Test,O=Test,OU=MyUnit,C=RS,ST=Novi Sad,L=Serbia', '1442569', '1442570', '1442571'),
    (true, 2, '2025-09-27 02:00:00', '2027-07-27 02:00:00', '5c7dca77-c171-46a4-b23a-33f7edb177b9', NULL, 'CN=Test2,O=Test,OU=MyUnit,C=RS,ST=Novi Sad,L=Serbia', '77194048025825794944453812206568286232', 'ACTIVE', 'CN=Test2,O=Test,OU=MyUnit,C=RS,ST=Novi Sad,L=Serbia', '1442572', '1442573', '1442574'),
    (true, 2, '2025-09-27 02:00:00', '2027-07-27 02:00:00', '50778df3-9a82-4ba6-afc9-e8de0faa91d3', NULL, 'CN=TestBG,O=Test,OU=MyUnit,C=RS,ST=Belgrade,L=Serbia', '134020731896448649577810782252905363358', 'ACTIVE', 'CN=TestBG,O=Test,OU=MyUnit,C=RS,ST=Belgrade,L=Serbia', '1442575', '1442576', '1442577');
