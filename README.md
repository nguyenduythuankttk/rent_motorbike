# 🏍️ Hệ Thống Quản Lý Thuê Xe Máy (Motorbike Rental Management)

## 👥 Thông Tin Nhóm

**Môn học:** Lập trình Java  
**Tên dự án:** Hệ thống quản lý thuê xe máy

| STT | Họ và Tên | MSSV | Phụ trách |
| :---: | :--- | :--- | :--- |
| 1 | Nguyễn Duy Thuận | 10240401 | Chức năng Admin |
| 2 | Lương Thanh Nhật | 102240386 | Chức năng User |
| 3 | Nguyễn Đức Quang | | Thiết kế giao diện & Database |

---

## 📌 Mô Tả Dự Án

Chương trình quản lý thuê xe máy xây dựng bằng **Java Swing** và **MySQL**, hỗ trợ 2 vai trò: **Admin** và **User**. Sau khi đăng nhập, hệ thống tự động chuyển hướng đến giao diện phù hợp theo vai trò.

**Công nghệ:** Java (JDK 8+) · Java Swing & AWT · MySQL · JDBC

---

## ✅ Chức Năng Chương Trình

### 🔐 Xác Thực Chung (Cả 2 vai trò)
- Đăng nhập bằng username/password, kiểm tra từ CSDL
- Đăng ký tài khoản mới với validation đầy đủ
- Quản lý phiên đăng nhập qua `Session` class

### 👑 Chức Năng Admin
- **Quản lý Xe:** Xem danh sách, thêm, sửa, xóa xe; tìm kiếm theo tên/biển số
- **Quản lý Đơn Thuê:** Xem tất cả đơn, lọc trạng thái, xác nhận thanh toán, hủy đơn
- **Quản lý Người Dùng:** Xem danh sách tài khoản, xóa tài khoản
- **Thống kê:** Tổng doanh thu, tổng đơn, phân loại theo trạng thái, xe sẵn sàng
- **Xuất báo cáo:** Xuất thống kê ra file CSV

### 🙋 Chức Năng User
- **Xem xe:** Danh sách xe sẵn có, tìm kiếm, lọc theo giá
- **Thuê xe:** Chọn xe, chọn ngày nhận/trả, tự động tính tổng tiền
- **Lịch sử thuê:** Xem lại các đơn thuê cá nhân và trạng thái

---

## 👤 Đóng Góp Của Từng Thành Viên

---

#### Nguyễn Duy Thuận (10240401) — Phụ trách: Admin

- Xây dựng `AdminDashboard`: sidebar điều hướng, chuyển tab bằng `CardLayout`
- `ManageMotorbikesPanel`: bảng danh sách xe, thêm/sửa/xóa, tìm kiếm, `MotorbikeDialog`
- `ManageRentalsPanel`: xem đơn thuê, lọc trạng thái, xác nhận thanh toán, hủy đơn
- `ManageUsersPanel`: danh sách người dùng, xóa tài khoản (bảo vệ tài khoản Admin)
- `StatisticsPanel`: 6 chỉ số thời gian thực, nút làm mới, xuất file CSV

---

#### Lương Thanh Nhật (102240386) — Phụ trách: User

- Xây dựng `UserDashboard`: sidebar điều hướng, chuyển tab bằng `CardLayout`
- `ViewMotorbikesPanel`: danh sách xe sẵn có, tìm kiếm theo từ khóa, lọc theo giá
- `RentMotorbikePanel`: form đặt xe, chọn ngày nhận/trả, tự động tính số ngày & tổng tiền
- `RentalHistoryPanel`: lịch sử thuê cá nhân theo phiên đăng nhập, màu trạng thái đơn
- Tích hợp `Session` để lấy thông tin người dùng hiện tại cho các thao tác

---

#### Nguyễn Đức Quang — Phụ trách: Thiết kế giao diện & Database

- Thiết kế schema CSDL (`database.sql`): 3 bảng `Users`, `Motorbikes`, `Rentals`, ràng buộc FK, dữ liệu mẫu
- `DBConnection.java`: cấu hình kết nối JDBC MySQL, xử lý exception
- Các lớp Model: `User.java`, `Motorbike.java`, `Rental.java` (POJO)
- `UIStyles.java`: design system — bảng màu, font, `ModernButton`, `SidebarButton`, `RoundedPanel`
- `LoginFrame.java`: màn hình đăng nhập split-layout gradient, routing theo role
- `RegisterDialog.java`: form đăng ký với validation

---

## 🗄️ Cơ Sở Dữ Liệu

### Bảng `Users`
| Cột | Kiểu | Mô tả |
| :--- | :--- | :--- |
| `id` | INT (PK) | ID tự tăng |
| `username` | VARCHAR(50) | Tên đăng nhập (UNIQUE) |
| `password` | VARCHAR(255) | Mật khẩu |
| `full_name` | VARCHAR(100) | Họ và tên |
| `phone` | VARCHAR(20) | Số điện thoại |
| `role` | VARCHAR(10) | `Admin` hoặc `User` |

### Bảng `Motorbikes`
| Cột | Kiểu | Mô tả |
| :--- | :--- | :--- |
| `id` | INT (PK) | ID tự tăng |
| `license_plate` | VARCHAR(20) | Biển số xe (UNIQUE) |
| `model` | VARCHAR(100) | Tên xe |
| `brand` | VARCHAR(100) | Hãng xe |
| `price_per_day` | DECIMAL(12,0) | Giá thuê / ngày |
| `status` | VARCHAR(20) | `Sẵn sàng` / `Đang thuê` / `Bảo trì` |

### Bảng `Rentals`
| Cột | Kiểu | Mô tả |
| :--- | :--- | :--- |
| `id` | INT (PK) | ID tự tăng |
| `user_id` | INT (FK) | Người thuê → `Users` |
| `motorbike_id` | INT (FK) | Xe thuê → `Motorbikes` |
| `rent_date` | DATE | Ngày nhận xe |
| `return_date` | DATE | Ngày trả xe |
| `total_price` | DECIMAL(15,0) | Tổng tiền |
| `status` | VARCHAR(20) | `Chờ xử lý` / `Đã thanh toán` / `Đã hủy` |

---

## 🚀 Cài Đặt & Chạy

```bash
# 1. Tạo database
mysql -u root -p < database.sql

# 2. Cập nhật mật khẩu MySQL trong src/db/DBConnection.java

# 3. Biên dịch & chạy
chmod +x compile.sh
./compile.sh
```

**Tài khoản mặc định:** `admin` / `admin123` (Admin) · `nguyenvana` / `123456` (User)
