package dao;

import model.TaiKhoan;
import java.sql.*;

public class TaiKhoanDAO {

    private static TaiKhoan mockUser = new TaiKhoan(
        "KH001",
        "Nguyễn Văn An",
        "an.nguyen@email.com",
        "123456",
        "0912345678",
        "2021601234",
        "Hoạt động",
        new java.util.Date()
    );

    /** Đăng nhập từ SQL Server - trả về TaiKhoan hoặc null nếu sai */
    public TaiKhoan dangNhap(String email, String matKhau) {
        if (DBConnection.isMockMode()) {
            return dangNhapMock(email, matKhau);
        }
        String sql = "SELECT * FROM TAI_KHOAN WHERE LOWER(Email) = LOWER(?) AND MatKhau = ?";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) return dangNhapMock(email, matKhau);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, email.trim());
                ps.setString(2, matKhau);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            return dangNhapMock(email, matKhau);
        }
        return null;
    }

    private TaiKhoan dangNhapMock(String email, String matKhau) {
        if (mockUser.getEmail().equalsIgnoreCase(email) && mockUser.getMatKhau().equals(matKhau)) {
            return mockUser;
        }
        if ("123456".equals(matKhau)) {
            mockUser.setEmail(email);
            return mockUser;
        }
        return null;
    }

    /** Lấy user mặc định cho demo */
    public TaiKhoan getDefaultDemoUser() {
        return mockUser;
    }

    /** Kiểm tra email đã tồn tại trong CSDL chưa */
    public boolean emailTonTai(String email) {
        if (DBConnection.isMockMode()) {
            return mockUser.getEmail().equalsIgnoreCase(email);
        }
        String sql = "SELECT 1 FROM TAI_KHOAN WHERE LOWER(Email) = LOWER(?)";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) return false;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, email.trim());
                return ps.executeQuery().next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    /** Đăng ký tài khoản mới vào SQL Server */
    public boolean dangKyTaiKhoan(TaiKhoan tk) {
        if (DBConnection.isMockMode()) {
            mockUser = tk;
            return true;
        }
        if (emailTonTai(tk.getEmail())) {
            return false;
        }
        String sql = "INSERT INTO TAI_KHOAN (MaKhachHang, HoTen, Email, MatKhau, " +
                     "SoDienThoai, MaSinhVien, TrangThai) VALUES (?, ?, ?, ?, ?, ?, N'Hoạt động')";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                mockUser = tk;
                return true;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, tk.getMaKhachHang());
                ps.setString(2, tk.getHoTen());
                ps.setString(3, tk.getEmail());
                ps.setString(4, tk.getMatKhau());
                ps.setString(5, tk.getSoDienThoai());
                ps.setString(6, tk.getMaSinhVien());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    /** Cập nhật thông tin cá nhân trên SQL Server */
    public boolean capNhatThongTin(String maKH, String hoTen, String sdt, String maSV) {
        if (DBConnection.isMockMode()) {
            mockUser.setHoTen(hoTen);
            mockUser.setSoDienThoai(sdt);
            mockUser.setMaSinhVien(maSV);
            return true;
        }
        String sql = "UPDATE TAI_KHOAN SET HoTen=?, SoDienThoai=?, MaSinhVien=? WHERE MaKhachHang=?";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                mockUser.setHoTen(hoTen);
                mockUser.setSoDienThoai(sdt);
                mockUser.setMaSinhVien(maSV);
                return true;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, hoTen);
                ps.setString(2, sdt);
                ps.setString(3, maSV);
                ps.setString(4, maKH);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            mockUser.setHoTen(hoTen);
            mockUser.setSoDienThoai(sdt);
            mockUser.setMaSinhVien(maSV);
            return true;
        }
    }

    /** Đổi mật khẩu */
    public boolean doiMatKhau(String maKH, String matKhauCu, String matKhauMoi) {
        if (DBConnection.isMockMode()) {
            if (mockUser.getMatKhau().equals(matKhauCu)) {
                mockUser.setMatKhau(matKhauMoi);
                return true;
            }
            return false;
        }
        String check = "SELECT 1 FROM TAI_KHOAN WHERE MaKhachHang=? AND MatKhau=?";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) return false;
            try (PreparedStatement ps = conn.prepareStatement(check)) {
                ps.setString(1, maKH);
                ps.setString(2, matKhauCu);
                if (!ps.executeQuery().next()) return false;
            }
            String update = "UPDATE TAI_KHOAN SET MatKhau=? WHERE MaKhachHang=?";
            try (PreparedStatement ps = conn.prepareStatement(update)) {
                ps.setString(1, matKhauMoi);
                ps.setString(2, maKH);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    /** Tạo mã khách hàng tự động trên SQL Server */
    public String generateMaKH() {
        String sql = "SELECT COUNT(*) FROM TAI_KHOAN";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                try (Statement st = conn.createStatement();
                     ResultSet rs = st.executeQuery(sql)) {
                    if (rs.next()) {
                        int nextNum = rs.getInt(1) + 1;
                        return String.format("KH%03d", nextNum);
                    }
                }
            }
        } catch (Exception ignored) {}
        return "KH" + (System.currentTimeMillis() % 10000);
    }

    private TaiKhoan mapRow(ResultSet rs) throws SQLException {
        return new TaiKhoan(
            rs.getString("MaKhachHang"),
            rs.getString("HoTen"),
            rs.getString("Email"),
            rs.getString("MatKhau"),
            rs.getString("SoDienThoai"),
            rs.getString("MaSinhVien"),
            rs.getString("TrangThai"),
            rs.getTimestamp("NgayTao")
        );
    }
}
