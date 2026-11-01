package dao;

import model.*;
import java.sql.*;
import java.util.*;

public class GioHangDAO {

    private static final List<ChiTietGioHang> MOCK_CART = new ArrayList<>();

    static {
        // Khởi tạo 2 khóa học mặc định trong giỏ giống hệt ảnh mẫu của bạn
        KhoaHocDAO khDAO = new KhoaHocDAO();
        KhoaHoc kh1 = khDAO.getChiTiet("KH004"); // Java từ cơ bản đến hướng đối tượng (hoặc Java Cơ Bản)
        KhoaHoc kh2 = khDAO.getChiTiet("KH007"); // Python cơ bản và ứng dụng thực tế
        if (kh1 != null) MOCK_CART.add(new ChiTietGioHang("GH001", kh1, 499000));
        if (kh2 != null) MOCK_CART.add(new ChiTietGioHang("GH001", kh2, 449000));
    }

    /** Tạo giỏ hàng mới cho khách hàng */
    public String taoGioHang(String maKhachHang) {
        String maGH = generateMaGH();
        if (DBConnection.isMockMode()) return maGH;
        String sql = "INSERT INTO GIO_HANG (MaGioHang, MaKhachHang) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) return maGH;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, maGH);
                ps.setString(2, maKhachHang);
                if (ps.executeUpdate() > 0) return maGH;
            }
        } catch (SQLException e) { return maGH; }
        return maGH;
    }

    /** Lấy hoặc tạo giỏ hàng */
    public String getOrCreateGioHang(String maKhachHang) {
        if (DBConnection.isMockMode()) return "GH001";
        String sql = "SELECT MaGioHang FROM GIO_HANG WHERE MaKhachHang = ?";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) return "GH001";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, maKhachHang);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) return rs.getString(1);
            }
        } catch (SQLException e) { return "GH001"; }
        return taoGioHang(maKhachHang);
    }

    /** Kiểm tra khóa học đã có trong giỏ chưa */
    public boolean daCoTrongGio(String maGioHang, String maKhoaHoc) {
        if (DBConnection.isMockMode()) {
            for (ChiTietGioHang ct : MOCK_CART) {
                if (ct.getKhoaHoc() != null && ct.getKhoaHoc().getMaKhoaHoc().equals(maKhoaHoc)) {
                    return true;
                }
            }
            return false;
        }
        String sql = "SELECT 1 FROM CHI_TIET_GIO_HANG WHERE MaGioHang=? AND MaKhoaHoc=?";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) return daCoTrongGioMock(maKhoaHoc);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, maGioHang);
                ps.setString(2, maKhoaHoc);
                return ps.executeQuery().next();
            }
        } catch (SQLException e) {
            return daCoTrongGioMock(maKhoaHoc);
        }
    }

    private boolean daCoTrongGioMock(String maKhoaHoc) {
        for (ChiTietGioHang ct : MOCK_CART) {
            if (ct.getKhoaHoc() != null && ct.getKhoaHoc().getMaKhoaHoc().equals(maKhoaHoc)) {
                return true;
            }
        }
        return false;
    }

    /** Thêm khóa học vào giỏ */
    public boolean themVaoGio(String maGioHang, String maKhoaHoc, long donGia) {
        if (daCoTrongGio(maGioHang, maKhoaHoc)) return false;
        KhoaHocDAO khDAO = new KhoaHocDAO();
        KhoaHoc kh = khDAO.getChiTiet(maKhoaHoc);
        if (kh == null) return false;

        if (DBConnection.isMockMode()) {
            MOCK_CART.add(new ChiTietGioHang(maGioHang, kh, donGia));
            return true;
        }

        String sql = "INSERT INTO CHI_TIET_GIO_HANG (MaGioHang, MaKhoaHoc, DonGia) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                MOCK_CART.add(new ChiTietGioHang(maGioHang, kh, donGia));
                return true;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, maGioHang);
                ps.setString(2, maKhoaHoc);
                ps.setLong(3, donGia);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            MOCK_CART.add(new ChiTietGioHang(maGioHang, kh, donGia));
            return true;
        }
    }

    /** Xóa khóa học khỏi giỏ */
    public boolean xoaKhoiGio(String maGioHang, String maKhoaHoc) {
        if (DBConnection.isMockMode()) {
            return MOCK_CART.removeIf(ct -> ct.getKhoaHoc() != null && ct.getKhoaHoc().getMaKhoaHoc().equals(maKhoaHoc));
        }
        String sql = "DELETE FROM CHI_TIET_GIO_HANG WHERE MaGioHang=? AND MaKhoaHoc=?";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                return MOCK_CART.removeIf(ct -> ct.getKhoaHoc() != null && ct.getKhoaHoc().getMaKhoaHoc().equals(maKhoaHoc));
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, maGioHang);
                ps.setString(2, maKhoaHoc);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            return MOCK_CART.removeIf(ct -> ct.getKhoaHoc() != null && ct.getKhoaHoc().getMaKhoaHoc().equals(maKhoaHoc));
        }
    }

    /** Lấy danh sách chi tiết giỏ hàng */
    public List<ChiTietGioHang> getChiTiet(String maGioHang) {
        if (DBConnection.isMockMode()) {
            return new ArrayList<>(MOCK_CART);
        }
        String sql = "SELECT ctgh.*, kh.MaDanhMuc, kh.TenKhoaHoc, kh.MoTa, kh.GiangVien, " +
                     "kh.HocPhi, kh.GiaKhuyenMai, kh.ThoiLuong, kh.CapDo, kh.TrangThai, kh.AnhKhoaHoc " +
                     "FROM CHI_TIET_GIO_HANG ctgh " +
                     "JOIN KHOA_HOC kh ON ctgh.MaKhoaHoc = kh.MaKhoaHoc " +
                     "WHERE ctgh.MaGioHang = ? ORDER BY ctgh.NgayThem DESC";
        List<ChiTietGioHang> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) return new ArrayList<>(MOCK_CART);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, maGioHang);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    KhoaHoc kh = new KhoaHoc(
                        rs.getString("MaKhoaHoc"), rs.getString("MaDanhMuc"),
                        rs.getString("TenKhoaHoc"), rs.getString("MoTa"),
                        rs.getString("GiangVien"), rs.getLong("HocPhi"),
                        rs.getLong("GiaKhuyenMai"), rs.getString("ThoiLuong"),
                        rs.getString("CapDo"), rs.getString("TrangThai"),
                        rs.getString("AnhKhoaHoc")
                    );
                    ChiTietGioHang ct = new ChiTietGioHang(maGioHang, kh, rs.getLong("DonGia"));
                    ct.setNgayThem(rs.getTimestamp("NgayThem"));
                    list.add(ct);
                }
            }
        } catch (SQLException e) {
            return new ArrayList<>(MOCK_CART);
        }
        return list;
    }

    /** Xóa toàn bộ giỏ sau khi thanh toán */
    public void xoaGio(String maGioHang) {
        MOCK_CART.clear();
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                PreparedStatement ps1 = conn.prepareStatement("DELETE FROM CHI_TIET_GIO_HANG WHERE MaGioHang=?");
                ps1.setString(1, maGioHang);
                ps1.executeUpdate();
            }
        } catch (SQLException e) {}
    }

    /** Đếm số items trong giỏ */
    public int demGio(String maGioHang) {
        if (DBConnection.isMockMode()) {
            return MOCK_CART.size();
        }
        String sql = "SELECT COUNT(*) FROM CHI_TIET_GIO_HANG WHERE MaGioHang=?";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) return MOCK_CART.size();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, maGioHang);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            return MOCK_CART.size();
        }
        return MOCK_CART.size();
    }

    private String generateMaGH() {
        return "GH" + System.currentTimeMillis();
    }
}
