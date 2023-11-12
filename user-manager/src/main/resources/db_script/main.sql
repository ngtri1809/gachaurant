-- Database name
CREATE DATABASE user_manager;

USE user_manager;

GRANT ALL PRIVILEGES ON user_manager.* TO 'tri'@'localhost' IDENTIFIED BY 'tri123';

--user table structure:
CREATE TABLE users(
    user_id INT NOT NULL AUTO_INCREMENT,
    full_name VARCHAR(50) NOT NULL,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    PRIMARY KEY(user_id)
);

