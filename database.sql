-- ============================================================
-- Hệ Thống Quản Lý Thuê Xe Máy - Database Schema
-- ============================================================

CREATE DATABASE java_motor_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE java_motor_db;

-- ----------------------------
-- Bảng Users
-- ----------------------------
CREATE TABLE Users (
    id         INT PRIMARY KEY AUTO_INCREMENT,
    username   VARCHAR(50)  UNIQUE NOT NULL,
    password   VARCHAR(255) NOT NULL,
    full_name  VARCHAR(100),
    phone      VARCHAR(20),
    role       VARCHAR(10)  DEFAULT 'User'
);

-- ----------------------------
-- Bảng Motorbikes
-- ----------------------------
CREATE TABLE Motorbikes (
    id            INT PRIMARY KEY AUTO_INCREMENT,
    license_plate VARCHAR(20)    UNIQUE NOT NULL,
    model         VARCHAR(100),
    brand         VARCHAR(100),
    price_per_day DECIMAL(12, 0),
    status        VARCHAR(20)    DEFAULT 'Sẵn sàng'
);

-- ----------------------------
-- Bảng Rentals
-- ----------------------------
CREATE TABLE Rentals (
    id                 INT PRIMARY KEY AUTO_INCREMENT,
    user_id            INT,
    motorbike_id       INT,
    rent_date          DATE,
    return_date        DATE,
    actual_return_date DATE NULL,
    total_price        DECIMAL(15, 0),
    status             VARCHAR(20) DEFAULT 'Chờ xử lý',
    FOREIGN KEY (user_id)      REFERENCES Users(id),
    FOREIGN KEY (motorbike_id) REFERENCES Motorbikes(id)
);

-- ----------------------------
-- Bảng Invoices
-- ----------------------------
CREATE TABLE Invoices (
    id            INT PRIMARY KEY AUTO_INCREMENT,
    rental_id     INT UNIQUE NOT NULL,
    issued_date   DATE           NOT NULL,
    days          INT            NOT NULL,
    price_per_day DECIMAL(12, 0) NOT NULL,
    total_price   DECIMAL(15, 0) NOT NULL,
    FOREIGN KEY (rental_id) REFERENCES Rentals(id)
);

-- ----------------------------
-- Dữ liệu mặc định
-- ----------------------------
INSERT INTO Users (username, password, full_name, phone, role) VALUES
('admin',      'admin123', 'Quản Trị Viên', '0900000000', 'Admin'),
('nguyenvana', '123456',   'Nguyễn Văn A',  '0901234567', 'User'),
('tranthib',   '123456',   'Trần Thị B',    '0912345678', 'User'),
('lehoanc',    '123456',   'Lê Hoàng C',    '0923456789', 'User'),
('phamthid',   '123456',   'Phạm Thị D',    '0934567890', 'User'),
('vuminhe',    '123456',   'Vũ Minh E',     '0945678901', 'User'),
('dangvanf',   '123456',   'Đặng Văn F',    '0956789012', 'User');

INSERT INTO Motorbikes (license_plate, model, brand, price_per_day, status) VALUES
('59A1-12345', 'Wave Alpha',  'Honda',  150000, 'Sẵn sàng'),
('59A1-67890', 'Exciter 155', 'Yamaha', 250000, 'Đang thuê'),
('59A1-11111', 'Air Blade',   'Honda',  200000, 'Sẵn sàng'),
('59A1-22222', 'SH 150i',     'Honda',  350000, 'Bảo trì'),
('59B2-33333', 'NVX 155',     'Yamaha', 280000, 'Đang thuê'),
('59B2-44444', 'Vision',      'Honda',  180000, 'Sẵn sàng'),
('59B2-55555', 'Vario 160',   'Honda',  220000, 'Sẵn sàng'),
('59C3-66666', 'Janus',       'Yamaha', 190000, 'Sẵn sàng');

-- ----------------------------
-- Dữ liệu mẫu Rentals
-- ----------------------------
INSERT INTO Rentals (user_id, motorbike_id, rent_date, return_date, total_price, status)
SELECT u.id, m.id, '2026-04-01', '2026-04-03', 500000, 'Đã thanh toán'
FROM Users u, Motorbikes m WHERE u.username = 'nguyenvana' AND m.license_plate = '59A1-12345';

INSERT INTO Rentals (user_id, motorbike_id, rent_date, return_date, total_price, status)
SELECT u.id, m.id, '2026-04-10', '2026-04-15', 1250000, 'Đã thanh toán'
FROM Users u, Motorbikes m WHERE u.username = 'tranthib' AND m.license_plate = '59A1-11111';

INSERT INTO Rentals (user_id, motorbike_id, rent_date, return_date, total_price, status)
SELECT u.id, m.id, '2026-05-01', '2026-05-04', 750000, 'Đã thanh toán'
FROM Users u, Motorbikes m WHERE u.username = 'lehoanc' AND m.license_plate = '59B2-44444';

INSERT INTO Rentals (user_id, motorbike_id, rent_date, return_date, total_price, status)
SELECT u.id, m.id, '2026-05-10', '2026-05-13', 840000, 'Chờ xử lý'
FROM Users u, Motorbikes m WHERE u.username = 'nguyenvana' AND m.license_plate = '59A1-67890';

INSERT INTO Rentals (user_id, motorbike_id, rent_date, return_date, total_price, status)
SELECT u.id, m.id, '2026-05-11', '2026-05-15', 1120000, 'Chờ xử lý'
FROM Users u, Motorbikes m WHERE u.username = 'phamthid' AND m.license_plate = '59B2-33333';

INSERT INTO Rentals (user_id, motorbike_id, rent_date, return_date, total_price, status)
SELECT u.id, m.id, '2026-04-20', '2026-04-22', 380000, 'Đã hủy'
FROM Users u, Motorbikes m WHERE u.username = 'vuminhe' AND m.license_plate = '59B2-55555';

-- ----------------------------
-- Hóa đơn cho các đơn đã thanh toán
-- ----------------------------
INSERT INTO Invoices (rental_id, issued_date, days, price_per_day, total_price)
SELECT r.id,
       r.return_date,
       DATEDIFF(r.return_date, r.rent_date),
       m.price_per_day,
       r.total_price
FROM Rentals r
JOIN Motorbikes m ON r.motorbike_id = m.id
WHERE r.status = 'Đã thanh toán';
