package dao;

import model.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DonHangDAO {
    private static final AtomicInteger ID_SEQUENCE = new AtomicInteger();

    /** Kiểm tra voucher hợp lệ - trả về % giảm hoặc -1 nếu không hợp lệ */
    public int kiemTraVoucher(String maVoucher) {
        if (maVoucher == null || maVoucher.trim().isEmpty()) return -1;
        if (DBConnection.isMockMode()) {
            LocalDate today = LocalDate.now();
            if ("HAUI10".equalsIgnoreCase(maVoucher) && !today.isAfter(LocalDate.of(2028, 12, 31))) return 10;
            if ("HAUI20".equalsIgnoreCase(maVoucher) && !today.isAfter(LocalDate.of(2028, 12, 31))) return 20;
            if ("HAUI50".equalsIgnoreCase(maVoucher) && !today.isAfter(LocalDate.of(2028, 6, 30))) return 50;
            return -1;
        }

        String sql = "SELECT PhanTramGiam, NgayHetHan, TrangThai FROM MA_GIAM_GIA WHERE MaVoucher=?";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) return kiemTraVoucher(maVoucher);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, maVoucher);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String trangThai = rs.getString("TrangThai");
                        java.sql.Date hetHan = rs.getDate("NgayHetHan");
                        if ("Hoạt động".equals(trangThai) && hetHan != null
                                && !hetHan.toLocalDate().isBefore(LocalDate.now())) {
                            return rs.getInt("PhanTramGiam");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            if (DBConnection.isMockMode()) return kiemTraVoucher(maVoucher);
            e.printStackTrace();
        }
        return -1;
    }

    /** Tạo đơn hàng mới và chi tiết, xóa giỏ hàng - trả về mã đơn hàng hoặc null */
    public String taoDonHang(String maKH, String maVoucher, List<ChiTietGioHang> items,
            long tongTamTinh, long soTienGiam, long tongThanhToan,
            String phuongThuc, String maGioHang) {
        if (maKH == null || maKH.trim().isEmpty() || maGioHang == null
                || maGioHang.trim().isEmpty() || items == null || items.isEmpty()) {
            return null;
        }
        for (ChiTietGioHang item : items) {
            if (item == null || item.getKhoaHoc() == null
                    || item.getKhoaHoc().getMaKhoaHoc() == null) {
                return null;
            }
        }

        String maDH = taoMaDonHang();
        if (DBConnection.isMockMode()) {
            DangKyDAO dangKyDAO = new DangKyDAO();
            int idx = 1;
            for (ChiTietGioHang item : items) {
                dangKyDAO.ghiNhanThanhToanMock(maKH, item.getKhoaHoc(), maDH, taoMaDangKy(maDH, idx++));
            }
            new GioHangDAO().xoaGio(maGioHang);
            return maDH;
        }

        String sqlDH = "INSERT INTO DON_HANG (MaDonHang, MaKhachHang, MaVoucher, " +
                "TongTienTamTinh, SoTienGiam, TongThanhToan, PhuongThucTT, TrangThai) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, N'Đã thanh toán')";
        String sqlCT = "INSERT INTO CHI_TIET_DON_HANG (MaDonHang, MaKhoaHoc, GiaMua) VALUES (?, ?, ?)";
        String sqlDK = "INSERT INTO DANG_KY (MaDangKy, MaKhachHang, MaKhoaHoc, MaDonHang, TrangThai) " +
                "VALUES (?, ?, ?, ?, N'Đã thanh toán')";
        String sqlDeleteCart = "DELETE FROM CHI_TIET_GIO_HANG WHERE MaGioHang=?";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) return null;
            boolean previousAutoCommit = conn.getAutoCommit();
            try {
                conn.setAutoCommit(false);
                try (PreparedStatement psDH = conn.prepareStatement(sqlDH);
                        PreparedStatement psCT = conn.prepareStatement(sqlCT);
                        PreparedStatement psDK = conn.prepareStatement(sqlDK);
                        PreparedStatement psDeleteCart = conn.prepareStatement(sqlDeleteCart)) {
                    psDH.setString(1, maDH);
                    psDH.setString(2, maKH);
                    psDH.setString(3, maVoucher == null || maVoucher.isEmpty() ? null : maVoucher);
                    psDH.setLong(4, tongTamTinh);
                    psDH.setLong(5, soTienGiam);
                    psDH.setLong(6, tongThanhToan);
                    psDH.setString(7, phuongThuc);
                    psDH.executeUpdate();

                    int idx = 1;
                    for (ChiTietGioHang item : items) {
                        psCT.setString(1, maDH);
                        psCT.setString(2, item.getKhoaHoc().getMaKhoaHoc());
                        psCT.setLong(3, item.getDonGia());
                        psCT.executeUpdate();

                        psDK.setString(1, taoMaDangKy(maDH, idx++));
                        psDK.setString(2, maKH);
                        psDK.setString(3, item.getKhoaHoc().getMaKhoaHoc());
                        psDK.setString(4, maDH);
                        psDK.executeUpdate();
                    }

                    psDeleteCart.setString(1, maGioHang);
                    psDeleteCart.executeUpdate();
                }
                conn.commit();
                return maDH;
            } catch (SQLException e) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }
                throw e;
            } finally {
                try {
                    conn.setAutoCommit(previousAutoCommit);
                } catch (SQLException resetError) {
                    resetError.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String taoMaDonHang() {
        return "DH" + System.currentTimeMillis() + String.format("%03d", ID_SEQUENCE.getAndIncrement() % 1000);
    }

    private static String taoMaDangKy(String maDonHang, int index) {
        return "DK" + maDonHang.substring(2) + String.format("%02d", index);
    }
}
