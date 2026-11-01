package model;

import java.util.Date;

public class DangKy {
    private String maDangKy;
    private String maKhachHang;
    private KhoaHoc khoaHoc;
    private String maDonHang;
    private Date ngayDangKy;
    private String trangThai;

    public DangKy() {}
    public DangKy(String maDangKy, String maKhachHang, KhoaHoc khoaHoc,
                  String maDonHang, Date ngayDangKy, String trangThai) {
        this.maDangKy = maDangKy;
        this.maKhachHang = maKhachHang;
        this.khoaHoc = khoaHoc;
        this.maDonHang = maDonHang;
        this.ngayDangKy = ngayDangKy;
        this.trangThai = trangThai;
    }

    public String getMaDangKy() { return maDangKy; }
    public void setMaDangKy(String v) { this.maDangKy = v; }
    public String getMaKhachHang() { return maKhachHang; }
    public void setMaKhachHang(String v) { this.maKhachHang = v; }
    public KhoaHoc getKhoaHoc() { return khoaHoc; }
    public void setKhoaHoc(KhoaHoc v) { this.khoaHoc = v; }
    public String getMaDonHang() { return maDonHang; }
    public void setMaDonHang(String v) { this.maDonHang = v; }
    public Date getNgayDangKy() { return ngayDangKy; }
    public void setNgayDangKy(Date v) { this.ngayDangKy = v; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String v) { this.trangThai = v; }
}
