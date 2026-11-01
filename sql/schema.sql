-- ============================================================
-- HaUI Course App - Database Schema & Seed Data
-- SQL Server (HaUICourseDB)
-- ============================================================

USE master;
GO

IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'HaUICourseDB')
BEGIN
    CREATE DATABASE HaUICourseDB;
END
GO

USE HaUICourseDB;
GO

-- Xóa các bảng cũ theo thứ tự khóa ngoại để khởi tạo sạch chuẩn schema
IF OBJECT_ID('DANG_KY', 'U') IS NOT NULL DROP TABLE DANG_KY;
IF OBJECT_ID('CHI_TIET_DON_HANG', 'U') IS NOT NULL DROP TABLE CHI_TIET_DON_HANG;
IF OBJECT_ID('DON_HANG', 'U') IS NOT NULL DROP TABLE DON_HANG;
IF OBJECT_ID('MA_GIAM_GIA', 'U') IS NOT NULL DROP TABLE MA_GIAM_GIA;
IF OBJECT_ID('CHI_TIET_GIO_HANG', 'U') IS NOT NULL DROP TABLE CHI_TIET_GIO_HANG;
IF OBJECT_ID('GIO_HANG', 'U') IS NOT NULL DROP TABLE GIO_HANG;
IF OBJECT_ID('TAI_KHOAN', 'U') IS NOT NULL DROP TABLE TAI_KHOAN;
IF OBJECT_ID('KHOA_HOC', 'U') IS NOT NULL DROP TABLE KHOA_HOC;
IF OBJECT_ID('DANH_MUC', 'U') IS NOT NULL DROP TABLE DANH_MUC;
GO

-- ========== 1. DANH MUC ==========
CREATE TABLE DANH_MUC (
    MaDanhMuc   NVARCHAR(30)  PRIMARY KEY,
    TenDanhMuc  NVARCHAR(100) NOT NULL,
    MoTa        NVARCHAR(255),
    Icon        NVARCHAR(50)
);
GO

-- ========== 2. KHOA HOC ==========
CREATE TABLE KHOA_HOC (
    MaKhoaHoc    NVARCHAR(15)   PRIMARY KEY,
    MaDanhMuc    NVARCHAR(30)   REFERENCES DANH_MUC(MaDanhMuc),
    TenKhoaHoc   NVARCHAR(200)  NOT NULL,
    MoTa         NVARCHAR(MAX),
    GiangVien    NVARCHAR(100),
    HocPhi       DECIMAL(12,0)  DEFAULT 0,
    GiaKhuyenMai DECIMAL(12,0)  DEFAULT 0,
    ThoiLuong    NVARCHAR(50),
    CapDo        NVARCHAR(50),
    TrangThai    NVARCHAR(20)   DEFAULT N'Đang mở',
    AnhKhoaHoc   NVARCHAR(255),
    NgayTao      DATETIME       DEFAULT GETDATE()
);
GO

-- ========== 3. TAI KHOAN ==========
CREATE TABLE TAI_KHOAN (
    MaKhachHang NVARCHAR(15)  PRIMARY KEY,
    HoTen       NVARCHAR(100) NOT NULL,
    Email       NVARCHAR(100) UNIQUE NOT NULL,
    MatKhau     NVARCHAR(255) NOT NULL,
    SoDienThoai NVARCHAR(15),
    MaSinhVien  NVARCHAR(20),
    TrangThai   NVARCHAR(20)  DEFAULT N'Hoạt động',
    NgayTao     DATETIME      DEFAULT GETDATE()
);
GO

-- ========== 4. GIO HANG ==========
CREATE TABLE GIO_HANG (
    MaGioHang   NVARCHAR(20)  PRIMARY KEY,
    MaKhachHang NVARCHAR(15)  REFERENCES TAI_KHOAN(MaKhachHang),
    NgayCapNhat DATETIME      DEFAULT GETDATE()
);
GO

-- ========== 5. CHI TIET GIO HANG ==========
CREATE TABLE CHI_TIET_GIO_HANG (
    MaGioHang   NVARCHAR(20)  REFERENCES GIO_HANG(MaGioHang),
    MaKhoaHoc   NVARCHAR(15)  REFERENCES KHOA_HOC(MaKhoaHoc),
    DonGia      DECIMAL(12,0),
    NgayThem    DATETIME      DEFAULT GETDATE(),
    PRIMARY KEY (MaGioHang, MaKhoaHoc)
);
GO

-- ========== 6. MA GIAM GIA ==========
CREATE TABLE MA_GIAM_GIA (
    MaVoucher    NVARCHAR(20)  PRIMARY KEY,
    PhanTramGiam INT           DEFAULT 0,
    NgayHetHan   DATE,
    TrangThai    NVARCHAR(20)  DEFAULT N'Hoạt động'
);
GO

