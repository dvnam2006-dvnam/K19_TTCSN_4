package dao;

import model.KhoaHoc;
import java.sql.*;
import java.util.*;

public class KhoaHocDAO {

    private static final List<KhoaHoc> MOCK_COURSES = new ArrayList<>();

    static {
        MOCK_COURSES.add(new KhoaHoc("KH001", "Java", "Java từ cơ bản đến hướng đối tượng",
            "Khóa học giúp bạn xây dựng nền tảng lập trình Java vững chắc, từ cú pháp cơ bản đến tư duy hướng đối tượng. Mỗi chương kết hợp video hướng dẫn, ví dụ minh họa và bài tập để bạn áp dụng ngay kiến thức đã học.\n\nỞ cuối khóa học, bạn sẽ tự xây dựng ứng dụng Quản lý sinh viên bằng Java, tổ chức mã nguồn theo lớp và xử lý dữ liệu đầu vào.",
            "Nguyễn Minh Tuấn", 499000, 0, "42 bài học • 28 giờ", "Cơ bản", "Đang mở", "Java"));

        MOCK_COURSES.add(new KhoaHoc("KH002", "C++", "Lập trình C++ cho người mới bắt đầu",
            "Khóa học C++ từ đầu: biến, kiểu dữ liệu, hàm, con trỏ, mảng và cấu trúc dữ liệu cơ bản. Rèn luyện tư duy thuật toán vững vàng.",
            "Trần Hoàng Nam", 399000, 0, "36 bài học • 24 giờ", "Cơ bản", "Đang mở", "C++"));

        MOCK_COURSES.add(new KhoaHoc("KH003", "Python", "Python cơ bản và ứng dụng thực tế",
            "Lập trình Python hiện đại từ số 0, thao tác dữ liệu, xử lý file, tự động hóa tác vụ văn phòng và làm quen với thư viện phân tích dữ liệu.",
            "Lê Thu Hà", 449000, 0, "40 bài học • 26 giờ", "Cơ bản", "Đang mở", "Python"));

        MOCK_COURSES.add(new KhoaHoc("KH004", "HTML/CSS", "Thiết kế website với HTML và CSS",
            "Xây dựng giao diện web chuẩn responsive, Flexbox, CSS Grid, animation mượt mà, tối ưu hiển thị trên mọi thiết bị.",
            "Phạm Quang Huy", 349000, 0, "32 bài học • 20 giờ", "Cơ bản", "Đang mở", "HTML/CSS"));

        MOCK_COURSES.add(new KhoaHoc("KH005", "Spring Boot", "Xây dựng REST API với Spring Boot",
            "Phát triển ứng dụng Backend chuyên nghiệp với Spring Boot, Spring Data JPA, JWT Authentication, Swagger và SQL Server.",
            "Nguyễn Minh Tuấn", 699000, 0, "48 bài học • 35 giờ", "Trung cấp", "Đang mở", "Spring Boot"));

        MOCK_COURSES.add(new KhoaHoc("KH006", "C++", "Cấu trúc dữ liệu và giải thuật C++",
            "Nắm vững danh sách liên kết, ngăn xếp (Stack), hàng đợi (Queue), cây nhị phân (Binary Tree), đồ thị và các giải thuật tìm kiếm, sắp xếp tối ưu.",
            "Trần Hoàng Nam", 599000, 0, "45 bài học • 30 giờ", "Trung cấp", "Đang mở", "C++"));

        MOCK_COURSES.add(new KhoaHoc("KH007", "Java", "Lập trình Java Swing Desktop App",
            "Xây dựng phần mềm quản lý trên máy tính với Java Swing, kết nối CSDL SQL Server qua JDBC, thiết kế UI hiện đại.",
            "Phạm Thị D", 550000, 0, "35 bài học • 25 giờ", "Trung cấp", "Đang mở", "Java"));

        MOCK_COURSES.add(new KhoaHoc("KH008", "Python", "Machine Learning căn bản với Python",
            "Nhập môn Trí tuệ nhân tạo & Máy học: Hồi quy tuyến tính, Phân loại, Gom cụm dữ liệu với NumPy, Pandas, Scikit-Learn.",
            "Bùi Thị H", 799000, 0, "50 bài học • 40 giờ", "Nâng cao", "Đang mở", "Python"));

        MOCK_COURSES.add(new KhoaHoc("KH009", "HTML/CSS", "JavaScript Toàn Tập & Thực Chiến",
            "Làm chủ JavaScript từ cơ bản đến nâng cao: ES6+, DOM manipulation, Event loop, Async/Await, Fetch API.",
            "Trần Thị B", 520000, 0, "38 bài học • 28 giờ", "Cơ bản", "Đang mở", "HTML/CSS"));

        MOCK_COURSES.add(new KhoaHoc("KH010", "Spring Boot", "Microservices Architecture với Spring Cloud",
            "Xây dựng hệ thống phân tán chịu tải cao: Service Discovery, API Gateway, Circuit Breaker, Config Server.",
            "Vũ Thị F", 950000, 0, "55 bài học • 45 giờ", "Nâng cao", "Đang mở", "Spring Boot"));
    }

