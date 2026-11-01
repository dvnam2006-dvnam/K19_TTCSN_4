package model;

import java.util.Date;

public class DonHang {
    private String maDonHang;
    private String maKhachHang;
    private String maVoucher;
    private Date ngayDat;
    private long tongTienTamTinh;
    private long soTienGiam;
    private long tongThanhToan;
    private String phuongThucTT;
    private String trangThai;

    public DonHang() {}

    public String getMaDonHang() { return maDonHang; }
    public void setMaDonHang(String v) { this.maDonHang = v; }
    public String getMaKhachHang() { return maKhachHang; }
    public void setMaKhachHang(String v) { this.maKhachHang = v; }
    public String getMaVoucher() { return maVoucher; }
    public void setMaVoucher(String v) { this.maVoucher = v; }
    public Date getNgayDat() { return ngayDat; }
    public void setNgayDat(Date v) { this.ngayDat = v; }
    public long getTongTienTamTinh() { return tongTienTamTinh; }
    public void setTongTienTamTinh(long v) { this.tongTienTamTinh = v; }
    public long getSoTienGiam() { return soTienGiam; }
    public void setSoTienGiam(long v) { this.soTienGiam = v; }
    public long getTongThanhToan() { return tongThanhToan; }
    public void setTongThanhToan(long v) { this.tongThanhToan = v; }
    public String getPhuongThucTT() { return phuongThucTT; }
    public void setPhuongThucTT(String v) { this.phuongThucTT = v; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String v) { this.trangThai = v; }
}