-- ========== 7. DON HANG ==========
CREATE TABLE DON_HANG (
    MaDonHang       NVARCHAR(25)  PRIMARY KEY,
    MaKhachHang     NVARCHAR(15)  REFERENCES TAI_KHOAN(MaKhachHang),
    MaVoucher       NVARCHAR(20)   NULL REFERENCES MA_GIAM_GIA(MaVoucher),
    NgayDat         DATETIME      DEFAULT GETDATE(),
    TongTienTamTinh DECIMAL(12,0),
    SoTienGiam      DECIMAL(12,0) DEFAULT 0,
    TongThanhToan   DECIMAL(12,0),
    PhuongThucTT    NVARCHAR(100),
    TrangThai       NVARCHAR(30)  DEFAULT N'Đã thanh toán'
);
GO

-- ========== 8. CHI TIET DON HANG ==========
CREATE TABLE CHI_TIET_DON_HANG (
    MaDonHang   NVARCHAR(25)  REFERENCES DON_HANG(MaDonHang),
    MaKhoaHoc   NVARCHAR(15)  REFERENCES KHOA_HOC(MaKhoaHoc),
    GiaMua      DECIMAL(12,0),
    PRIMARY KEY (MaDonHang, MaKhoaHoc)
);
GO

-- ========== 9. DANG KY KHOA HOC ==========
CREATE TABLE DANG_KY (
    MaDangKy    NVARCHAR(25)  PRIMARY KEY,
    MaKhachHang NVARCHAR(15)  REFERENCES TAI_KHOAN(MaKhachHang),
    MaKhoaHoc   NVARCHAR(15)  REFERENCES KHOA_HOC(MaKhoaHoc),
    MaDonHang   NVARCHAR(25)  NULL REFERENCES DON_HANG(MaDonHang),
    NgayDangKy  DATETIME      DEFAULT GETDATE(),
    TrangThai   NVARCHAR(30)  DEFAULT N'Đã thanh toán'
);
GO

-- ============================================================
-- DU LIEU MAU CHUAN FIGMA
-- ============================================================

-- 1. Danh mục
INSERT INTO DANH_MUC (MaDanhMuc, TenDanhMuc, MoTa, Icon) VALUES
(N'Java',        N'Java',        N'Nền tảng & hướng đối tượng',    N'Java'),
(N'C++',         N'C++',         N'Lập trình & giải thuật',         N'C++'),
(N'Python',      N'Python',      N'Ứng dụng & tự động hóa',         N'Python'),
(N'HTML/CSS',    N'HTML/CSS',    N'Thiết kế giao diện web',         N'HTML/CSS'),
(N'Spring Boot', N'Spring Boot', N'Phát triển ứng dụng backend',    N'Spring Boot');
GO

-- 2. Khóa học
INSERT INTO KHOA_HOC (MaKhoaHoc, MaDanhMuc, TenKhoaHoc, MoTa, GiangVien, HocPhi, GiaKhuyenMai, ThoiLuong, CapDo, TrangThai, AnhKhoaHoc, NgayTao) VALUES
('KH001', N'Java', N'Java từ cơ bản đến hướng đối tượng',
 N'Khóa học giúp bạn xây dựng nền tảng lập trình Java vững chắc, từ cú pháp cơ bản đến tư duy hướng đối tượng. Mỗi chương kết hợp video hướng dẫn, ví dụ minh họa và bài tập để bạn áp dụng ngay kiến thức đã học.',
 N'Nguyễn Minh Tuấn', 499000, 0, N'42 bài học • 28 giờ', N'Cơ bản', N'Đang mở', N'Java', DATEADD(DAY, -1, GETDATE())),

('KH002', N'C++', N'Lập trình C++ cho người mới bắt đầu',
 N'Khóa học C++ từ đầu: biến, kiểu dữ liệu, hàm, con trỏ, mảng và cấu trúc dữ liệu cơ bản. Rèn luyện tư duy thuật toán vững vàng.',
 N'Trần Hoàng Nam', 399000, 0, N'36 bài học • 24 giờ', N'Cơ bản', N'Đang mở', N'C++', DATEADD(DAY, -2, GETDATE())),

('KH003', N'Python', N'Python cơ bản và ứng dụng thực tế',
 N'Lập trình Python hiện đại từ số 0, thao tác dữ liệu, xử lý file, tự động hóa tác vụ văn phòng và làm quen với thư viện phân tích dữ liệu.',
 N'Lê Thu Hà', 449000, 0, N'40 bài học • 26 giờ', N'Cơ bản', N'Đang mở', N'Python', DATEADD(DAY, -3, GETDATE())),

('KH004', N'HTML/CSS', N'Thiết kế website với HTML và CSS',
 N'Xây dựng giao diện web chuẩn responsive, Flexbox, CSS Grid, animation mượt mà, tối ưu hiển thị trên mọi thiết bị.',
 N'Phạm Quang Huy', 349000, 0, N'32 bài học • 20 giờ', N'Cơ bản', N'Đang mở', N'HTML/CSS', DATEADD(DAY, -4, GETDATE())),

('KH005', N'Spring Boot', N'Xây dựng REST API với Spring Boot',
 N'Phát triển ứng dụng Backend chuyên nghiệp với Spring Boot, Spring Data JPA, JWT Authentication, Swagger và SQL Server.',
 N'Nguyễn Minh Tuấn', 699000, 0, N'48 bài học • 35 giờ', N'Trung cấp', N'Đang mở', N'Spring Boot', DATEADD(DAY, -5, GETDATE())),

