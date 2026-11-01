# 🎓 HaUI Course App - Hệ Thống Đăng Ký & Quản Lý Khóa Học Trực Tuyến

> **Dự án ứng dụng Desktop Java Swing** dành cho sinh viên Trường Đại học Công nghiệp Hà Nội (HaUI).  
> Giao diện được thiết kế chuẩn **theo bản vẽ**, tích hợp hệ quản trị cơ sở dữ liệu **Microsoft SQL Server**, mô hình kiến trúc phân lớp chuẩn **DAO / MVC**.

---

## 📌 Mục Lục
- [Giới Thiệu Tổng Quan](#-giới-thiệu-tổng-quan)
- [Tính Năng Nổi Bật](#-tính-năng-nổi-bật)
- [Công Nghệ & Thư Viện Sử Dụng](#-công-nghệ--thư-viện-sử-dụng)
- [Cấu Trúc Thư Mục Dự Án](#-cấu-trúc-thư-mục-dự-án)
- [Tài Khoản & Dữ Liệu Mẫu](#-tài-khoản--dữ-liệu-mẫu)
- [Hướng Dẫn Cài Đặt & Khởi Chạy](#-hướng-dẫn-cài-đặt--khởi-chạy)
  - [1. Khởi chạy nhanh với run.bat (Khuyên dùng)](#1-khởi-chạy-nhanh-với-runbat-khuyên-dùng)
  - [2. Khởi tạo Cơ sở dữ liệu thủ công](#2-khởi-tạo-cơ-sở-dữ-liệu-thủ-công)
  - [3. Chạy ứng dụng bằng dòng lệnh (CLI)](#3-chạy-ứng-dụng-bằng-dòng-lệnh-cli)
  - [4. Chạy ứng dụng trên IDE (VS Code / IntelliJ IDEA / Eclipse)](#4-chạy-ứng-dụng-trên-ide)
- [Thiết Kế Giao Diện Chuẩn Figma](#-thiết-kế-giao-diện-chuẩn-figma)
- [Bản Quyền & Tác Giả](#-bản-quyền--tác-giả)

---

## 🌟 Giới Thiệu Tổng Quan

**HaUI Course App** là ứng dụng hỗ trợ sinh viên khám phá, đăng ký và theo dõi tiến độ các khóa học công nghệ thông tin (Java, C++, Python, HTML/CSS, Spring Boot,...). Ứng dụng cung cấp trải nghiệm mượt mà, trực quan với màu sắc nhận diện thương hiệu đỏ HaUI đặc trưng (`#BA1C1C` / `#D32F2F`), bố cục thẻ thông minh và cơ chế đồng bộ cơ sở dữ liệu theo thời gian thực.

---

## 🚀 Tính Năng Nổi Bật

### 1. 🔐 Xác thực người dùng (Auth)
- **Mặc định khởi động tại màn hình Đăng nhập**: Tự động chuyển đến màn hình Đăng nhập khi mở ứng dụng.
- **Form Đăng nhập chuẩn Figma**: Thiết kế cân đối hai khối (Khối giới thiệu thương hiệu HaUI kèm logo + Khối form đăng nhập hiện đại).
- **Đăng ký tài khoản mới**: Kiểm tra trùng email, mã sinh viên, tự động băm mật khẩu và lưu vào SQL Server.
- **Quản lý phiên đăng nhập (`UserSession`)**: Duy trì trạng thái người dùng xuyên suốt toàn bộ ứng dụng bằng mô hình Singleton.

### 2. 🏠 Trang chủ sinh động (Home)
- Banner Hero giới thiệu các chương trình đào tạo chuẩn khoa CNTT - HaUI.
- Thống kê nhanh: Số lượng khóa học, giảng viên, sinh viên đồng hành.
- Danh mục phân loại: *Java*, *C++*, *Python*, *HTML/CSS*, *Spring Boot*.
- Danh sách khóa học mới và nổi bật nhất với hình ảnh, giá học phí và nút thao tác nhanh.

### 3. 📚 Khám phá & Tìm kiếm Khóa học (Courses)
- Danh sách khóa học phong phú, hiển thị dưới dạng card trực quan.
- Bộ lọc khóa học theo danh mục ngôn ngữ / công nghệ.
- Thanh tìm kiếm theo từ khóa tên khóa học, giảng viên phụ trách.
- Xem thông tin tổng quan: Giảng viên, thời lượng video, số bài giảng, cấp độ (Cơ bản / Trung cấp / Nâng cao).

### 4. 🔍 Chi tiết khóa học (Course Detail)
- Mô tả chi tiết nội dung, chuẩn đầu ra và lộ trình học từng phần.
- Nút **"Thêm vào giỏ hàng"** và **"Đăng ký ngay"** liên kết trực tiếp với giỏ hàng.

### 5. 🛒 Quản lý Giỏ hàng (Cart)
- Danh sách các khóa học đã chọn mua cùng đơn giá thực tế.
- Xóa từng khóa học hoặc xóa toàn bộ giỏ hàng.
- Tự động tính tổng tiền tạm tính.
- Nút chuyển tiếp nhanh sang màn hình **Thanh toán**.

### 6. 💳 Thanh toán & Voucher giảm giá (Checkout)
- Tóm tắt đơn hàng chi tiết: Tạm tính, mã giảm giá, tổng thanh toán.
- **Hệ thống Voucher sinh viên**: Hỗ trợ nhập mã voucher giảm giá (Ví dụ: `HAUI10`, `HAUI20`, `HAUI50`).
- Phương thức thanh toán linh hoạt: Chuyển khoản QR ngân hàng, Ví điện tử.
- Sau khi xác nhận thanh toán thành công, hệ thống tự động:
  - Tạo bản ghi đơn hàng trong bảng `DON_HANG`.
  - Lưu chi tiết trong bảng `CHI_TIET_DON_HANG`.
  - Cập nhật bảng `DANG_KY` để mở khóa học cho sinh viên.
  - Xóa các khóa học đã mua ra khỏi giỏ hàng.

### 7. 🎓 Khóa học của tôi (My Courses)
- Quản lý các khóa học sinh viên đã thanh toán/đăng ký thành công.
- Hiển thị tiến độ học tập (%) và nút **"Vào học ngay"**.

### 8. 👤 Quản lý Hồ sơ cá nhân (Profile)
- Xem thông tin sinh viên: Họ và tên, Mã sinh viên, Email, Số điện thoại.
- Cập nhật thông tin cá nhân.
- Đổi mật khẩu tài khoản.
- Nút Đăng xuất an toàn đưa về màn hình Đăng nhập.

### 9. 🛡️ Cơ chế CSDL Linh hoạt (Dual Mode)
- **SQL Server Mode (Mặc định)**: Kết nối trực tiếp CSDL Microsoft SQL Server `HaUICourseDB`.
- **Tự động nạp JDBC Driver**: Tự động nhận diện file JAR trong thư mục `lib/` mà không bắt buộc cấu hình biến môi trường thủ công.
- **Graceful Fallback (Mock Mode)**: Tự động chuyển sang dữ liệu mẫu nếu SQL Server chưa bật, giúp trải nghiệm demo không bao giờ bị gián đoạn.

---

## 🛠️ Công Nghệ & Thư Viện Sử Dụng

| Thành phần | Công nghệ / Thư viện | Ghi chú |
| :--- | :--- | :--- |
| **Ngôn ngữ** | Java SE (JDK 11 / 17 / 21+) | Hướng đối tượng chuẩn |
| **Giao diện (GUI)** | Java Swing / AWT / Graphics2D | Khử răng cưa (Antialiasing), Flat Modern UI |
| **Cơ sở dữ liệu** | Microsoft SQL Server (2012 - 2022) | CSDL quan hệ `HaUICourseDB` |
| **JDBC Driver** | `mssql-jdbc-12.4.2.jre11.jar` | Thư viện kết nối SQL Server chính thức |
| **Kiến trúc** | DAO (Data Access Object) & MVC | Tách bạch UI, Business Logic và Database |

---

## 📁 Cấu Trúc Thư Mục Dự Án

```text
avc/
├── .vscode/                     # Cấu hình workspace Visual Studio Code
│   └── settings.json
├── bin/                         # Thư mục chứa các file .class đã biên dịch
├── lib/                         # Thư viện ngoài và tài nguyên đồ họa
│   ├── haui_logo.png            # Logo biểu trưng Đại học Công nghiệp Hà Nội
│   └── mssql-jdbc-12.4.2.jre11.jar # Driver kết nối Microsoft SQL Server
├── sql/
│   └── schema.sql               # Kịch bản DDL & DML khởi tạo CSDL HaUICourseDB
├── src/                         # Toàn bộ mã nguồn Java
│   ├── Main.java                # Điểm khởi chạy ứng dụng (Entry Point)
│   ├── dao/                     # Tầng truy xuất dữ liệu (Data Access Object)
│   │   ├── DBConnection.java    # Quản lý kết nối SQL Server & nạp Driver
│   │   ├── DangKyDAO.java       # Xử lý đăng ký khóa học
│   │   ├── DonHangDAO.java      # Xử lý đơn hàng & chi tiết đơn hàng
│   │   ├── GioHangDAO.java      # Xử lý thêm, xóa, lấy giỏ hàng
│   │   ├── KhoaHocDAO.java      # Xử lý truy vấn danh sách & chi tiết khóa học
│   │   └── TaiKhoanDAO.java     # Xử lý đăng nhập, đăng ký, cập nhật tài khoản
│   ├── model/                   # Tầng thực thể dữ liệu (POJO)
│   │   ├── ChiTietGioHang.java
│   │   ├── DangKy.java
│   │   ├── DonHang.java
│   │   ├── GioHang.java
│   │   ├── KhoaHoc.java
│   │   └── TaiKhoan.java
│   ├── session/                 # Quản lý phiên làm việc người dùng
│   │   └── UserSession.java     # Singleton lưu tài khoản đang đăng nhập
│   ├── ui/                      # Giao diện người dùng
│   │   └── MainFrame.java       # Điều phối màn hình chuẩn Figma 1:1
│   └── utils/                   # Tiện ích định dạng tiền tệ, ngày tháng
│       └── AppUtils.java
├── run.bat                      # File khởi chạy tự động (kiểm tra CSDL & chạy app)
└── README.md                    # Tài liệu hướng dẫn dự án (File này)
```

---

## 🔑 Tài Khoản & Dữ Liệu Mẫu

### 1. Tài khoản đăng nhập sẵn có trong CSDL

| Họ và tên | Mã sinh viên | Email đăng nhập | Mật khẩu |
| :--- | :--- | :--- | :--- |
| **Nguyễn Văn An** | `2021601234` | `an.nguyen@email.com` | `123456` |

> *Lưu ý: Bạn cũng có thể nhấn vào nút **"Đăng ký ngay"** trên giao diện để tạo một tài khoản mới bất kỳ.*

### 2. Mã giảm giá (Voucher) áp dụng khi thanh toán

| Mã Voucher | Tỷ lệ giảm | Hạn sử dụng | Trạng thái |
| :--- | :--- | :--- | :--- |
| `HAUI10` | **10%** | 31/12/2028 | Đang hoạt động |
| `HAUI20` | **20%** | 31/12/2028 | Đang hoạt động |
| `HAUI50` | **50%** | 30/06/2028 | Đang hoạt động |

---

## 💻 Hướng Dẫn Cài Đặt & Khởi Chạy

### 1. Khởi chạy nhanh với `run.bat` (Khuyên dùng)

Chỉ cần nhấp đúp vào file `run.bat` hoặc mở terminal tại thư mục `avc` và gõ:
```cmd
run.bat
```
- **Tự động kiểm tra CSDL**: Kiểm tra cơ sở dữ liệu `HaUICourseDB`. Nếu chưa có, script tự động nạp file `sql\schema.sql`. Nếu đã tồn tại thì tự động bỏ qua.
- **Tự động biên dịch**: Tự động biên dịch mã nguồn Java nếu thư mục `bin` chưa có class.
- **Khởi chạy ứng dụng**: Khởi chạy ứng dụng HaUI Course App với đầy đủ thư viện.

---

### 2. Khởi tạo Cơ sở dữ liệu thủ công

1. Mở **SQL Server Management Studio (SSMS)** hoặc **Azure Data Studio**.
2. Kết nối tới SQL Server cục bộ (`localhost` hoặc `127.0.0.1:1433`).
3. Mở file [schema.sql](file:///c:/Users/Administrator/Desktop/Full%20Proj/avc/sql/schema.sql) trong thư mục `sql/`.
4. Nhấn **Execute (F5)** để chạy kịch bản tạo CSDL `HaUICourseDB` cùng toàn bộ bảng và dữ liệu mẫu.
5. Kiểm tra thông tin kết nối trong [DBConnection.java](file:///c:/Users/Administrator/Desktop/Full%20Proj/avc/src/dao/DBConnection.java):
   ```java
   private static final String SERVER   = "localhost";
   private static final String PORT     = "1433";
   private static final String DATABASE = "HaUICourseDB";
   private static final String USERNAME = "sa";
   private static final String PASSWORD = "123456";
   ```

---

### 3. Chạy ứng dụng bằng dòng lệnh (CLI)

Mở PowerShell hoặc Command Prompt tại thư mục gốc `avc` và thực hiện:

#### Bước 3.1: Biên dịch mã nguồn Java
```powershell
javac -encoding UTF-8 -cp "lib/*" -d bin src/*.java src/dao/*.java src/model/*.java src/session/*.java src/ui/*.java src/utils/*.java
```

#### Bước 3.2: Khởi chạy ứng dụng
```powershell
java -cp "bin;lib/*" Main
```

---

### 4. Chạy ứng dụng trên IDE

#### Trên Visual Studio Code:
1. Cài đặt extension **Extension Pack for Java**.
2. Mở thư mục dự án `avc` trong VS Code.
3. Đảm bảo file JAR `lib/mssql-jdbc-12.4.2.jre11.jar` đã được nhận diện trong mục **Java Projects > Referenced Libraries**.
4. Mở file `src/Main.java` và nhấn biểu tượng **Run** (hoặc `F5`).

#### Trên IntelliJ IDEA:
1. Chọn **File > Open**, chọn thư mục `avc`.
2. Vào **File > Project Structure > Modules > Dependencies > Add JARs or directories...**, chọn file `lib/mssql-jdbc-12.4.2.jre11.jar`.
3. Chuột phải vào file `src/Main.java` chọn **Run 'Main.main()'**.

#### Trên NetBeans / Eclipse:
1. Tạo một Java Project mới trỏ thư mục nguồn vào `src/`.
2. Add External JAR file `lib/mssql-jdbc-12.4.2.jre11.jar` vào **Build Path / Libraries**.
3. Chọn `Main.java` làm Main Class và tiến hành Run Project.

---

## 🎨 Thiết Kế Giao Diện

- **Bảng màu nhận diện (Palette):**
  - **Màu chủ đạo (Primary Red):** `#BA1C1C` / `#D32F2F` (Màu cờ & logo HaUI).
  - **Màu điểm nhấn (Accent Hover):** `#991B1B` / `#B71C1C`.
  - **Màu nền sáng (Page Background):** `#F8FAFC`.
  - **Màu chữ chính (Heading / Text):** `#0F172A` / `#334155`.
  - **Màu thẻ & phân cách (Border / Card):** `#E2E8F0` / `#FFFFFF`.
- **Thành phần tự thiết kế (Custom UI Components):**
  - `ModernButton`: Nút bấm phẳng, hiệu ứng chuyển màu khi rê chuột (Hover animation), bo góc mượt mà.
  - `CardBox`: Khối hiển thị bo viền tròn, tự động kéo dãn linh hoạt, không bị co giật hay tràn layout.
  - `PagePanel`: Tự động căn chỉnh chiều cao khớp với viewport màn hình, ghim Footer xuống đáy trang.

---

## 👥 Bản Quyền & Tác Giả

- **Đơn vị phát triển:** Nhóm 4 - Sinh viên Khoa Công nghệ Thông tin - Trường Đại học Công nghiệp Hà Nội (HaUI).
- **Mục đích:** Bài tập lớn Lập trình ứng dụng Java Swing & Cơ sở dữ liệu.
