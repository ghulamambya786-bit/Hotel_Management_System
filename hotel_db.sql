-- =====================================================
-- HOTEL MANAGEMENT SYSTEM - Complete Database
-- Run this file in MySQL Workbench or phpMyAdmin
-- =====================================================

CREATE DATABASE IF NOT EXISTS hotel_db;
USE hotel_db;

DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS rooms;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS users;

-- Users Table
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role ENUM('admin','user') DEFAULT 'user',
    full_name VARCHAR(100),
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Rooms Table
CREATE TABLE rooms (
    id INT AUTO_INCREMENT PRIMARY KEY,
    room_number VARCHAR(10) NOT NULL UNIQUE,
    room_type ENUM('Single','Double','Suite','Deluxe') NOT NULL,
    price_per_night DECIMAL(10,2) NOT NULL,
    status ENUM('Available','Booked','Maintenance') DEFAULT 'Available',
    description TEXT,
    floor VARCHAR(10),
    capacity INT DEFAULT 1
);

-- Customers Table
CREATE TABLE customers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    cnic VARCHAR(20) NOT NULL UNIQUE,
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT,
    city VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bookings Table
CREATE TABLE bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    booking_code VARCHAR(20) NOT NULL UNIQUE,
    customer_id INT NOT NULL,
    room_id INT NOT NULL,
    check_in DATE NOT NULL,
    check_out DATE NOT NULL,
    check_in_time VARCHAR(20),
    check_out_time VARCHAR(20),
    total_amount DECIMAL(10,2),
    status ENUM('Confirmed','Checked-In','Checked-Out','Cancelled','Pending') DEFAULT 'Confirmed',
    booked_by INT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
    FOREIGN KEY (booked_by) REFERENCES users(id)
);

-- =====================================================
-- DEFAULT DATA
-- =====================================================

INSERT INTO users (username,password,role,full_name,email) VALUES
('admin','admin123','admin','System Administrator','admin@hotel.com'),
('user','user123','user','Hotel Receptionist','user@hotel.com'),
('manager','manager123','admin','Hotel Manager','manager@hotel.com');

INSERT INTO rooms (room_number,room_type,price_per_night,status,description,floor,capacity) VALUES
('101','Single',3000.00,'Available','AC room with TV and WiFi','1st Floor',1),
('102','Single',3000.00,'Available','AC room with garden view','1st Floor',1),
('103','Single',3500.00,'Booked','Corner room with extra space','1st Floor',1),
('104','Single',3000.00,'Available','Standard single room','1st Floor',1),
('105','Single',3500.00,'Available','Single deluxe room','1st Floor',1),
('201','Double',5000.00,'Available','Double bed AC room with TV','2nd Floor',2),
('202','Double',5500.00,'Booked','Double room with balcony','2nd Floor',2),
('203','Double',5000.00,'Available','Standard double room','2nd Floor',2),
('204','Double',6000.00,'Maintenance','Double superior room','2nd Floor',2),
('205','Double',5500.00,'Available','Double with city view','2nd Floor',2),
('301','Suite',10000.00,'Available','Luxury suite with jacuzzi','3rd Floor',3),
('302','Suite',12000.00,'Available','Royal suite with living area','3rd Floor',4),
('303','Suite',11000.00,'Booked','Business suite with office','3rd Floor',2),
('401','Deluxe',8000.00,'Available','Deluxe room with sea view','4th Floor',2),
('402','Deluxe',8500.00,'Available','Premium deluxe room','4th Floor',2);

INSERT INTO customers (full_name,cnic,phone,email,address,city) VALUES
('Ali Khan','42101-1234567-1','0300-1234567','ali@gmail.com','House 5 Block A','Karachi'),
('Sara Ahmed','42201-7654321-2','0321-7654321','sara@gmail.com','Flat 3 Gulshan','Karachi'),
('Usman Raza','35201-1111111-3','0333-1111111','usman@yahoo.com','Street 4 Johar','Lahore'),
('Ayesha Malik','42301-2222222-4','0311-2222222','ayesha@gmail.com','Block 6 PECHS','Karachi'),
('Hamza Ali','35301-3333333-5','0345-3333333','hamza@gmail.com','Model Town','Lahore'),
('Noor Fatima','42101-4444444-6','0300-4444444','noor@gmail.com','DHA Phase 5','Karachi'),
('Daniyal Khan','35201-5555555-7','0321-5555555','daniyal@yahoo.com','Bahria Town','Lahore'),
('Zainab Ali','42201-6666666-8','0333-6666666','zainab@gmail.com','Clifton Block 2','Karachi'),
('Hassan Raza','42301-7777777-9','0311-7777777','hassan@gmail.com','Garden East','Karachi'),
('Maham Shahzad','35101-8888888-0','0345-8888888','maham@gmail.com','F-8 Sector','Islamabad');

INSERT INTO bookings (booking_code,customer_id,room_id,check_in,check_out,check_in_time,check_out_time,total_amount,status,booked_by) VALUES
('BKG001',1,3,'2024-05-20','2024-05-23','10:00 AM',NULL,10500.00,'Checked-In',1),
('BKG002',2,7,'2024-05-20','2024-05-22','11:15 AM',NULL,11000.00,'Checked-In',1),
('BKG003',3,3,'2024-05-20','2024-05-24','12:30 PM',NULL,14000.00,'Confirmed',1),
('BKG004',4,1,'2024-05-21','2024-05-23','01:45 PM',NULL,6000.00,'Confirmed',1),
('BKG005',5,2,'2024-05-21','2024-05-24','02:20 PM',NULL,10500.00,'Confirmed',1),
('BKG006',6,8,'2024-05-20','2024-05-22','09:30 AM','09:30 AM',11000.00,'Checked-Out',1),
('BKG007',7,9,'2024-05-20','2024-05-23','10:45 AM','10:45 AM',16500.00,'Checked-Out',1),
('BKG008',8,13,'2024-05-20','2024-05-23','11:00 AM','11:00 AM',33000.00,'Checked-Out',1),
('BKG009',9,11,'2024-05-20','2024-05-22',NULL,NULL,20000.00,'Confirmed',1),
('BKG010',10,12,'2024-05-21','2024-05-24',NULL,NULL,36000.00,'Pending',1);

SELECT 'Database setup complete!' AS Result;
SELECT COUNT(*) AS Total_Users FROM users;
SELECT COUNT(*) AS Total_Rooms FROM rooms;
SELECT COUNT(*) AS Total_Customers FROM customers;
SELECT COUNT(*) AS Total_Bookings FROM bookings;