    /** Lấy tất cả khóa học đang mở từ SQL Server */
    public List<KhoaHoc> getDanhSach() {
        if (DBConnection.isMockMode()) {
            return new ArrayList<>(MOCK_COURSES);
        }
        List<KhoaHoc> res = query("SELECT * FROM KHOA_HOC WHERE TrangThai = N'Đang mở' ORDER BY MaKhoaHoc ASC", null);
        return res.isEmpty() ? new ArrayList<>(MOCK_COURSES) : res;
    }

    /** Lấy khóa học theo danh mục từ SQL Server */
    public List<KhoaHoc> getTheoDanhMuc(String maDanhMuc) {
        if (maDanhMuc == null || maDanhMuc.equalsIgnoreCase("Tất cả")) {
            return getDanhSach();
        }
        if (!DBConnection.isMockMode()) {
            List<KhoaHoc> dbRes = query(
                "SELECT * FROM KHOA_HOC WHERE TrangThai = N'Đang mở' AND (MaDanhMuc = ? OR TenKhoaHoc LIKE ?) ORDER BY MaKhoaHoc ASC",
                ps -> {
                    ps.setString(1, maDanhMuc);
                    ps.setString(2, "%" + maDanhMuc + "%");
                }
            );
            if (!dbRes.isEmpty()) return dbRes;
        }
        List<KhoaHoc> filtered = new ArrayList<>();
        for (KhoaHoc kh : getDanhSach()) {
            if (kh.getMaDanhMuc() != null && kh.getMaDanhMuc().toLowerCase().contains(maDanhMuc.toLowerCase())) {
                filtered.add(kh);
            }
        }
        return filtered;
    }

    /** Tìm kiếm khóa học theo từ khóa trên SQL Server */
    public List<KhoaHoc> timKiem(String tuKhoa) {
        if (tuKhoa == null || tuKhoa.trim().isEmpty()) return getDanhSach();
        String kw = tuKhoa.trim();
        if (!DBConnection.isMockMode()) {
            List<KhoaHoc> dbRes = query(
                "SELECT * FROM KHOA_HOC WHERE TrangThai = N'Đang mở' AND (TenKhoaHoc LIKE ? OR GiangVien LIKE ? OR MaDanhMuc LIKE ?) ORDER BY MaKhoaHoc ASC",
                ps -> {
                    String pattern = "%" + kw + "%";
                    ps.setString(1, pattern);
                    ps.setString(2, pattern);
                    ps.setString(3, pattern);
                }
            );
            return dbRes;
        }
        String lower = kw.toLowerCase();
        List<KhoaHoc> matched = new ArrayList<>();
        for (KhoaHoc kh : MOCK_COURSES) {
            if ((kh.getTenKhoaHoc() != null && kh.getTenKhoaHoc().toLowerCase().contains(lower)) ||
                (kh.getGiangVien() != null && kh.getGiangVien().toLowerCase().contains(lower)) ||
                (kh.getMaDanhMuc() != null && kh.getMaDanhMuc().toLowerCase().contains(lower))) {
                matched.add(kh);
            }
        }
        return matched;
    }

    /** Lấy chi tiết 1 khóa học theo mã từ SQL Server */
    public KhoaHoc getChiTiet(String maKhoaHoc) {
        if (!DBConnection.isMockMode()) {
            List<KhoaHoc> dbRes = query("SELECT * FROM KHOA_HOC WHERE MaKhoaHoc = ?", ps -> ps.setString(1, maKhoaHoc));
            if (!dbRes.isEmpty()) return dbRes.get(0);
        }
        for (KhoaHoc kh : MOCK_COURSES) {
            if (kh.getMaKhoaHoc().equalsIgnoreCase(maKhoaHoc)) return kh;
        }
        return MOCK_COURSES.get(0);
    }

    // ====== Utility ======
    interface PSetter { void set(PreparedStatement ps) throws SQLException; }

    private List<KhoaHoc> query(String sql, PSetter setter) {
        List<KhoaHoc> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) return list;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                if (setter != null) setter.set(ps);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException ignored) {}
        return list;
    }

    private KhoaHoc mapRow(ResultSet rs) throws SQLException {
        return new KhoaHoc(
            rs.getString("MaKhoaHoc"),
            rs.getString("MaDanhMuc"),
            rs.getString("TenKhoaHoc"),
            rs.getString("MoTa"),
            rs.getString("GiangVien"),
            rs.getLong("HocPhi"),
            rs.getLong("GiaKhuyenMai"),
            rs.getString("ThoiLuong"),
            rs.getString("CapDo"),
            rs.getString("TrangThai"),
            rs.getString("AnhKhoaHoc")
        );
    }
}
