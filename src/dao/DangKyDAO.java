package dao;

import model.*;
import java.sql.*;
import java.util.*;

public class DangKyDAO {

    private static final List<DangKy> MOCK_DANG_KY = new ArrayList<>();

    static {
        KhoaHocDAO khDAO = new KhoaHocDAO();
        KhoaHoc kh = khDAO.getChiTiet("KH004");
        if (kh != null) {
            MOCK_DANG_KY.add(new DangKy("DK001", "KH001", kh, "DH001", new java.util.Date(), "Đã thanh toán"));
        }
    }

    /** Kiểm tra khách hàng đã đăng ký khóa học này chưa */
    public boolean daDangKy(String maKH, String maKhoaHoc) {
        if (DBConnection.isMockMode()) {
            for (DangKy dk : MOCK_DANG_KY) {
                if (dk.getMaKhachHang().equals(maKH) && 
                    dk.getKhoaHoc() != null && 
                    dk.getKhoaHoc().getMaKhoaHoc().equals(maKhoaHoc)) {
                    return true;
                }
            }
            return false;
        }
        String sql = "SELECT 1 FROM DANG_KY WHERE MaKhachHang=? AND MaKhoaHoc=? " +
                     "AND TrangThai=N'Đã thanh toán'";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) return daDangKyMock(maKH, maKhoaHoc);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, maKH);
                ps.setString(2, maKhoaHoc);
                return ps.executeQuery().next();
            }
        } catch (SQLException e) {
            return daDangKyMock(maKH, maKhoaHoc);
        }
    }

    private boolean daDangKyMock(String maKH, String maKhoaHoc) {
        for (DangKy dk : MOCK_DANG_KY) {
            if (dk.getMaKhachHang().equals(maKH) && 
                dk.getKhoaHoc() != null && 
                dk.getKhoaHoc().getMaKhoaHoc().equals(maKhoaHoc)) {
                return true;
            }
        }
        return false;
    }

    /** Đăng ký khóa học mới (chức năng 2.1 tuần 6) */
    public boolean dangKyKhoaHoc(String maKH, String maKhoaHoc) {
        if (daDangKy(maKH, maKhoaHoc)) {
            return false;
        }
        String maDangKy = "DK" + System.currentTimeMillis() % 100000;
        KhoaHocDAO khDAO = new KhoaHocDAO();
        KhoaHoc kh = khDAO.getChiTiet(maKhoaHoc);

        if (DBConnection.isMockMode()) {
            MOCK_DANG_KY.add(0, new DangKy(maDangKy, maKH, kh, "DH" + (System.currentTimeMillis() % 10000), new java.util.Date(), "Đã thanh toán"));
            return true;
        }

        String sql = "INSERT INTO DANG_KY (MaDangKy, MaKhachHang, MaKhoaHoc, NgayDangKy, TrangThai) " +
                     "VALUES (?, ?, ?, GETDATE(), N'Đã thanh toán')";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                MOCK_DANG_KY.add(0, new DangKy(maDangKy, maKH, kh, "DH001", new java.util.Date(), "Đã thanh toán"));
                return true;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, maDangKy);
                ps.setString(2, maKH);
                ps.setString(3, maKhoaHoc);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            MOCK_DANG_KY.add(0, new DangKy(maDangKy, maKH, kh, "DH001", new java.util.Date(), "Đã thanh toán"));
            return true;
        }
    }

    /** Lấy danh sách khóa học đã đăng ký (chức năng 2.2 tuần 6) */
    public List<DangKy> getDanhSachDaDangKy(String maKH) {
        if (DBConnection.isMockMode()) {
            List<DangKy> list = new ArrayList<>();
            for (DangKy dk : MOCK_DANG_KY) {
                if (dk.getMaKhachHang().equals(maKH)) {
                    list.add(dk);
                }
            }
            return list;
        }
        String sql = "SELECT dk.MaDangKy, dk.MaKhachHang, dk.MaDonHang, dk.NgayDangKy, dk.TrangThai, " +
                     "kh.MaKhoaHoc, kh.MaDanhMuc, kh.TenKhoaHoc, kh.MoTa, kh.GiangVien, " +
                     "kh.HocPhi, kh.GiaKhuyenMai, kh.ThoiLuong, kh.CapDo, kh.TrangThai AS TTKhoaHoc, kh.AnhKhoaHoc " +
                     "FROM DANG_KY dk JOIN KHOA_HOC kh ON dk.MaKhoaHoc=kh.MaKhoaHoc " +
                     "WHERE dk.MaKhachHang=? ORDER BY dk.NgayDangKy DESC";
        List<DangKy> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) return getDanhSachMock(maKH);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, maKH);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    KhoaHoc kh = new KhoaHoc(
                        rs.getString("MaKhoaHoc"), rs.getString("MaDanhMuc"),
                        rs.getString("TenKhoaHoc"), rs.getString("MoTa"),
                        rs.getString("GiangVien"), rs.getLong("HocPhi"),
                        rs.getLong("GiaKhuyenMai"), rs.getString("ThoiLuong"),
                        rs.getString("CapDo"), rs.getString("TTKhoaHoc"),
                        rs.getString("AnhKhoaHoc")
                    );
                    DangKy dk = new DangKy(
                        rs.getString("MaDangKy"), rs.getString("MaKhachHang"),
                        kh, rs.getString("MaDonHang"),
                        rs.getTimestamp("NgayDangKy"), rs.getString("TrangThai")
                    );
                    list.add(dk);
                }
            }
        } catch (SQLException e) {
            return getDanhSachMock(maKH);
        }
        return list;
    }

    private List<DangKy> getDanhSachMock(String maKH) {
        List<DangKy> list = new ArrayList<>();
        for (DangKy dk : MOCK_DANG_KY) {
            if (dk.getMaKhachHang().equals(maKH)) {
                list.add(dk);
            }
        }
        return list;
    }
}
