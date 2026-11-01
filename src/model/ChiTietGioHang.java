package model;

import java.util.Date;

public class ChiTietGioHang {
    private String maGioHang;
    private KhoaHoc khoaHoc;
    private long donGia;
    private Date ngayThem;

    public ChiTietGioHang() {}
    public ChiTietGioHang(String maGioHang, KhoaHoc khoaHoc, long donGia) {
        this.maGioHang = maGioHang;
        this.khoaHoc = khoaHoc;
        this.donGia = donGia;
    }

    public String getMaGioHang() { return maGioHang; }
    public void setMaGioHang(String v) { this.maGioHang = v; }
    public KhoaHoc getKhoaHoc() { return khoaHoc; }
    public void setKhoaHoc(KhoaHoc v) { this.khoaHoc = v; }
    public long getDonGia() { return donGia; }
    public void setDonGia(long v) { this.donGia = v; }
    public Date getNgayThem() { return ngayThem; }
    public void setNgayThem(Date v) { this.ngayThem = v; }
}
