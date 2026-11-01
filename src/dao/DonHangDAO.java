package dao;

import model.*;
import java.sql.*;
import java.util.*;

public class DonHangDAO {

    /** Kiểm tra voucher hợp lệ - trả về % giảm hoặc -1 nếu không hợp lệ */
    public int kiemTraVoucher(String maVoucher) {
        String sql = "SELECT PhanTramGiam, NgayHetHan, TrangThai FROM MA_GIAM_GIA WHERE MaVoucher=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maVoucher);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String trangThai = rs.getString("TrangThai");
                java.util.Date hetHan = rs.getDate("NgayHetHan");
                if ("Hoạt động".equals(trangThai) && hetHan != null && hetHan.after(new java.util.Date())) {
                    return rs.getInt("PhanTramGiam");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /** Tạo đơn hàng mới và chi tiết, xóa giỏ hàng - trả về mã đơn hàng hoặc null */
    public String taoDonHang(String maKH, String maVoucher, List<ChiTietGioHang> items,
            long tongTamTinh, long soTienGiam, long tongThanhToan,
            String phuongThuc, String maGioHang) {
        String maDH = "DH" + System.currentTimeMillis();
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Tạo đơn hàng
            String sqlDH = "INSERT INTO DON_HANG (MaDonHang, MaKhachHang, MaVoucher, " +
                    "TongTienTamTinh, SoTienGiam, TongThanhToan, PhuongThucTT, TrangThai) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, N'Đã thanh toán')";
            PreparedStatement ps1 = conn.prepareStatement(sqlDH);
            ps1.setString(1, maDH);
            ps1.setString(2, maKH);
            ps1.setString(3, (maVoucher == null || maVoucher.isEmpty()) ? null : maVoucher);
            ps1.setLong(4, tongTamTinh);
            ps1.setLong(5, soTienGiam);
            ps1.setLong(6, tongThanhToan);
            ps1.setString(7, phuongThuc);
            ps1.executeUpdate();

            // 2. Chi tiết đơn hàng + đăng ký khóa học
            String sqlCT = "INSERT INTO CHI_TIET_DON_HANG (MaDonHang, MaKhoaHoc, GiaMua) VALUES (?, ?, ?)";
            String sqlDK = "INSERT INTO DANG_KY (MaDangKy, MaKhachHang, MaKhoaHoc, MaDonHang, TrangThai) " +
                    "VALUES (?, ?, ?, ?, N'Đã thanh toán')";
            int idx = 1;
            for (ChiTietGioHang item : items) {
                PreparedStatement ps2 = conn.prepareStatement(sqlCT);
                ps2.setString(1, maDH);
                ps2.setString(2, item.getKhoaHoc().getMaKhoaHoc());
                ps2.setLong(3, item.getDonGia());
                ps2.executeUpdate();

                PreparedStatement ps3 = conn.prepareStatement(sqlDK);
                ps3.setString(1, String.format("DK%s%02d", System.currentTimeMillis(), idx++));
                ps3.setString(2, maKH);
                ps3.setString(3, item.getKhoaHoc().getMaKhoaHoc());
                ps3.setString(4, maDH);
                ps3.executeUpdate();
            }

            // 3. Xóa chi tiết giỏ hàng
            PreparedStatement ps4 = conn.prepareStatement(
                    "DELETE FROM CHI_TIET_GIO_HANG WHERE MaGioHang=?");
            ps4.setString(1, maGioHang);
            ps4.executeUpdate();

            conn.commit();
            return maDH;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (conn != null)
                    conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