('KH006', N'C++', N'Cấu trúc dữ liệu và giải thuật C++',
 N'Nắm vững danh sách liên kết, ngăn xếp (Stack), hàng đợi (Queue), cây nhị phân (Binary Tree), đồ thị và các giải thuật tìm kiếm, sắp xếp tối ưu.',
 N'Trần Hoàng Nam', 599000, 0, N'45 bài học • 30 giờ', N'Trung cấp', N'Đang mở', N'C++', DATEADD(DAY, -6, GETDATE())),

('KH007', N'Java', N'Lập trình Java Swing Desktop App',
 N'Xây dựng phần mềm quản lý trên máy tính với Java Swing, kết nối CSDL SQL Server qua JDBC, thiết kế UI hiện đại.',
 N'Phạm Thị D', 550000, 0, N'35 bài học • 25 giờ', N'Trung cấp', N'Đang mở', N'Java', DATEADD(DAY, -7, GETDATE())),

('KH008', N'Python', N'Machine Learning căn bản với Python',
 N'Nhập môn Trí tuệ nhân tạo & Máy học: Hồi quy tuyến tính, Phân loại, Gom cụm dữ liệu với NumPy, Pandas, Scikit-Learn.',
 N'Bùi Thị H', 799000, 0, N'50 bài học • 40 giờ', N'Nâng cao', N'Đang mở', N'Python', DATEADD(DAY, -8, GETDATE())),

('KH009', N'HTML/CSS', N'JavaScript Toàn Tập & Thực Chiến',
 N'Làm chủ JavaScript từ cơ bản đến nâng cao: ES6+, DOM manipulation, Event loop, Async/Await, Fetch API.',
 N'Trần Thị B', 520000, 0, N'38 bài học • 28 giờ', N'Cơ bản', N'Đang mở', N'HTML/CSS', DATEADD(DAY, -9, GETDATE())),

('KH010', N'Spring Boot', N'Microservices Architecture với Spring Cloud',
 N'Xây dựng hệ thống phân tán chịu tải cao: Service Discovery, API Gateway, Circuit Breaker, Config Server.',
 N'Vũ Thị F', 950000, 0, N'55 bài học • 45 giờ', N'Nâng cao', N'Đang mở', N'Spring Boot', DATEADD(DAY, -10, GETDATE()));
GO

-- 3. Tài khoản mẫu
INSERT INTO TAI_KHOAN (MaKhachHang, HoTen, Email, MatKhau, SoDienThoai, MaSinhVien, TrangThai) VALUES
('KH001', N'Nguyễn Văn An',  'an.nguyen@email.com',    '123456', '0912345678', '2021601234',    N'Hoạt động'),
('KH002', N'Trần Thị Mai',  'mai.tran@email.com',     '123456', '0912345679', '2021605678',    N'Hoạt động'),
('KH003', N'Sinh viên HaUI', 'sv@haui.edu.vn',         '123456', '0901234567', '2022600001',    N'Hoạt động');
GO

-- 4. Giỏ hàng mẫu
INSERT INTO GIO_HANG (MaGioHang, MaKhachHang) VALUES
('GH001', 'KH001'),
('GH002', 'KH002');
GO

INSERT INTO CHI_TIET_GIO_HANG (MaGioHang, MaKhoaHoc, DonGia) VALUES
('GH001', 'KH001', 499000),
('GH001', 'KH003', 449000),
('GH002', 'KH001', 499000),
('GH002', 'KH003', 449000);
GO

-- 5. Mã giảm giá
INSERT INTO MA_GIAM_GIA (MaVoucher, PhanTramGiam, NgayHetHan, TrangThai) VALUES
('HAUI10', 10, '2028-12-31', N'Hoạt động'),
('HAUI20', 20, '2028-12-31', N'Hoạt động'),
('HAUI50', 50, '2028-06-30', N'Hoạt động'),
('EXPIRED', 30, '2020-01-01', N'Hết hạn');
GO

-- 6. Đơn hàng & Đăng ký mẫu
INSERT INTO DON_HANG (MaDonHang, MaKhachHang, MaVoucher, TongTienTamTinh, SoTienGiam, TongThanhToan, PhuongThucTT, TrangThai) VALUES
('DH001', 'KH001', NULL, 349000, 0, 349000, N'Chuyển khoản ngân hàng', N'Đã thanh toán');
GO

INSERT INTO CHI_TIET_DON_HANG (MaDonHang, MaKhoaHoc, GiaMua) VALUES
('DH001', 'KH004', 349000);
GO

INSERT INTO DANG_KY (MaDangKy, MaKhachHang, MaKhoaHoc, MaDonHang, TrangThai) VALUES
('DK001', 'KH001', 'KH004', 'DH001', N'Đã thanh toán');
GO

PRINT N'✅ Database HaUICourseDB initialized successfully!';
GO
